package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.commons.PublishableEvent;
import lombok.Value;

import java.util.UUID;

public interface MatchEvent extends PublishableEvent {

    MatchId matchId();

    @Value
    class MatchFinished implements MatchEvent {
        UUID eventId = UUID.randomUUID();
        MatchId matchId;
        MatchScore result;
        MatchRewards rewards;
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
