package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchRewards;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import lombok.Value;

@Value
public class RewardPlayersCommand {
    MatchId matchId;
    MatchScore result;
    MatchRewards rewards;
}
