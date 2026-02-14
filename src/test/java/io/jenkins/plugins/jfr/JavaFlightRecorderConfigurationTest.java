package io.jenkins.plugins.jfr;

import static org.assertj.core.api.Assertions.assertThat;

import hudson.util.FormValidation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class JavaFlightRecorderConfigurationTest {

    @Test
    void defaultValues(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // then
        assertThat(config.getOutputDirectory()).isNull();
        assertThat(config.getMaxDumps()).isEqualTo(3);
    }

    @Test
    void setAndGetOutputDirectory(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        config.setOutputDirectory("/tmp/jfr-dumps");

        // then
        assertThat(config.getOutputDirectory()).isEqualTo("/tmp/jfr-dumps");
    }

    @Test
    void setAndGetMaxDumps(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        config.setMaxDumps(5);

        // then
        assertThat(config.getMaxDumps()).isEqualTo(5);
    }

    @Test
    void doCheckOutputDirectoryAcceptsBlank(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        FormValidation result = config.doCheckOutputDirectory("");

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.OK);
    }

    @Test
    void doCheckOutputDirectoryAcceptsAbsolutePath(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        FormValidation result = config.doCheckOutputDirectory("/tmp/jfr-output");

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.OK);
    }

    @Test
    void doCheckOutputDirectoryRejectsRelativePath(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        FormValidation result = config.doCheckOutputDirectory("relative/path");

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);
    }

    @Test
    void doCheckOutputDirectoryRejectsFileAsPath(JenkinsRule r, @TempDir Path tempDir) throws IOException {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();
        Path file = Files.createFile(tempDir.resolve("not-a-dir"));

        // when
        FormValidation result = config.doCheckOutputDirectory(file.toString());

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);
    }

    @Test
    void doCheckMaxDumpsAcceptsPositiveInteger(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        FormValidation result = config.doCheckMaxDumps("5");

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.OK);
    }

    @Test
    void doCheckMaxDumpsRejectsZero(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        FormValidation result = config.doCheckMaxDumps("0");

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);
    }

    @Test
    void doCheckMaxDumpsRejectsNegative(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        FormValidation result = config.doCheckMaxDumps("-1");

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);
    }

    @Test
    void doCheckMaxDumpsRejectsNonNumeric(JenkinsRule r) {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();

        // when
        FormValidation result = config.doCheckMaxDumps("abc");

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);
    }

    @Test
    void configurationSurvivesRoundTrip(JenkinsRule r) throws Exception {
        // given
        JavaFlightRecorderConfiguration config = JavaFlightRecorderConfiguration.get();
        config.setOutputDirectory("/tmp/jfr-test");
        config.setMaxDumps(3);

        // when
        r.configRoundtrip();

        // then
        JavaFlightRecorderConfiguration reloaded = JavaFlightRecorderConfiguration.get();
        assertThat(reloaded.getOutputDirectory()).isEqualTo("/tmp/jfr-test");
        assertThat(reloaded.getMaxDumps()).isEqualTo(3);
    }
}
