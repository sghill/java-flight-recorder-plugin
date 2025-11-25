package io.jenkins.plugins.jfr;

import java.io.IOException;
import java.util.List;
import org.jspecify.annotations.NonNull;

public interface JfrService {
    @NonNull
    List<JfrSession> getSessions();

    @NonNull
    DumpResponse dump(DumpRequest request) throws IOException;
}
