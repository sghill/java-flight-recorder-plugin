package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

public record DumpResponse(@NonNull @JsonProperty String path) {}
