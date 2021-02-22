package com.github.matek2305.betting.core.player.domain;

import com.github.matek2305.betting.core.match.domain.IncomingMatch;
import com.github.matek2305.betting.core.match.domain.MatchId;
import com.github.matek2305.betting.core.match.domain.MatchScore;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetMade;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PlayerBetRejected;
import com.github.matek2305.betting.core.player.domain.PlayerEvent.PointsRewarded;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

@Getter
@RequiredArgsConstructor
public class Player {

    private final PlayerId playerId;

    @With
    private final PlayerBets bets;

    @With
    private final PlayerPoints points;

    public Player(PlayerId playerId) {
        this(playerId, new PlayerBets(), new PlayerPoints());
    }

    Either<PlayerBetRejected, PlayerBetMade> placeBet(IncomingMatch match, MatchScore bet) {
        if (match.isBettingAllowed()) {
            return right(betSuccessful(match.matchId(), bet));
        } else {
            return left(betRejected(match.matchId()));
        }
    }

    PointsRewarded rewardPoints(RewardPlayersCommand command) {
        Points scoredPoints = Match(bets.get(command.matchId())).of(
                Case($(command.result()), () -> command.rewards().pointsForExactResultHit()),
                Case($(winningTeamHit(command.result())), () -> command.rewards().pointsForWinningTeamHit()),
                Case($(draw(command.result())), () -> command.rewards().pointsForDrawHit()),
                Case($(), () -> command.rewards().pointsForMissingBet()));

        return new PointsRewarded(playerId, command.matchId(), scoredPoints);
    }

    public boolean hasBetFor(MatchId matchId) {
        return bets.exist(matchId);
    }

    public Player handle(PlayerBetMade playerBetMade) {
        return withBets(bets.with(playerBetMade.matchId(), playerBetMade.bet()));
    }

    public Player handle(PointsRewarded pointsRewarded) {
        var points = new BetPoints(bets.get(pointsRewarded.matchId()), pointsRewarded.points());
        return withBets(bets.without(pointsRewarded.matchId()))
                .withPoints(points.with(pointsRewarded.matchId(), points));
    }

    private PlayerBetMade betSuccessful(MatchId matchId, MatchScore bet) {
        return new PlayerBetMade(playerId, matchId, bet);
    }

    private PlayerBetRejected betRejected(MatchId matchId) {
        return new PlayerBetRejected(playerId, matchId, "Bet rejected due to match betting policy.");
    }

    private static Predicate<MatchScore> winningTeamHit(MatchScore result) {
        Predicate<MatchScore> homeTeamWin = bet -> bet.homeTeamWon() && result.homeTeamWon();
        Predicate<MatchScore> awayTeamWin = bet -> bet.awayTeamWon() && result.awayTeamWon();
        return homeTeamWin.or(awayTeamWin);
    }

    private static Predicate<MatchScore> draw(MatchScore result) {
        return bet -> bet.draw() && result.draw();
    }
}
