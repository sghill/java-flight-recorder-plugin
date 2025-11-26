package io.jenkins.plugins.jfr;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

@Extension
public class JfrConfig extends GlobalConfiguration {

    private String jfrDumpsDirectory;
    private int maxConcurrentSessions;

    public static JfrConfig get() {
        return GlobalConfiguration.all().get(JfrConfig.class);
    }

    public JfrConfig() {
        load();
    }

    public String getJfrDumpsDirectory() {
        return jfrDumpsDirectory;
    }

    @DataBoundSetter
    public void setJfrDumpsDirectory(String jfrDumpsDirectory) {
        this.jfrDumpsDirectory = jfrDumpsDirectory;
        save();
    }

    public int getMaxConcurrentSessions() {
        return maxConcurrentSessions;
    }

    @DataBoundSetter
    public void setMaxConcurrentSessions(int maxConcurrentSessions) {
        this.maxConcurrentSessions = maxConcurrentSessions;
        save();
    }

    public FormValidation doCheckMaxConcurrentSessions(@QueryParameter int value) {
        if (value > 5) {
            return FormValidation.error("The maximum number of concurrent sessions cannot be greater than 5.");
        }
        return FormValidation.ok();
    }

    @Override
    public String getDisplayName() {
        return "Java Flight Recorder";
    }
}
