package com.github.matek2305.betting.commons;

public interface EventsPublisher {

    void publish(String address, PublishableEvent event);

    static EventsPublisher explicitPublish(EventsPublisher publisher) {
        return new ExplicitPublishDecorator(publisher);
    }
}
