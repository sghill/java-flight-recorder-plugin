package io.jenkins.plugins.jfr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jdk.jfr.FlightRecorder;
import jdk.jfr.Recording;
import org.jspecify.annotations.NonNull;

class DefaultJfrService implements JfrService {

    @Override
    @NonNull
    public List<JfrSession> getSessions() {
        return FlightRecorder.getFlightRecorder().getRecordings().stream()
                .map(DefaultJfrService::toJfrSession)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public DumpResponse dump(DumpRequest request) throws IOException {
        Optional<Recording> recording = FlightRecorder.getFlightRecorder().getRecordings().stream()
                .filter(r -> r.getName().equals(request.name()))
                .findFirst();
        if (recording.isPresent()) {
            Path outputDir = resolveOutputDirectory();
            Files.createDirectories(outputDir);
            Path path = Files.createTempFile(outputDir, "jenkins-jvm-", ".jfr");
            recording.get().dump(path);
            enforceMaxDumps(outputDir);
            return new DumpResponse(path.toString());
        }
        throw new NoSuchElementException("Recording not found");
    }

    @Override
    @NonNull
    public JfrSession start(StartRecordingRequest request) throws IOException {
        Recording recording = new Recording();
        recording.setDuration(Duration.ofSeconds(request.durationInSeconds()));
        recording.start();
        return toJfrSession(recording);
    }

    @NonNull
    private static Path resolveOutputDirectory() {
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();
        String outputDirectory = config.getOutputDirectory();
        if (outputDirectory != null && !outputDirectory.isBlank()) {
            return Path.of(outputDirectory);
        }
        return Path.of(System.getProperty("java.io.tmpdir"));
    }

    private static void enforceMaxDumps(@NonNull Path outputDir) throws IOException {
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();
        int maxDumps = config.getMaxDumps();
        try (Stream<Path> files = Files.list(outputDir)) {
            List<Path> jfrFiles = files.filter(p -> p.toString().endsWith(".jfr"))
                    .filter(p -> p.getFileName().toString().startsWith("jenkins-jvm-"))
                    .sorted(Comparator.comparingLong(p -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis();
                        } catch (IOException e) {
                            return 0L;
                        }
                    }))
                    .collect(Collectors.toList());
            while (jfrFiles.size() > maxDumps) {
                Files.deleteIfExists(jfrFiles.remove(0));
            }
        }
    }

    @NonNull
    private static JfrSession toJfrSession(@NonNull Recording recording) {
        return new JfrSession(
                recording.getName(),
                recording.getStartTime(),
                String.valueOf(recording.getMaxSize()),
                recording.getDuration() != null ? recording.getDuration().toString() : "",
                recording.getSettings());
    }
}
