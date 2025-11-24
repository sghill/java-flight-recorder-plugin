package io.jenkins.plugins.jfr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
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

@WithJenkins
@ExtendWith(MockitoExtension.class)
class JavaFlightRecorderActionTest {

    @Mock
    private JfrService jfrService;

    private JavaFlightRecorderAction javaFlightRecorderAction;

    @BeforeEach
    void setUp() {
        javaFlightRecorderAction = new JavaFlightRecorderAction();
        javaFlightRecorderAction.setService(jfrService);
        Injector injector = Guice.createInjector(new JfrGuiceModule());
        ObjectMapper objectMapper =
                injector.getInstance(Key.get(ObjectMapper.class, Names.named("java-flight-recorder")));
        javaFlightRecorderAction.setObjectMapper(objectMapper);
    }

    @Test
    void doSessions(JenkinsRule r) throws Exception {
        // given
        r.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.ADMINISTER)
                .everywhere()
                .toAuthenticated()
                .grant(Jenkins.READ)
                .everywhere()
                .to("reader"));
        JfrSession session = new JfrSession(
                "test", Instant.ofEpochMilli(1), "250MB", "PT1H", Collections.singletonMap("foo", "bar"));
        when(jfrService.getSessions()).thenReturn(List.of(session));

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
    public void doSessionsIsProtectedByAdministerPermission(JenkinsRule r) throws Exception {
        // given
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
        r.jenkins.setAuthorizationStrategy(
                new MockAuthorizationStrategy().grant(Jenkins.READ).everywhere().to("reader"));

        // when
        try (ACLContext c = ACL.as(User.get("reader", false, null))) {
            StaplerRequest req = mock(StaplerRequest.class);
            StaplerResponse rsp = mock(StaplerResponse.class);

            // then
            assertThatThrownBy(() -> javaFlightRecorderAction.doSessions(req, rsp))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
