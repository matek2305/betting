package com.github.matek2305.betting.core.match.domain;

import lombok.Value;

@Value
class CorrectMatchResultCommand {
    MatchId matchId;
    MatchScore result;
}
