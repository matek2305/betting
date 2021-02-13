package com.github.matek2305.betting.core.player.domain

trait PlayerFixtures {

    PlayerId randomPlayerId() {
        return PlayerId.of(UUID.randomUUID())
    }
}