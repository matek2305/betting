package com.github.matek2305.betting.match.domain;

public interface MatchRewardingPolicy {

    int getPointsForExactResultHit();

    int getPointsForWinningTeamHit();

    int getPointsForDraw();

    int getPointsForMissingBet();

    static MatchRewardingPolicy defaultRewards() {
        return new Default();
    }

    class Default implements MatchRewardingPolicy {

        @Override
        public int getPointsForExactResultHit() {
            return 5;
        }

        @Override
        public int getPointsForWinningTeamHit() {
            return 2;
        }

        @Override
        public int getPointsForDraw() {
            return 3;
        }

        @Override
        public int getPointsForMissingBet() {
            return 0;
        }
    }
}
