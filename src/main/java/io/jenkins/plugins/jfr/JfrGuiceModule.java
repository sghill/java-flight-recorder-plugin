package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

public class JfrGuiceModule extends AbstractModule {

    @Provides
    @Singleton
    @Named("java-flight-recorder")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
