package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.name.Named;
import jakarta.inject.Inject;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.RootAction;
import java.io.IOException;
import java.io.Writer;
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
  public void setObjectMapper(@Named("java-flight-recorder") ObjectMapper objectMapper) {
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
    if (objectMapper == null) {
      Jenkins.get().getInjector().injectMembers(this);
    }
    rsp.setContentType("application/json");
    rsp.setCharacterEncoding("UTF-8");
    try (Writer writer = rsp.getWriter()) {
      objectMapper.writeValue(writer, getSessions());
    }
  }

}
