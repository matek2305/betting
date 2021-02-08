package com.github.matek2305.betting.match.domain;

import com.github.matek2305.betting.player.domain.Points;

public interface MatchRewardingPolicy {

    Points getPointsForExactResultHit();

    Points getPointsForWinningTeamHit();

    Points getPointsForDraw();

    Points getPointsForMissingBet();

    static MatchRewardingPolicy defaultRewards() {
        return new Default();
    }

    class Default implements MatchRewardingPolicy {

        @Override
        public Points getPointsForExactResultHit() {
            return Points.of(5);
        }

        @Override
        public Points getPointsForWinningTeamHit() {
            return Points.of(2);
        }

        @Override
        public Points getPointsForDraw() {
            return Points.of(3);
        }

        @Override
        public Points getPointsForMissingBet() {
            return Points.of(0);
        }
    }
}
