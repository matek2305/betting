package com.github.matek2305.betting.core.player.domain;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class PlayerId {
    @NonNull String id;
}
