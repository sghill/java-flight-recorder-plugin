package io.jenkins.plugins.jfr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import hudson.Extension;

@Extension
public class JfrGuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(JfrService.class).in(Singleton.class);
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
