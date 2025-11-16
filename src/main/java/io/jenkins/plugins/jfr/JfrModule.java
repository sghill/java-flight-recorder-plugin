package io.jenkins.plugins.jfr;

import com.google.inject.AbstractModule;

public class JfrModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(JfrService.class).to(JfrServiceImpl.class);
    }
}
