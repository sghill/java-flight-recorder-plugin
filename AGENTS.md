# AI Agent Guidelines for Java Flight Recorder Plugin

This document provides guidelines for AI agents contributing to the Java Flight Recorder Plugin. Following these conventions will help ensure code quality and consistency with project standards.

## Maven Dependencies

### Dependency Management

- **Check parent POM first**: Before adding any dependency, verify if it's already provided by the parent POM (`org.jenkins-ci.plugins:plugin`). The parent POM includes many common dependencies.
- **Use API plugins**: The Jenkins ecosystem prefers "API plugins" that provide shared libraries rather than each plugin bundling its own. Always prefer API plugins over direct library dependencies.
- **Remove unnecessary test dependencies**: Many test dependencies (like `jenkins-test-harness`, `mockito-core`, `mockito-junit-jupiter`) are already provided by the parent POM.

### Specific Dependency Rules

#### Jackson (JSON Processing)
- ✅ **DO**: Use `org.jenkins-ci.plugins:jackson2-api`
- ❌ **DON'T**: Add direct dependencies on `com.fasterxml.jackson.core:jackson-databind` or other Jackson modules
- **Why**: The `jackson2-api` plugin provides Jackson libraries in a shared, version-managed way

#### Annotations
- ✅ **DO**: Use `io.jenkins.plugins:jspecify-api` for nullability annotations (`@NonNull`, `@Nullable`)
- ❌ **DON'T**: Use `com.google.code.findbugs:jsr305` (defunct project)
- ✅ **DO**: Use Jakarta namespace annotations (e.g., `jakarta.inject.Named`)
- ❌ **DON'T**: Use older javax namespace annotations

### Build Configuration

- ❌ **NEVER** add `<spotbugs.skip>true</spotbugs.skip>` to properties
  - **Why**: SpotBugs rules prevent problematic code patterns. New code must pass these checks.
- ❌ **DON'T** override Maven Surefire plugin configuration unless absolutely necessary
  - **Why**: The parent POM already configures test execution appropriately
- ✅ **DO**: Keep `<hpi.strictBundledArtifacts>true</hpi.strictBundledArtifacts>` enabled
  - **Why**: Ensures proper dependency management and prevents bundling conflicts

## Code Style and Patterns

### Dependency Injection (Guice)

When using Guice modules for dependency injection:

- ✅ **DO**: Use `@Provides` methods in modules to create instances
  ```java
  @Provides
  @Singleton
  public MyService myService() {
      return new MyServiceImpl();
  }
  ```
- ✅ **DO**: Use qualified injections with `@Named` when multiple instances of the same type exist
  ```java
  @Provides
  @Singleton
  @Named("java-flight-recorder")
  public ObjectMapper objectMapper() {
      return new ObjectMapper();
  }
  ```
- ❌ **DON'T**: Use `@Named` directly on implementation classes without a corresponding `@Provides` method
  - **Why**: Violates Guice best practices; prefer explicit provider methods for clarity
- ❌ **DON'T**: Use `bind` methods in `AbstractModules`
  - **Why**: Violates Guice best practices; prefer explicit provider methods for clarity

### Java Language Features

- ✅ **DO**: Use Java records for simple data classes
  ```java
  public record JfrSession(String name, long id) {}
  ```
- ✅ **DO**: Use JSpecify annotations (`@NonNull`, `@Nullable`) for nullability contracts
- ❌ **DON'T**: Use FindBugs annotations (`@edu.umd.cs.findbugs.annotations.NonNull`) for nullability contracts
- ✅ **DO**: Use modern Java features (streams, lambdas, etc.) - this project targets Java 17+


## Testing

- ✅ **DO**: Write unit tests for new functionality
- ✅ **DO**: Use JUnit 5 (Jupiter) for tests
- ✅ **DO**: Use Mockito for mocking when appropriate
- ✅ **DO**: Use AssertJ for assertions (better readability and diagnostics)
- ✅ **DO**: Follow the given-when-then pattern in tests
  ```java
  @Test
  public void testGetSessions() {
      // given
      List<JfrSession> sessions = List.of(new JfrSession("test", 123));
      given(service.getSessions()).willReturn(sessions);

      // when
      Collection<JfrSession> result = action.getSessions();

      // then
      assertThat(sessions).isEqualTo(result);
  }
  ```
