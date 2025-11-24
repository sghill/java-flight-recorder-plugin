package io.jenkins.plugins.jfr;

import org.jspecify.annotations.NonNull;

public record JfrGetRecordingResponse(@NonNull JfrSession session) {}
