package com.github.matek2305.betting.core.match.domain;

import com.github.matek2305.betting.date.DateProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchBettingPolicies {

    private final DateProvider dateProvider;

    public MatchBettingPolicy bettingAllowedBeforeMatchStartOnly() {
        return new MatchBettingPolicy.Default(dateProvider);
    }
}
