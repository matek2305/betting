package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.core.match.domain.MatchEvent.IncomingMatchCreated;
import com.github.matek2305.betting.core.match.domain.MatchEvent.IncomingMatchCreationRejected;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class CreateIncomingMatchCommand {
    ZonedDateTime startDateTime;
    Team homeTeam;
    Team awayTeam;

    IncomingMatchCreated accept() {
        return new IncomingMatchCreated(
                MatchId.of(UUID.randomUUID()),
                startDateTime,
                homeTeam,
                awayTeam);
    }

    IncomingMatchCreationRejected reject() {
        return new IncomingMatchCreationRejected(
                MatchId.of(UUID.randomUUID()),
                startDateTime,
                homeTeam,
                awayTeam);
    }
}
