package com.github.matek2305.betting.match.domain;

import lombok.Value;

@Value
class FinishMatchCommand {
    MatchId matchId;
    MatchScore result;
}
