package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import lombok.Value;

@Value
class MakeBetCommand{
    PlayerId playerId;
    MatchId matchId;
    MatchScore bet;
}
