package com.github.matek2305.betting.core.room.domain;

import com.github.matek2305.betting.commons.DateProvider;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

public interface BettingRoomPolicy {

    boolean check(AddIncomingMatchCommand command);

    String getRuleDescription();

    static BettingRoomPolicy atLeastOneHourBeforeMatchStart(DateProvider dateProvider) {
        return new RequiredTimeBeforeMatchStart(dateProvider, Duration.ofHours(1));
    }

    @RequiredArgsConstructor
    class RequiredTimeBeforeMatchStart implements BettingRoomPolicy {

        private final DateProvider dateProvider;
        private final TemporalAmount requiredTimeBeforeMatchStart;

        @Override
        public boolean check(AddIncomingMatchCommand command) {
            return dateProvider.getCurrentDateTime().plus(requiredTimeBeforeMatchStart)
                    .isBefore(command.startDateTime());
        }

        @Override
        public String getRuleDescription() {
            return "Only matches that starts in one hour at the latest can be added for betting.";
        }
    }
}
