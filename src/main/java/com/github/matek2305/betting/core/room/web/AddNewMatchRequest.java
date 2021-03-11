package com.github.matek2305.betting.core.room.web;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
class AddNewMatchRequest {
    String homeTeamName;
    String awayTeamName;
    ZonedDateTime startDateTime;
    String origin;
    String externalId;
}
