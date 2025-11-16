package io.jenkins.plugins.jfr;

import hudson.Extension;
import hudson.model.RootAction;
import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.json.JsonHttpResponse;
import org.kohsuke.stapler.verb.GET;

@Extension
public class JfrRootAction implements RootAction {

    @Inject
    private transient JfrService service;

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
    public JsonHttpResponse doSessions() {
        JSONObject response = new JSONObject();
        response.element("sessions", JSONSerializer.toJSON(getSessions()));
        return new JsonHttpResponse(response);
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
