package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.date.DateProvider;
import com.github.matek2305.betting.match.domain.IncomingMatch;
import lombok.RequiredArgsConstructor;

interface BettingPolicy {

    boolean playerCanBet(IncomingMatch match);

    @RequiredArgsConstructor
    class Default implements BettingPolicy {

        private final DateProvider dateProvider;

        @Override
        public boolean playerCanBet(IncomingMatch match) {
            return dateProvider.getCurrentDateTime()
                    .isBefore(match.startDateTime());
        }
    }
}
