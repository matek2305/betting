package com.github.matek2305.betting.core.match.domain;

import java.time.ZonedDateTime;

public interface Match {

    default MatchId matchId() {
        return matchInformation().matchId();
    }

    default Team homeTeam() {
        return matchInformation().homeTeam();
    }

    default Team awayTeam() {
        return matchInformation().awayTeam();
    }

    default ZonedDateTime startDateTime() {
        return matchInformation().startDateTime();
    }

    MatchInformation matchInformation();
}
