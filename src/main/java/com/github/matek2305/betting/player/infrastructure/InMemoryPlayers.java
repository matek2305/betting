package com.github.matek2305.betting.player.infrastructure;

import com.github.matek2305.betting.player.domain.*;
import io.vavr.control.Option;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPlayers implements Players {

    private Map<PlayerId, Player> players = new ConcurrentHashMap<>();

    private Map<Class<? extends PlayerEvent>, List<PlayerEventListener<PlayerEvent>>> listeners
            = new ConcurrentHashMap<>();

    @Override
    public Option<Player> findBy(PlayerId playerId) {
        return Option.of(players.get(playerId));
    }

    @Override
    public void save(Player player) {
        players.put(player.playerId(), player);
    }

    @Override
    public void publish(PlayerEvent result) {
        listeners.getOrDefault(result.getClass(), Collections.emptyList())
                .forEach(listener -> listener.handle(result));
    }
}
