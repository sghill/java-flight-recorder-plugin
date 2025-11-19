package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.stapler.json.JsonHttpResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class JfrRootActionTest {

  @Mock private JfrService jfrService;
  private JfrRootAction jfrRootAction;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    jfrRootAction = new JfrRootAction();
    jfrRootAction.service = jfrService;
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    jfrRootAction.objectMapper = objectMapper;
  }

  @Test
  void doSessions() throws Exception {
    // given
    JfrSession session =
        new JfrSession(
            "test", Instant.ofEpochMilli(1), "250MB", "PT1H", Collections.singletonMap("foo", "bar"));
    when(jfrService.getSessions()).thenReturn(List.of(session));

    // when
    StaplerRequest req = mock(StaplerRequest.class);
    StaplerResponse rsp = mock(StaplerResponse.class);
    StringWriter writer = new StringWriter();
    when(rsp.getWriter()).thenReturn(new PrintWriter(writer));
    jfrRootAction.doSessions(req, rsp);

    // then
    Assertions.assertEquals(
        "[{\"name\":\"test\",\"startTime\":1,\"maxSize\":\"250MB\",\"duration\":\"PT1H\",\"settings\":{\"foo\":\"bar\"}}]",
        writer.toString());
  }
}
