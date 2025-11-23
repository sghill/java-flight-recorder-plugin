package io.jenkins.plugins.jfr;

import static org.junit.jupiter.api.Assertions.assertThrows;

import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.AccessDeniedException3;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
public class JfrActionTest {

  @Test
  public void testGetSessionsWithOverallReadPermission(JenkinsRule r) {
    // given
    final String READ_USER = "reader";
    r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
    r.jenkins.setAuthorizationStrategy(
        new MockAuthorizationStrategy().grant(Jenkins.READ).everywhere().to(READ_USER));

    try (ACLContext ignored =
        ACL.as(User.get(READ_USER, true, null).impersonate())) {
      JfrAction action = new JfrAction();

      // when
      assertThrows(AccessDeniedException3.class, action::getSessions);

      // then
      // AccessDeniedException3 should be thrown
    }
  }

  @Test
  public void testGetSessionsAsAdmin(JenkinsRule r) {
    // given
    final String ADMIN_USER = "admin";
    r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
    r.jenkins.setAuthorizationStrategy(
        new MockAuthorizationStrategy().grant(Jenkins.ADMINISTER).everywhere().to(ADMIN_USER));

    try (ACLContext ignored =
        ACL.as(User.get(ADMIN_USER, true, null).impersonate())) {
      JfrAction action = new JfrAction();

      // when
      action.getSessions();

      // then
      // No exception should be thrown
    }
  }
}
