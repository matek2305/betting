package com.github.matek2305.betting.player.domain;

import io.vavr.control.Option;

public interface Players {

    Option<Player> findBy(PlayerId playerId);

    Player publish(PlayerEvent result);
}
