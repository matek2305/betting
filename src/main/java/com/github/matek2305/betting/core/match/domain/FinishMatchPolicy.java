package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.commons.DateProvider;
import lombok.RequiredArgsConstructor;

public interface FinishMatchPolicy {

    boolean check(IncomingMatch match);

    String getRuleDescription();

    static FinishMatchPolicy afterMatchStarted(DateProvider dateProvider) {
        return new AfterMatchStarted(dateProvider);
    }

    @RequiredArgsConstructor
    class AfterMatchStarted implements FinishMatchPolicy {

        private final DateProvider dateProvider;

        @Override
        public boolean check(IncomingMatch match) {
            return dateProvider.getCurrentDateTime()
                    .isAfter(match.startDateTime());
        }

        @Override
        public String getRuleDescription() {
            return "Match cannot be finished before it starts.";
        }
    }
}
