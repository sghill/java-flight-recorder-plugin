package io.jenkins.plugins.jfr;

import java.util.Arrays;
import java.util.Collection;
import javax.inject.Named;

@Named
public class JfrServiceImpl implements JfrService {

    @Override
    public Collection<JfrSession> getSessions() {
        return Arrays.asList(new JfrSession("Session 1", 1), new JfrSession("Session 2", 2));
    }
}
