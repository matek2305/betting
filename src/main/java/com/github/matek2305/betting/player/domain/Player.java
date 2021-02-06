package com.github.matek2305.betting.player.domain;

import com.github.matek2305.betting.match.domain.IncomingMatch;
import com.github.matek2305.betting.match.domain.MatchId;
import com.github.matek2305.betting.match.domain.MatchScore;
import com.github.matek2305.betting.player.domain.PlayerEvent.PointsRewarded;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

import static io.vavr.API.*;

@Getter
@RequiredArgsConstructor
public class Player {

    private final PlayerId playerId;
    private final PlayerBets bets;

    public Player(PlayerId playerId) {
        this(playerId, new PlayerBets());
    }

    PlayerEvent placeBet(IncomingMatch match, MatchScore bet) {
        if (match.isBettingAllowed()) {
            return betSuccessful(match.matchId(), bet);
        } else {
            return betRejected(match.matchId());
        }
    }

    PointsRewarded rewardPoints(RewardPlayersCommand command) {
        int scoredPoints = Match(bets.bets().get(command.matchId())).of(
                Case($(command.result()), () -> command.rewards().pointsForExactResultHit()),
                Case($(winningTeamHit(command.result())), () -> command.rewards().pointsForWinningTeamHit()),
                Case($(MatchScore::draw), () -> command.rewards().pointsForDrawHit()),
                Case($(), () -> command.rewards().pointsForMissingBet()));

        return new PointsRewarded(playerId, command.matchId(), scoredPoints);
    }

    private PlayerEvent betSuccessful(MatchId matchId, MatchScore bet) {
        return new PlayerEvent.PlayerBetMade(playerId, matchId, bet);
    }

    private PlayerEvent betRejected(MatchId matchId) {
        return new PlayerEvent.PlayerBetRejected(playerId, matchId);
    }

    private static Predicate<MatchScore> winningTeamHit(MatchScore result) {
        Predicate<MatchScore> homeTeamWin = bet -> bet.homeTeamWon() && result.homeTeamWon();
        Predicate<MatchScore> awayTeamWin = bet -> bet.awayTeamWon() && result.awayTeamWon();
        return homeTeamWin.or(awayTeamWin);
    }
}
