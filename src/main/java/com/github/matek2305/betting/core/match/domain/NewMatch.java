package com.github.matek2305.betting.core.match.domain;

import java.time.ZonedDateTime;

public interface NewMatch extends Match {

    ZonedDateTime bettingAvailableUntil();
}
