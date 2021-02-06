package com.github.matek2305.betting.player.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import com.github.matek2305.betting.player.domain.*;
import com.github.matek2305.betting.player.domain.PlayerEvent.NewPlayerCreated;
import com.github.matek2305.betting.player.domain.PlayerEvent.PlayerBetMade;
import com.google.common.collect.ImmutableMap;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
public class InMemoryPlayers implements Players {

    private final Map<PlayerId, Player> players = new ConcurrentHashMap<>();

    private final EventsPublisher publisher;

    @Override
    public Option<Player> findBy(PlayerId playerId) {
        return Option.of(players.get(playerId));
    }

    @Override
    public Player publish(PlayerEvent event) {
        var player = Match(event).of(
                Case($(instanceOf(NewPlayerCreated.class)), this::saveNewPlayer),
                Case($(), this::handleNextEvent));

        publisher.publish(event);
        return player;
    }

    private Player saveNewPlayer(NewPlayerCreated newPlayerCreated) {
        return players.computeIfAbsent(newPlayerCreated.playerId(), Player::new);
    }

    private Player handleNextEvent(PlayerEvent event) {
        return Match(event).of(
                Case($(instanceOf(PlayerBetMade.class)), this::savePlayerBet),
                Case($(), () -> players.get(event.playerId()))
        );
    }

    private Player savePlayerBet(PlayerBetMade playerBetMade) {
        return players.computeIfPresent(playerBetMade.playerId(), ((playerId, player) -> {
            Map<MatchId, MatchScore> matchBets = new ImmutableMap.Builder<MatchId, MatchScore>()
                    .putAll(player.bets().bets())
                    .put(playerBetMade.matchId(), playerBetMade.bet())
                    .build();
            return new Player(playerId, new PlayerBets(matchBets));
        }));
    }
}
