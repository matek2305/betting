package com.github.matek2305.betting.player.domain;

public interface PlayerEventListener<T extends PlayerEvent> {

    void handle(T event);
}
