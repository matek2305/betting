package com.github.matek2305.betting.core.match.web;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
class AddNewMatchRequest {
    String homeTeamName;
    String awayTeamName;
    ZonedDateTime startDateTime;
}
