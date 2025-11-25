package io.jenkins.plugins.jfr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
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
            Path path = Files.createTempFile("jenkins-jvm-", ".jfr");
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
