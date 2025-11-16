package io.jenkins.plugins.jfr;

import jakarta.inject.Named;
import java.util.Collection;
import java.util.stream.Collectors;
import jdk.jfr.FlightRecorder;
import jdk.jfr.Recording;

@Named
public class JfrServiceImpl implements JfrService {

    @Override
    public Collection<JfrSession> getSessions() {
        return FlightRecorder.getFlightRecorder().getRecordings().stream()
                .map(this::toJfrSession)
                .collect(Collectors.toList());
    }

    private JfrSession toJfrSession(Recording recording) {
        return new JfrSession(recording.getName(), recording.getId());
    }
}
