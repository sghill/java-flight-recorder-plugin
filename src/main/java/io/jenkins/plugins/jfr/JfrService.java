package io.jenkins.plugins.jfr;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;

public interface JfrService {
    @NonNull
    List<JfrSession> getSessions();
}
