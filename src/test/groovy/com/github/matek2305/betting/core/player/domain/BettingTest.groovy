package com.github.matek2305.betting.core.player.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.domain.MatchBettingPolicy
import com.github.matek2305.betting.core.match.domain.Team
import com.github.matek2305.betting.date.DateProvider

import com.github.matek2305.betting.core.match.domain.MatchEvent
import com.github.matek2305.betting.core.match.domain.MatchId
import com.github.matek2305.betting.core.match.domain.MatchScore
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.core.player.infrastructure.InMemoryPlayers
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import spock.lang.Subject

import java.time.ZonedDateTime

import static java.time.ZonedDateTime.parse

class BettingTest extends DomainSpecification {
    
    def dateProviderMock = Mock(DateProvider)
    
    def matches = withEventsPublisher({
        new InMemoryMatchRepository(new MatchBettingPolicy.Default(dateProviderMock), it, dateProviderMock)
    })
    
    def players = withEventsPublisher({ new InMemoryPlayers(it) })
    
    @Subject
    def betting = new Betting(matches, players)
    
    def "player should be able to bet on incoming match"() {
        given:
            setCurrentDateTime(parse('2021-01-31T18:02Z'))
        
        and:
            def matchStartDateTime = parse('2021-02-02T21:00Z')
            def matchId = randomIncomingMatch(matchStartDateTime)
            def playerId = randomPlayer()
        
        when:
            def bet = makeBet(playerId, matchId)
        
        then:
            def event = findPublishedEvent(PlayerEvent.PlayerBetMade)
            event.matchId() == matchId
            event.playerId() == playerId
            event.bet() == bet
    }
    
    def "player should not be able to bet on match when it has started"() {
        given:
            setCurrentDateTime(parse('2021-02-02T21:02Z'))
        
        and:
            def matchStartDateTime = parse('2021-02-02T21:00Z')
            def matchId = randomIncomingMatch(matchStartDateTime)
            def playerId = randomPlayer()
        
        when:
            makeBet(playerId, matchId)
        
        then:
            def event = findPublishedEvent(PlayerEvent.PlayerBetRejected)
            event.matchId() == matchId
            event.playerId() == playerId
    }
    
    private void setCurrentDateTime(ZonedDateTime dateTime) {
        dateProviderMock.getCurrentDateTime() >> dateTime
    }
    
    private MatchScore makeBet(PlayerId playerId, MatchId matchId) {
        def bet = randomScore()
        betting.makeBet(new MakeBetCommand(playerId, matchId, bet))
        return bet
    }
    
    private PlayerId randomPlayer() {
        var playerId = randomPlayerId()
        players.publish(new PlayerEvent.NewPlayerCreated(playerId))
        return playerId
    }
    
    private MatchId randomIncomingMatch(ZonedDateTime startDateTime) {
        def matchId = randomMatchId()
        matches.publish(new MatchEvent.IncomingMatchCreated(matchId, startDateTime, randomTeam(), randomTeam()))
        return matchId
    }
    
    private static PlayerId randomPlayerId() {
        return PlayerId.of(UUID.randomUUID())
    }
    
    private static MatchId randomMatchId() {
        return MatchId.of(UUID.randomUUID())
    }
    
    private static MatchScore randomScore() {
        return MatchScore.of(RandomUtils.nextInt(0, 3), RandomUtils.nextInt(0, 3))
    }

    private static Team randomTeam() {
        return Team.of(RandomStringUtils.randomAlphabetic(10))
    }
}