package com.github.matek2305.betting.player.domain;

import io.vavr.control.Option;

public interface Players {

    Option<Player> findBy(PlayerId playerId);

    void save(Player player);

    void publish(PlayerEvent result);
}
