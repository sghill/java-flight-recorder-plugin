package io.jenkins.plugins.jfr;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JavaFlightRecorderActionTest {

    @Mock
    private JfrService jfrService;

    private JavaFlightRecorderAction javaFlightRecorderAction;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        javaFlightRecorderAction = new JavaFlightRecorderAction();
        javaFlightRecorderAction.setService(jfrService);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.configure(
                com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        javaFlightRecorderAction.setObjectMapper(objectMapper);
    }

    @Test
    void doSessions() throws Exception {
        // given
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
        Assertions.assertEquals(
                "[{\"name\":\"test\",\"startTime\":1,\"maxSize\":\"250MB\",\"duration\":\"PT1H\",\"settings\":{\"foo\":\"bar\"}}]",
                writer.toString());
    }
}
