package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import java.io.IOException;
import java.util.Collection;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.json.JsonHttpResponse;
import org.kohsuke.stapler.verb.GET;

@Extension
public class JavaFlightRecorderAction implements UnprotectedRootAction {

  @Inject transient JfrService service;

  @Inject
  @Named("java-flight-recorder")
  transient ObjectMapper objectMapper;

  @Override
  @Nullable
  public String getIconFileName() {
    return null;
  }

  @Override
  @Nullable
  public String getDisplayName() {
    return null;
  }

  @Override
  @Nullable
  public String getUrlName() {
    return "java-flight-recorder";
  }

  @NonNull
  public Collection<JfrSession> getSessions() {
    return service.getSessions();
  }

  @GET
  @WebMethod(name = "sessions")
  public void doSessions(org.kohsuke.stapler.StaplerRequest req, org.kohsuke.stapler.StaplerResponse rsp) throws IOException {
    rsp.setContentType("application/json");
    objectMapper.writeValue(rsp.getWriter(), getSessions());
  }

  @NonNull
  public static JavaFlightRecorderAction get() {
    return Jenkins.get().getExtensionList(JavaFlightRecorderAction.class).get(0);
  }
}
