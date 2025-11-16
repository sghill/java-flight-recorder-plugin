package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;

public class JfrModule extends AbstractModule {

    @Provides
    @Singleton
    public JfrService jfrService() {
        return new JfrServiceImpl();
    }

    @Provides
    @Singleton
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
