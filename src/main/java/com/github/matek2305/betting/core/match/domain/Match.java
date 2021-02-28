package com.github.matek2305.betting.core.match.domain;

import java.time.ZonedDateTime;

public interface Match {

    default MatchId matchId() {
        return matchInformation().matchId();
    }

    default String homeTeamName() {
        return matchInformation().homeTeam().name();
    }

    default String awayTeamName() {
        return matchInformation().awayTeam().name();
    }

    default ZonedDateTime startDateTime() {
        return matchInformation().startDateTime();
    }

    MatchInformation matchInformation();
}
