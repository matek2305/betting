package com.github.matek2305.betting.core.player.app;

import com.github.matek2305.betting.core.match.domain.MatchEvent.MatchFinished;
import com.github.matek2305.betting.core.player.domain.RewardPlayers;
import com.github.matek2305.betting.core.player.domain.RewardPlayersCommand;
import io.quarkus.vertx.ConsumeEvent;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
class MatchFinishedEventHandler {

    private final RewardPlayers rewardPlayers;

    @ConsumeEvent(value = "matches")
    public void handle(MatchFinished matchFinished) {
        rewardPlayers.give(toCommand(matchFinished));
    }

    private RewardPlayersCommand toCommand(MatchFinished matchFinished) {
        return new RewardPlayersCommand(
                matchFinished.matchId(),
                matchFinished.result(),
                matchFinished.rewards()
        );
    }
}
