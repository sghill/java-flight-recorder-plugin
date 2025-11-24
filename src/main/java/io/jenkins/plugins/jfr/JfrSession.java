package io.jenkins.plugins.jfr;

import java.time.Instant;
import java.util.Map;
import org.jspecify.annotations.NonNull;

public record JfrSession(
        @NonNull String name,
        @NonNull Instant startTime,
        @NonNull String maxSize,
        @NonNull String duration,
        @NonNull Map<String, String> settings) {}
