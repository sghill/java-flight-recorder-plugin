package io.jenkins.plugins.jfr;

import java.util.List;
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
