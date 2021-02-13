package com.github.matek2305.betting.commons;

public interface EventsPublisher {

    void publish(PublishableEvent event);

    static EventsPublisher explicitPublish(EventsPublisher publisher) {
        return new ExplicitPublishDecorator(publisher);
    }
}
