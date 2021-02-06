package com.github.matek2305.betting.player.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RewardPlayers {

    private final Players players;

    public void give(RewardPlayersCommand command) {
        players
                .findByBetMatchId(command.matchId())
                .forEach(player -> players.publish(player.rewardPoints(command)));
    }
}
