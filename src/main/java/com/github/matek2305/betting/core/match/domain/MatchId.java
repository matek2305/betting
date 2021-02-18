package com.github.matek2305.betting.core.match.domain;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class MatchId {
    @NonNull UUID id;
}
