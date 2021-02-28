package com.github.matek2305.betting.configuration;

import com.github.matek2305.betting.commons.DateProvider;
import com.github.matek2305.betting.commons.EventsPublisher;
import io.vertx.core.eventbus.EventBus;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.time.ZonedDateTime;

@Dependent
@RequiredArgsConstructor
class CommonContextConfiguration {

    private final EventBus eventBus;

    @Produces
    @ApplicationScoped
    public DateProvider dateProvider() {
        return ZonedDateTime::now;
    }

    @Produces
    public EventsPublisher eventsPublisher() {
        return EventsPublisher.explicitPublish(eventBus::publish);
    }
}
