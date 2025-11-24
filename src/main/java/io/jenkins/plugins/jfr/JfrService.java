package io.jenkins.plugins.jfr;

import java.util.List;
import org.jspecify.annotations.NonNull;

public interface JfrService {
    @NonNull
    List<JfrSession> getSessions();
}
