package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

public record DumpRequest(
        @NonNull @JsonProperty String name,
        @NonNull @JsonProperty String dir) {}
