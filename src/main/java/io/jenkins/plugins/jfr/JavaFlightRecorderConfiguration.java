package io.jenkins.plugins.jfr;

import hudson.Extension;
import hudson.util.FormValidation;
import java.nio.file.Files;
import java.nio.file.Path;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class JavaFlightRecorderConfiguration extends GlobalConfiguration {

    private static final int DEFAULT_MAX_DUMPS = 3;

    @Nullable
    private String outputDirectory;

    private int maxDumps = DEFAULT_MAX_DUMPS;

    public JavaFlightRecorderConfiguration() {
        load();
    }

    @NonNull
    public static JavaFlightRecorderConfiguration get() {
        return GlobalConfiguration.all().getInstance(JavaFlightRecorderConfiguration.class);
    }

    @Nullable
    public String getOutputDirectory() {
        return outputDirectory;
    }

    @DataBoundSetter
    public void setOutputDirectory(@Nullable String outputDirectory) {
        this.outputDirectory = outputDirectory;
        save();
    }

    public int getMaxDumps() {
        return maxDumps;
    }

    @DataBoundSetter
    public void setMaxDumps(int maxDumps) {
        this.maxDumps = maxDumps;
        save();
    }

    public FormValidation doCheckOutputDirectory(@QueryParameter String value) {
        if (value == null || value.isBlank()) {
            return FormValidation.ok("Default: system temporary directory");
        }
        Path path = Path.of(value);
        if (!path.isAbsolute()) {
            return FormValidation.error("Output directory must be an absolute path");
        }
        if (Files.exists(path) && !Files.isDirectory(path)) {
            return FormValidation.error("Path exists but is not a directory");
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckMaxDumps(@QueryParameter String value) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1) {
                return FormValidation.error("Must be at least 1");
            }
            return FormValidation.ok();
        } catch (NumberFormatException e) {
            return FormValidation.error("Must be a positive integer");
        }
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        return true;
    }
}
