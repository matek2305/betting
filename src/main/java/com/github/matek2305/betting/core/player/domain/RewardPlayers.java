package com.github.matek2305.betting.core.player.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RewardPlayers {

    private final Players players;

    public void give(RewardPlayersCommand command) {
        players.findByBetMatchId(command.matchId())
                .stream()
                .map(player -> player.rewardPoints(command))
                .forEach(players::publish);
    }
}
