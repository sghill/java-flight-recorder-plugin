package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import hudson.Extension;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Extension
public class JfrGuiceModule extends AbstractModule {

    @Provides
    @Singleton
    public JfrService jfrService() {
        return new DefaultJfrService();
    }

    @Provides
    @Singleton
    @Named("java-flight-recorder")
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
