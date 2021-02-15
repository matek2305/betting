package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.core.match.domain.Team;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class AddIncomingMatchCommand {
    ZonedDateTime startDateTime;
    Team homeTeam;
    Team awayTeam;
}