- ✅ **DO**: To integrate Jenkins test infrastructure with JUnit 5, annotate test classes with `@WithJenkins` and add `JenkinsRule` as a parameter to test methods.

## Quality Checks

Before submitting code:

1. ✅ Ensure SpotBugs passes (never skip)
2. ✅ Ensure `mvn spotless:apply` has been run to format code
3. ✅ Ensure Spotless formatting passes
4. ✅ Ensure no JUnit 4 imports (project uses JUnit 5)
5. ✅ Run `mvn verify spotless:check` to execute all checks
6. ✅ Verify no unnecessary dependencies were added

## Common Pitfalls to Avoid

1. ❌ Adding dependencies that are already in the parent POM
2. ❌ Using defunct annotation libraries (jsr305)
3. ❌ Bundling libraries instead of using API plugins
4. ❌ Skipping SpotBugs checks
5. ❌ Using javax instead of jakarta namespace
6. ❌ Committing IDE or build tool configuration files
7. ❌ Using abbreviated names in user-facing strings

## Project Specific Information
- The project is a Jenkins plugin for Java Flight Recorder, built with Maven and packaged as an HPI file.
- To download all Maven dependencies, run `mvn --batch-mode dependency:go-offline`.
- To build and verify the project, run `mvn --batch-mode clean verify`.
- Run `mvn --batch-mode spotless:apply` to format the code according to the project's style guidelines.
- Always use the `--batch-mode` flag when running Maven commands.
- The project uses the Stapler web framework for request handling. While Jenkins core uses Google Guice for dependency injection, `jakarta.inject` annotations should be preferred where possible.
- The project uses Jackson for JSON serialization. For Stapler endpoints, it may be necessary to serialize objects to a JSON string with Jackson and then parse it into a `net.sf.json` object, or write directly to the `StaplerResponse` writer.
- Jenkins `Action` classes require a corresponding view file (e.g., `index.jelly`) in a resource directory that mirrors the class's package structure (e.g., `src/main/resources/io/jenkins/plugins/jfr/JfrAction/`) to render a UI.
- The project's Jackson `ObjectMapper` must be configured with `SerializationFeature.WRITE_DATES_AS_TIMESTAMPS = true` and `SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS = false` for correct date serialization.
- A Guice module (`JfrGuiceModule`) provides a Jackson `ObjectMapper` instance using the `@Named("java-flight-recorder")` annotation.
- Guice modules should exclusively use `@Provides` methods for bindings, removing the `configure()` method and `bind()` statements.
- For dependency injection and testability, services should be defined as interfaces (e.g., `JfrService`) with package-private implementations (e.g., `DefaultJfrService`). The Guice module should bind the interface to its implementation.
- Use `org.springframework.security.core.*` for security-related classes, as the project has migrated from Acegi to Spring Security.
- To test code that requires permissions, use `MockAuthorizationStrategy` to configure user permissions and `ACL.as(User.get(username, ...))` to impersonate a user.
- When testing permissions with a user that may not exist, first set up a security realm in the test (e.g., `jenkinsRule.jenkins.setSecurityRealm(jenkinsRule.createDummySecurityRealm())`) to allow for dynamic user creation.
- The `AGENTS.md` file provides project-specific contribution guidelines, such as using `@NonNull` annotations and the given-when-then pattern in tests.
- The DTD for the `checkstyle.xml` configuration file should be `https://checkstyle.org/dtds/configuration_1_3.dtd`.
- The project's checkstyle configuration (`checkstyle.xml`) disallows certain imports, including Guice annotations (e.g., `com.google.inject.Singleton`) and FindBugs nullability annotations (`edu.umd.cs.findbugs.annotations.NonNull`).
- The project uses a GitHub Actions workflow defined in `.github/workflows/ci.yml` for continuous integration.

## Resources

- [Jenkins Plugin Development](https://www.jenkins.io/doc/developer/plugin-development/)
- [Jenkins Plugin Parent POM](https://github.com/jenkinsci/plugin-pom)
- [JSpecify](https://jspecify.dev/)
- [Guice Best Practices](https://github.com/google/guice/wiki/KeepConstructorsHidden)
