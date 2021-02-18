package com.github.matek2305.betting.commons;

import com.github.matek2305.betting.security.LoggedUser;
import io.vertx.core.eventbus.EventBus;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
@RequiredArgsConstructor
class CommonContextConfiguration {

    private final EventBus eventBus;

    @Produces
    public EventsPublisher eventsPublisher() {
        return EventsPublisher.explicitPublish(eventBus::publish);
    }

    @Produces
    public LoggedUser loggedUser() {
        return () -> "test@user.com";
    }
}
