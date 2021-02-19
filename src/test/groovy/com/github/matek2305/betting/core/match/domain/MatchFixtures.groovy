package com.github.matek2305.betting.core.match.domain


import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils

trait MatchFixtures {

    MatchId randomMatchId() {
        return MatchId.of(UUID.randomUUID())
    }

    MatchScore randomScore() {
        return MatchScore.of(RandomUtils.nextInt(0, 3), RandomUtils.nextInt(0, 3))
    }

    Team randomTeam() {
        return Team.of(RandomStringUtils.randomAlphabetic(10))
    }
    
    MatchRewards defaultRewards() {
        def rewardingPolicy = MatchRewardingPolicy.defaultRewards()
        return new MatchRewards(
                rewardingPolicy.pointsForExactResultHit,
                rewardingPolicy.pointsForWinningTeamHit,
                rewardingPolicy.pointsForDraw,
                rewardingPolicy.pointsForMissingBet
        )
    }
}