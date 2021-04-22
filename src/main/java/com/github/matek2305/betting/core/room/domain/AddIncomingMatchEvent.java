package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.commons.PublishableEvent;
import com.github.matek2305.betting.core.match.domain.NewMatch;
import lombok.Value;

import java.util.UUID;

public interface AddIncomingMatchEvent extends PublishableEvent {

    @Value
    class IncomingMatchAdded implements AddIncomingMatchEvent {

        public static final String ADDRESS = "matches";

        UUID eventId = UUID.randomUUID();
        NewMatch match;

        @Override
        public PublishAddress address() {
            return PublishAddress.of(ADDRESS);
        }

        @Override
        public boolean shouldPublish() {
            return true;
        }
    }

    @Value
    class AddIncomingMatchRejected implements AddIncomingMatchEvent {
        UUID eventId = UUID.randomUUID();
        String rejectionReason;
    }
}
