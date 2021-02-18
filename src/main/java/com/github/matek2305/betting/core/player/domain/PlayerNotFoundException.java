package com.github.matek2305.betting.core.player.domain;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(PlayerId playerId) {
        super("Player not found by " + playerId);
    }
}
