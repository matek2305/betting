package com.github.matek2305.betting.core.player.web;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@RegisterForReflection
class AddPlayerBetsRejectionsResponse {
    Set<BetRejection> rejections;

    @Value
    @RegisterForReflection
    static class BetRejection {
        UUID matchId;
        String error;
    }
}
