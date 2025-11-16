package io.jenkins.plugins.jfr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JfrRootActionTest {

    @Mock
    private JfrService service;

    private JfrRootAction action;

    @BeforeEach
    public void setUp() {
        action = new JfrRootAction();
        action.setService(service);
    }

    @Test
    public void testGetSessions() {
        // given
        Collection<JfrSession> sessions = Arrays.asList(new JfrSession("test", 123));
        given(service.getSessions()).willReturn(sessions);

        // when
        Collection<JfrSession> result = action.getSessions();

        // then
        assertEquals(sessions, result);
    }
}
