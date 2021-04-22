package com.github.matek2305.betting.commons;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

public interface PublishableEvent {

    UUID eventId();

    default PublishAddress address() {
        throw new IllegalArgumentException("Publish address not set for " + this.getClass().getName());
    }

    default boolean shouldPublish() {
        return false;
    }

    @Value(staticConstructor = "of")
    class PublishAddress {
        @NonNull String value;
    }
}
