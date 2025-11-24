package io.jenkins.plugins.jfr;

import edu.umd.cs.findbugs.annotations.NonNull;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import jdk.jfr.FlightRecorder;
import jdk.jfr.Recording;

@Singleton
public class JfrService {

    @NonNull
    public List<JfrSession> getSessions() {
        return FlightRecorder.getFlightRecorder().getRecordings().stream()
                .map(JfrService::toJfrSession)
                .collect(Collectors.toList());
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
