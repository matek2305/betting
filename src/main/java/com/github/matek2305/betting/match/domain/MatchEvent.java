package com.github.matek2305.betting.match.domain;

import com.github.matek2305.betting.commons.PublishableEvent;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface MatchEvent extends PublishableEvent {

    MatchId matchId();

    @Value
    class NewMatchAdded implements MatchEvent {
        UUID eventId = UUID.randomUUID();
        MatchId matchId;
        ZonedDateTime startDateTime;
    }

    @Value
    class MatchFinished implements MatchEvent {
        UUID eventId = UUID.randomUUID();
        MatchId matchId;
        MatchScore result;
        MatchRewards rewards;
    }
}
