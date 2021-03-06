package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.core.match.domain.Team;
import com.github.matek2305.betting.core.match.domain.external.ExternalId;
import com.github.matek2305.betting.core.match.domain.external.Origin;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class AddIncomingMatchCommand {
    ZonedDateTime startDateTime;
    Team homeTeam;
    Team awayTeam;
}
