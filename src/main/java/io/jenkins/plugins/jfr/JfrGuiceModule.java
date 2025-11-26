package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return mapper;
    }

    @Provides
    public JfrConfig jfrConfig() {
        return JfrConfig.get();
    }
}
