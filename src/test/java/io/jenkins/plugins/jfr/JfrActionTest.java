package io.jenkins.plugins.jfr;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

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
