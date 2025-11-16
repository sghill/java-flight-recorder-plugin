package io.jenkins.plugins.jfr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JfrRootActionTest {

    @Mock
    private JfrService service;

    @InjectMocks
    private JfrRootAction action;

    @BeforeEach
    public void setUp() {
        action = new JfrRootAction();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSessions() {
        Collection<JfrSession> sessions = Arrays.asList(new JfrSession("test", 123));
        when(service.getSessions()).thenReturn(sessions);

        assertEquals(sessions, action.getSessions());
    }
}
