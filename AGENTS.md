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

### Java Language Features

- ✅ **DO**: Use Java records for simple data classes
  ```java
  public record JfrSession(
      @JsonProperty @NonNull String name,
      @JsonProperty long id) {}
  ```
- ✅ **DO**: Use JSpecify annotations (`@NonNull`, `@Nullable`) for nullability contracts
- ✅ **DO**: Use modern Java features (streams, lambdas, etc.) - this project targets Java 17+

### Internationalization

- ✅ **DO**: Use full, descriptive names in Messages.properties
  - Example: `JfrRootAction.DisplayName=Java Flight Recorder` (not "JFR")
- ✅ **DO**: Follow the pattern `ClassName.PropertyName=Value`

## Implementation Guidelines

### Using JFR APIs

When working with Java Flight Recorder:

- ✅ **DO**: Use `jdk.jfr.FlightRecorder` and related classes to get real runtime data
  ```java
  FlightRecorder.getFlightRecorder().getRecordings()
  ```
- ❌ **DON'T**: Use hardcoded or mock data in production code

### JSON Serialization

- ✅ **DO**: Use Jackson's `ObjectMapper` for JSON serialization
- ✅ **DO**: Inject the `ObjectMapper` with proper qualifiers
- ✅ **DO**: Use `@JsonProperty` annotations on record components for explicit serialization

## File Organization

### Maven Settings

- ❌ **NEVER** commit Maven settings files to the repository
  - Files like `~/.m2/settings.xml` belong in the user's home directory, not in source control
  - The `~` directory should never appear in the repository

### Source Structure

Follow standard Maven project structure:
```
src/
├── main/
│   ├── java/
│   │   └── io/jenkins/plugins/jfr/
│   └── resources/
│       └── io/jenkins/plugins/jfr/
└── test/
    └── java/
        └── io/jenkins/plugins/jfr/
```

## Testing

- ✅ **DO**: Write unit tests for new functionality
- ✅ **DO**: Use JUnit 5 (Jupiter) for tests
- ✅ **DO**: Use Mockito for mocking when appropriate
- ✅ **DO**: Follow the given-when-then pattern in tests
  ```java
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
  ```

## Quality Checks

Before submitting code:

1. ✅ Ensure SpotBugs passes (never skip)
2. ✅ Ensure Spotless formatting passes
3. ✅ Ensure no JUnit 4 imports (project uses JUnit 5)
4. ✅ Run `mvn verify` to execute all checks
5. ✅ Verify no unnecessary dependencies were added

## Common Pitfalls to Avoid

1. ❌ Adding dependencies that are already in the parent POM
2. ❌ Using defunct annotation libraries (jsr305)
3. ❌ Bundling libraries instead of using API plugins
4. ❌ Skipping SpotBugs checks
5. ❌ Using javax instead of jakarta namespace
6. ❌ Committing IDE or build tool configuration files
7. ❌ Using abbreviated names in user-facing strings

## Resources

- [Jenkins Plugin Development](https://www.jenkins.io/doc/developer/plugin-development/)
- [Jenkins Plugin Parent POM](https://github.com/jenkinsci/plugin-pom)
- [JSpecify](https://jspecify.dev/)
- [Guice Best Practices](https://github.com/google/guice/wiki/KeepConstructorsHidden)
