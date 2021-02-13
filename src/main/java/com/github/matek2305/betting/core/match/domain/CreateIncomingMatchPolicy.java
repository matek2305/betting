package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.date.DateProvider;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

public interface CreateIncomingMatchPolicy {

    boolean canBeCreated(CreateIncomingMatchCommand command);

    static CreateIncomingMatchPolicy atLeastOneHourBeforeMatchStart(DateProvider dateProvider) {
        return new AtLeastSpecifiedTimeBeforeMatchStart(dateProvider, Duration.ofHours(1));
    }

    @RequiredArgsConstructor
    class AtLeastSpecifiedTimeBeforeMatchStart implements CreateIncomingMatchPolicy {

        private final DateProvider dateProvider;
        private final TemporalAmount requiredTimeBeforeMatchStart;

        @Override
        public boolean canBeCreated(CreateIncomingMatchCommand command) {
            return dateProvider.getCurrentDateTime().plus(requiredTimeBeforeMatchStart)
                    .isBefore(command.startDateTime());
        }
    }
}
