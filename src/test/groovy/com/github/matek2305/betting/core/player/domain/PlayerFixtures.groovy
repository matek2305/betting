package com.github.matek2305.betting.core.player.domain

import org.apache.commons.lang3.RandomStringUtils

trait PlayerFixtures {

    PlayerId randomPlayerId() {
        return PlayerId.of(RandomStringUtils.randomAlphabetic(10))
    }
}