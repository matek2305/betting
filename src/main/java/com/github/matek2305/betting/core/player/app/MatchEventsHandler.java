package com.github.matek2305.betting.core.player.app;

import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.player.domain.RewardPlayers;
import com.github.matek2305.betting.core.player.domain.RewardPlayersCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class MatchEventsHandler {

    private RewardPlayers rewardPlayers;

    void handle(MatchFinished matchFinished) {
        rewardPlayers.give(new RewardPlayersCommand(
                matchFinished.matchId(),
                matchFinished.result(),
                matchFinished.rewards()));
    }
}
