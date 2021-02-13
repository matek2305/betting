package com.github.matek2305.betting.commons;

import java.util.UUID;

public interface PublishableEvent {

    UUID eventId();

    default boolean shouldPublish() {
        return false;
    }
}
