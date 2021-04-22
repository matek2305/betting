package com.github.matek2305.betting.core.player.infrastructure;

import com.github.matek2305.betting.commons.EventsPublisher;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.player.domain.Player;
import com.github.matek2305.betting.core.player.domain.PlayerEvent;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetMade;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PointsRewarded;
import com.github.matek2305.betting.core.player.domain.PlayerId;
import com.github.matek2305.betting.core.player.domain.Players;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@ApplicationScoped
@RequiredArgsConstructor
public class InMemoryPlayers implements Players {

    private final Map<PlayerId, Player> players = new ConcurrentHashMap<>();

    private final EventsPublisher publisher;

    @Override
    public Player createWithId(PlayerId playerId) {
        return players.computeIfAbsent(playerId, Player::new);
    }

    @Override
    public Option<Player> findBy(PlayerId playerId) {
        return Option.of(players.get(playerId));
    }

    @Override
    public Set<Player> findByBetMatchId(MatchId matchId) {
        return players.values()
                .stream()
                .filter(player -> player.hasBetFor(matchId))
                .collect(Collectors.toSet());
    }

    @Override
    public void publish(PlayerEvent event) {
        Match(event).option(

                Case($(instanceOf(PlayerBetMade.class)), this::savePlayerBet),
                Case($(instanceOf(PointsRewarded.class)), this::rewardPoints)

        ).forEach(player -> players.put(player.playerId(), player));

        publisher.publish(event);
    }

    private Player savePlayerBet(PlayerBetMade playerBetMade) {
        var player = players.get(playerBetMade.playerId());
        return player.handle(playerBetMade);
    }

    private Player rewardPoints(PointsRewarded pointsRewarded) {
        var player = players.get(pointsRewarded.playerId());
        return player.handle(pointsRewarded);
    }
}
