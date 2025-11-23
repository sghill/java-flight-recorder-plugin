package io.jenkins.plugins.jfr;

import hudson.model.Action;
import java.util.Collection;
import java.util.Collections;
import jenkins.model.Jenkins;
import org.jspecify.annotations.NonNull;

public class JfrAction implements Action {

    @Override
    @NonNull
    public String getIconFileName() {
        return "document.png";
    }

    @Override
    @NonNull
    public String getDisplayName() {
        return "Java Flight Recorder";
    }

    @Override
    @NonNull
    public String getUrlName() {
        return "jfr";
    }

    @NonNull
    public Collection<JfrConfig> getSessions() {
        Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        return Collections.emptyList();
    }
}
