package com.github.matek2305.betting.commons;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ExplicitPublishDecorator implements EventsPublisher {

    private final EventsPublisher eventsPublisher;

    @Override
    public void publish(PublishableEvent event) {
        if (event.shouldPublish()) {
            eventsPublisher.publish(event);
        }
    }
}
