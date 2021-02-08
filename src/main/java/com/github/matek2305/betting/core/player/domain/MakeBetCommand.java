package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import lombok.Value;

@Value
class MakeBetCommand{
    PlayerId playerId;
    MatchId matchId;
    MatchScore bet;
}
