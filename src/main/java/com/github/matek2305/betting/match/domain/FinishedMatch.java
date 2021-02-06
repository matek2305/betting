package com.github.matek2305.betting.match.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FinishedMatch implements Match {

    @Getter
    private final MatchInformation matchInformation;
    private final MatchScore result;
}
