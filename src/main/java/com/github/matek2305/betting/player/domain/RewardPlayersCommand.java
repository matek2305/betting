package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchRewards;
import com.github.matek2305.betting.match.domain.MatchScore;
import lombok.Value;

@Value
public class RewardPlayersCommand {
    MatchId matchId;
    MatchScore result;
    MatchRewards rewards;
}
