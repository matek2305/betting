package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.commons.PublishableEvent;
import lombok.Value;

import java.util.UUID;

public interface MatchEvent extends PublishableEvent {

    MatchId matchId();

    @Value
    class MatchFinished implements MatchEvent {

        public static final String ADDRESS = "match_results";

        UUID eventId = UUID.randomUUID();
        MatchId matchId;
        MatchScore result;
        MatchRewards rewards;

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
    class MatchFinishRejected implements MatchEvent {
        UUID eventId = UUID.randomUUID();
        MatchId matchId;
        String rejectionReason;
    }

    @Value
    class MatchResultCorrected implements MatchEvent {
        UUID eventId = UUID.randomUUID();
        MatchId matchId;
        MatchScore result;
        MatchRewards rewards;
    }
}
