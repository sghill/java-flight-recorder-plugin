package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.Extension;
import hudson.model.RootAction;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import org.jspecify.annotations.Nullable;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.verb.GET;

@Extension
public class JfrRootAction implements RootAction {

    @Inject
    @Nullable
    private transient JfrService service;

    @Inject
    @Nullable
    private transient ObjectMapper objectMapper;

    @Override
    public String getIconFileName() {
        return "symbol-flight-recorder";
    }

    @Override
    public String getDisplayName() {
        return Messages.JfrRootAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "jfr";
    }

    @GET
    @WebMethod(name = "sessions")
    public void doSessions(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        rsp.setContentType("application/json");
        objectMapper.writeValue(rsp.getWriter(), getSessions());
    }

    public Collection<JfrSession> getSessions() {
        if (service == null) {
            Jenkins.get().getInjector().injectMembers(this);
        }
        if (service != null) {
            Collection<JfrSession> sessions = service.getSessions();
            if (sessions != null) {
                return sessions;
            }
        }
        return Collections.emptyList();
    }
}
