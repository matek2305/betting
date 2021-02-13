package com.github.matek2305.betting.core.player.domain;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class PlayerId {
    UUID id;
}
