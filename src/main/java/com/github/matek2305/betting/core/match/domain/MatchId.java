package com.github.matek2305.betting.core.match.domain;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class MatchId {
    UUID id;
}