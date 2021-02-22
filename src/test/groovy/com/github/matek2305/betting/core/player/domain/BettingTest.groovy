package com.github.matek2305.betting.core.player.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.RandomMatchFixtures
import com.github.matek2305.betting.core.match.domain.MatchId
import com.github.matek2305.betting.core.match.domain.MatchScore
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.core.player.infrastructure.InMemoryPlayers
import com.github.matek2305.betting.date.DateProvider
import spock.lang.Subject

import java.time.ZonedDateTime

import static java.time.ZonedDateTime.parse

class BettingTest extends DomainSpecification implements RandomMatchFixtures, PlayerFixtures {

    def dateProviderMock = Mock(DateProvider)

    def matches = withEventsPublisher({ new InMemoryMatchRepository(it, dateProviderMock) })

    def players = withEventsPublisher({ new InMemoryPlayers(it) })

    @Subject
    def betting = new Betting(matches, players)

    def "player should be able to bet on incoming match"() {
        given:
            setCurrentDateTime(parse('2021-01-31T18:02Z'))

        and:
            def matchStartDateTime = parse('2021-02-02T21:00Z')
            def matchId = createRandomIncomingMatch(matchStartDateTime)
            def playerId = createRandomPlayer()

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
            def matchId = createRandomIncomingMatch(matchStartDateTime)
            def playerId = createRandomPlayer()

        when:
            makeBet(playerId, matchId)

        then:
            def event = findPublishedEvent(PlayerEvent.PlayerBetRejected)
            event.matchId() == matchId
            event.playerId() == playerId
    }

    def "player should be able to bet more than once on the same match"() {
        given:
            setCurrentDateTime(parse('2021-01-31T18:02Z'))

        and:
            def matchStartDateTime = parse('2021-02-02T21:00Z')
            def matchId = createRandomIncomingMatch(matchStartDateTime)
            def playerId = createRandomPlayer()

        when:
            makeBet(playerId, matchId)

        and:
            makeBet(playerId, matchId)

        then:
            def events = findAllPublishedEvents(PlayerEvent.PlayerBetMade)
            events.size() == 2
    }

    private void setCurrentDateTime(ZonedDateTime dateTime) {
        dateProviderMock.getCurrentDateTime() >> dateTime
    }

    private MatchScore makeBet(PlayerId playerId, MatchId matchId) {
        def bet = randomMatchScore()
        betting.makeBet(new MakeBetCommand(playerId, matchId, bet))
        return bet
    }

    private PlayerId createRandomPlayer() {
        var playerId = randomPlayerId()
        players.createWithId(playerId)
        return playerId
    }

    private MatchId createRandomIncomingMatch(ZonedDateTime startDateTime) {
        def incomingMatch = randomIncomingMatch(startDateTime, dateProviderMock)
        matches.save(incomingMatch)
        return incomingMatch.matchId()
    }
}
