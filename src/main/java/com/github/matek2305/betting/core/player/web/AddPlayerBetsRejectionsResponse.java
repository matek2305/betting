package com.github.matek2305.betting.core.player.web;

import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
class AddPlayerBetsRejectionsResponse {
    Set<BetRejection> rejections;

    @Value
    static class BetRejection {
        UUID matchId;
        String error;
    }
}
