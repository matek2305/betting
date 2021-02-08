package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.date.DateProvider;
import lombok.RequiredArgsConstructor;

interface MatchBettingPolicy {

    boolean playerCanBet(IncomingMatch match);

    @RequiredArgsConstructor
    class Default implements MatchBettingPolicy {

        private final DateProvider dateProvider;

        @Override
        public boolean playerCanBet(IncomingMatch match) {
            return dateProvider.getCurrentDateTime()
                    .isBefore(match.startDateTime());
        }
    }
}
