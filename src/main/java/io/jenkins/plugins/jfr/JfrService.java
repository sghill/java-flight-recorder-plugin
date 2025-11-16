package io.jenkins.plugins.jfr;

import java.util.Collection;

public interface JfrService {
    Collection<JfrSession> getSessions();
}
