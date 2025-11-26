package io.jenkins.plugins.jfr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import jakarta.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.google.inject.Key;
import com.google.inject.name.Names;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class JavaFlightRecorderActionTest {

    @Mock
    private JfrService jfrService;
    @Mock
    private Jenkins jenkins;

    private JavaFlightRecorderAction javaFlightRecorderAction;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        JfrGuiceModule.setJenkins(jenkins);
        Injector injector = Guice.createInjector(Modules.override(new JfrGuiceModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(JfrService.class).toInstance(jfrService);
            }
        }));
        javaFlightRecorderAction = injector.getInstance(JavaFlightRecorderAction.class);
        objectMapper = injector.getInstance(Key.get(ObjectMapper.class, Names.named("java-flight-recorder")));
    }

    @Test
    void doSessions() throws Exception {
        // given
        JfrSession session = new JfrSession(
                "test", Instant.ofEpochMilli(1), "250MB", "PT1H", Collections.singletonMap("foo", "bar"));
        when(jfrService.getSessions()).thenReturn(List.of(session));
        doNothing().when(jenkins).checkPermission(any());

        // when
        StaplerRequest req = mock(StaplerRequest.class);
        StaplerResponse rsp = mock(StaplerResponse.class);
        StringWriter writer = new StringWriter();
        when(rsp.getWriter()).thenReturn(new PrintWriter(writer));
        javaFlightRecorderAction.doSessions(req, rsp);

        // then
        assertThat(writer.toString())
                .isEqualTo(
                        "[{\"name\":\"test\",\"startTime\":1,\"maxSize\":\"250MB\",\"duration\":\"PT1H\",\"settings\":{\"foo\":\"bar\"}}]");
    }

    @Test
    void doDump() throws Exception {
        // given
        DumpRequest request = new DumpRequest("test");
        when(jfrService.dump(request)).thenReturn(new DumpResponse("/tmp/test"));
        doNothing().when(jenkins).checkPermission(any());

        // when
        StaplerRequest req = mock(StaplerRequest.class);
        StringWriter requestWriter = new StringWriter();
        objectMapper.writeValue(requestWriter, request);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(requestWriter.toString())));
        StaplerResponse rsp = mock(StaplerResponse.class);
        StringWriter writer = new StringWriter();
        when(rsp.getWriter()).thenReturn(new PrintWriter(writer));
        javaFlightRecorderAction.doDump(req, rsp);

        // then
        assertThat(writer.toString()).isEqualTo("{\"path\":\"/tmp/test\"}");
    }

    @Test
    void doDumpHandlesNoSuchElementException() throws Exception {
        // given
        DumpRequest request = new DumpRequest("test");
        when(jfrService.dump(request)).thenThrow(new NoSuchElementException());
        doNothing().when(jenkins).checkPermission(any());

        // when
        StaplerRequest req = mock(StaplerRequest.class);
        StringWriter requestWriter = new StringWriter();
        objectMapper.writeValue(requestWriter, request);
        when(req.getReader())
                .thenReturn(new java.io.BufferedReader(new java.io.StringReader(requestWriter.toString())));
        StaplerResponse rsp = mock(StaplerResponse.class);
        StringWriter writer = new StringWriter();
        when(rsp.getWriter()).thenReturn(new PrintWriter(writer));
        javaFlightRecorderAction.doDump(req, rsp);

        // then
        verify(rsp).setStatus(404);
    }

    @Test
    void doDumpHandlesInvalidJson() throws Exception {
        // given
        doNothing().when(jenkins).checkPermission(any());

        // when
        StaplerRequest req = mock(StaplerRequest.class);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{{")));
        StaplerResponse rsp = mock(StaplerResponse.class);
        StringWriter writer = new StringWriter();
        when(rsp.getWriter()).thenReturn(new PrintWriter(writer));
        javaFlightRecorderAction.doDump(req, rsp);

        // then
        verify(rsp).setStatus(400);
    }

    @Test
    void checkUIMethods() {
        assertThat(javaFlightRecorderAction.getIconFileName()).isNotNull();
        assertThat(javaFlightRecorderAction.getDisplayName()).isNotNull();
        assertThat(javaFlightRecorderAction.getUrlName()).isNotNull();
    }
}
