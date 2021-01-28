package com.github.matek2305.betting.match.domain;

import java.time.ZonedDateTime;

public interface Match {

    default MatchId matchId() {
        return getMatchInformation().matchId();
    }

    default ZonedDateTime startDateTime() {
        return getMatchInformation().startDateTime();
    }

    MatchInformation getMatchInformation();
}
