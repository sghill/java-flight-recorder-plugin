package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.Extension;
import hudson.model.RootAction;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.verb.GET;

@Extension
public class JfrRootAction implements RootAction {

    private transient JfrService service;

    private transient ObjectMapper objectMapper;

    @Inject
    public void setService(JfrService service) {
        this.service = service;
    }

    @Inject
    public void setObjectMapper(@Named("java-flight-recorder") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
        rsp.setContentType("application/json; charset=utf-8");
        try (OutputStream os = rsp.getOutputStream()) {
            objectMapper.writeValue(os, getSessions());
        }
    }

    public boolean dependenciesInjected() {
        return service != null && objectMapper != null;
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
