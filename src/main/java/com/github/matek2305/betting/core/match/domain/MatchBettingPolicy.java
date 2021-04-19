package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.commons.DateProvider;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

public interface MatchBettingPolicy {

    ZonedDateTime calculateMarginDate(IncomingMatch incomingMatch);

    boolean playerCanBet(IncomingMatch match);

    static MatchBettingPolicy bettingAllowedBeforeMatchStartOnly(DateProvider dateProvider) {
        return new Default(dateProvider);
    }

    @RequiredArgsConstructor
    class Default implements MatchBettingPolicy {

        private final DateProvider dateProvider;

        @Override
        public ZonedDateTime calculateMarginDate(IncomingMatch incomingMatch) {
            return incomingMatch.startDateTime();
        }

        @Override
        public boolean playerCanBet(IncomingMatch match) {
            return dateProvider.getCurrentDateTime().isBefore(calculateMarginDate(match));
        }
    }
}
