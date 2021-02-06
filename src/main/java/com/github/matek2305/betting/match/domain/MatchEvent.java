package com.github.matek2305.betting.match.domain;

import lombok.Value;

import java.time.ZonedDateTime;

public interface MatchEvent {

    MatchId matchId();

    @Value
    class NewMatchAdded implements MatchEvent {
        MatchId matchId;
        ZonedDateTime startDateTime;
        MatchRivals rivals;
    }

    @Value
    class MatchFinished implements MatchEvent {
        MatchId matchId;
        MatchScore result;
    }
}
