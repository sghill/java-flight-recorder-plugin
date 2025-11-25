package io.jenkins.plugins.jfr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jdk.jfr.FlightRecorder;
import jdk.jfr.Recording;
import jenkins.model.Jenkins;
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
            Path baseDir = Paths.get(
                    Objects.requireNonNull(Jenkins.get(), "Jenkins instance is null")
                            .getRootDir()
                            .getAbsolutePath(),
                    "jfr-dumps");
            Path dumpDir = baseDir.resolve(request.dir()).normalize();
            if (!dumpDir.startsWith(baseDir)) {
                throw new IllegalArgumentException("Invalid directory specified");
            }
            Files.createDirectories(dumpDir);
            Path namePath = Paths.get(request.name());
            Path fileName = namePath.getFileName();
            if (fileName == null) {
                throw new IOException("Invalid recording name provided: " + request.name());
            }
            String sanitizedName = fileName.toString();
            Path path = dumpDir.resolve(sanitizedName);
            recording.get().dump(path);
            return new DumpResponse(path.toString());
        }
        throw new NoSuchElementException("Recording not found");
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
