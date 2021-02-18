package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.commons.PublishableEvent;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import lombok.Value;

import java.util.UUID;

public interface PlayerEvent extends PublishableEvent {

    PlayerId playerId();

    @Value
    class PlayerBetMade implements PlayerEvent {
        UUID eventId = UUID.randomUUID();
        PlayerId playerId;
        MatchId matchId;
        MatchScore bet;
    }

    @Value
    class PlayerBetRejected implements PlayerEvent {
        UUID eventId = UUID.randomUUID();
        PlayerId playerId;
        MatchId matchId;
    }

    @Value
    class PointsRewarded implements PlayerEvent {
        UUID eventId = UUID.randomUUID();
        PlayerId playerId;
        MatchId matchId;
        Points points;
    }
}
