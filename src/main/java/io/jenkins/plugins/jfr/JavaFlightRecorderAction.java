package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import hudson.Extension;
import hudson.model.RootAction;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import jenkins.model.Jenkins;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.interceptor.RequirePOST;
import org.kohsuke.stapler.verb.GET;

@Extension
public class JavaFlightRecorderAction implements RootAction {

    @Inject
    private transient JfrService service;

    public void setService(JfrService service) {
        this.service = service;
    }

    @Inject
    @Named("java-flight-recorder")
    private transient ObjectMapper objectMapper;

    @Inject
    private transient Jenkins jenkins;

    @Override
    @Nullable
    public String getIconFileName() {
        return "images/24x24/jfr.png";
    }

    @Override
    @Nullable
    public String getDisplayName() {
        return "Java Flight Recorder";
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
        jenkins.checkPermission(Jenkins.ADMINISTER);
        rsp.setContentType("application/json");
        rsp.setCharacterEncoding("UTF-8");
        try (Writer writer = rsp.getWriter()) {
            objectMapper.writeValue(writer, getSessions());
        }
    }

    @RequirePOST
    @WebMethod(name = "dump")
    public void doDump(StaplerRequest req, StaplerResponse rsp) throws IOException {
        jenkins.checkPermission(Jenkins.ADMINISTER);
        rsp.setContentType("application/json");
        rsp.setCharacterEncoding("UTF-8");
        try (Writer writer = rsp.getWriter();
                Reader reader = req.getReader()) {
            DumpRequest dumpRequest = objectMapper.readValue(reader, DumpRequest.class);
            DumpResponse dumpResponse = service.dump(dumpRequest);
            objectMapper.writeValue(writer, dumpResponse);
        } catch (NoSuchElementException e) {
            rsp.setStatus(404);
        } catch (JsonProcessingException e) {
            rsp.setStatus(400);
        }
    }

}
