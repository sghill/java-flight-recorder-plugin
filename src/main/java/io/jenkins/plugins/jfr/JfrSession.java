package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

public record JfrSession(
        @JsonProperty @NonNull String name, @JsonProperty long id) {}
