package io.jenkins.plugins.jfr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class JfrActionTest {

    @Test
    public void testGetSessions() {
        // given
        JfrAction action = new JfrAction();

        // when
        var sessions = action.getSessions();

        // then
        assertThat(sessions).isEmpty();
    }
}
