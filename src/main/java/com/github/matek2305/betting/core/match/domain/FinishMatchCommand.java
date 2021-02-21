package com.github.matek2305.betting.core.match.domain;

import lombok.Value;

@Value
public class FinishMatchCommand {
    MatchId matchId;
    MatchScore result;
}
