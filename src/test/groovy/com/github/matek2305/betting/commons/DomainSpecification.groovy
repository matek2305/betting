package com.github.matek2305.betting.commons

import spock.lang.Specification

import java.util.function.Function

class DomainSpecification extends Specification {
    
    Set<PublishableEvent> publishedEvents = []
    
    void setup() {
        publishedEvents.clear()
    }
    
    def <T> T withEventsPublisher(Function<EventsPublisher, T> createFunction) {
        return createFunction.apply(new EventsPublisher() {
            @Override
            void publish(PublishableEvent event) {
                publishedEvents.add(event)
            }
        })
    }
    
    def <T extends PublishableEvent> T findPublishedEvent(Class<T> eventClass) {
        return publishedEvents.find { it.class == eventClass } as T
    }
}
