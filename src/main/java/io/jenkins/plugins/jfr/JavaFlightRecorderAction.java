package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.RootAction;
import java.io.IOException;
import java.util.Collection;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.verb.GET;

@Extension
public class JavaFlightRecorderAction implements RootAction {

  private transient JfrService service;

  private transient ObjectMapper objectMapper;

  @Inject
  public void setService(JfrService service) {
    this.service = service;
  }

  @Inject
  @Named("java-flight-recorder")
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

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
  public void doSessions(StaplerRequest req, StaplerResponse rsp) throws IOException {
    rsp.setContentType("application/json");
    rsp.setCharacterEncoding("UTF-8");
    try (java.io.Writer writer = rsp.getWriter()) {
      objectMapper.writeValue(writer, getSessions());
    }
  }

  @NonNull
  public static JavaFlightRecorderAction get() {
    return Jenkins.get().getExtensionList(JavaFlightRecorderAction.class).get(0);
  }
}
