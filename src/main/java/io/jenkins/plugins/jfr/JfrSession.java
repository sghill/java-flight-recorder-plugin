package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.time.Instant;
import java.util.Map;

public record JfrSession(
        @JsonProperty @NonNull String name,
        @JsonProperty @NonNull Instant startTime,
        @JsonProperty @NonNull String maxSize,
        @JsonProperty @NonNull String duration,
        @JsonProperty @NonNull Map<String, String> settings) {}
