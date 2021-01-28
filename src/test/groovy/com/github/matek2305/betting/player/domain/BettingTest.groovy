package com.github.matek2305.betting.player.domain

import com.github.matek2305.betting.date.DateProvider
import com.github.matek2305.betting.match.domain.IncomingMatch
import com.github.matek2305.betting.match.domain.Match
import com.github.matek2305.betting.match.domain.MatchId
import com.github.matek2305.betting.match.domain.MatchInformation
import com.github.matek2305.betting.match.domain.MatchRivals
import com.github.matek2305.betting.match.domain.MatchScore
import com.github.matek2305.betting.match.domain.PlayerBets
import com.github.matek2305.betting.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.player.infrastructure.InMemoryPlayers
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import spock.lang.Specification
import spock.lang.Subject

import java.time.ZonedDateTime

class BettingTest extends Specification {
    
    def dateProviderMock = Mock(DateProvider)
    
    def matches = new InMemoryMatchRepository()
    def players = new InMemoryPlayers()
    
    @Subject
    def betting = new Betting(matches, players)
    
    def "player should be able to bet on incoming match"() {
        given:
            def currentDateTime = ZonedDateTime.parse('2021-01-31T18:02Z')
            dateProviderMock.getCurrentDateTime() >> currentDateTime
        
        and:
            def matchStartDateTime = ZonedDateTime.parse('2021-02-02T21:00Z')
            def match = incomingMatch(matchStartDateTime)
            def player = playerWithDefaultBettingPolicy()
        
        when:
            def result = makeBet(player, match)
        
        then:
            result.class == PlayerEvent.PlayerBetMade
    }
    
    private PlayerEvent makeBet(Player player, Match match) {
        return betting.makeBet(new MakeBetCommand(player.playerId(), match.matchId(), randomScore()))
    }
    
    private Player playerWithDefaultBettingPolicy() {
        def player = new Player(randomPlayerId(), new BettingPolicy.Default(dateProviderMock))
        players.save(player)
        return player
    }
    
    private static PlayerId randomPlayerId() {
        return new PlayerId(UUID.randomUUID())
    }
    
    private IncomingMatch incomingMatch(ZonedDateTime startDateTime) {
        def match = new IncomingMatch(
                new MatchInformation(
                        randomMatchId(),
                        startDateTime,
                        randomRivals()),
                new PlayerBets())
        matches.save(match)
        return match
    }
    
    private static MatchId randomMatchId() {
        return new MatchId(UUID.randomUUID())
    }
    
    private static MatchRivals randomRivals() {
        return new MatchRivals(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5))
    }
    
    private static MatchScore randomScore() {
        return new MatchScore(RandomUtils.nextInt(0, 3), RandomUtils.nextInt(0, 3))
    }
}
