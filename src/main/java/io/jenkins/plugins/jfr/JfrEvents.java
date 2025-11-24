package io.jenkins.plugins.jfr;

import java.util.List;
import org.jspecify.annotations.NonNull;

public record JfrEvents(@NonNull String name, @NonNull List<String> events) {}
