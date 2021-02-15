package com.github.matek2305.betting.core.room.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.domain.MatchEvent
import com.github.matek2305.betting.core.match.domain.MatchFixtures
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.core.room.domain.AddIncomingMatch
import com.github.matek2305.betting.core.room.domain.AddIncomingMatchCommand
import com.github.matek2305.betting.core.room.infrastructure.InMemoryBettingRoomRepository
import com.github.matek2305.betting.date.DateProvider
import spock.lang.Subject

import java.time.ZonedDateTime

import static java.time.ZonedDateTime.parse

class AddIncomingMatchTest extends DomainSpecification implements MatchFixtures {

    def dateProviderMock = Mock(DateProvider)

    def matches = withEventsPublisher({new InMemoryMatchRepository(it, dateProviderMock) })

    @Subject
    def createIncomingMatch = new AddIncomingMatch(new InMemoryBettingRoomRepository(dateProviderMock), matches)

    def "should create incoming match"() {
        given:
            setCurrentDateTime(parse('2021-02-13T15:14Z'))
        
        and:
            def command = new AddIncomingMatchCommand(
                    parse('2021-02-13T17:30Z'),
                    randomTeam(),
                    randomTeam())
    
        when:
            def result = createIncomingMatch.add(command)
    
        then:
            result.isSuccess()
        
        and:
            def event = findPublishedEvent(MatchEvent.IncomingMatchCreated)
            event.matchId() != null
            event.startDateTime() == command.startDateTime()
            event.homeTeam() == command.homeTeam()
            event.awayTeam() == command.awayTeam()
    }

    def "should reject creation if match starts in less than hour"() {
        given:
            setCurrentDateTime(parse('2021-02-13T15:14Z'))
    
        and:
            def command = new AddIncomingMatchCommand(
                    parse('2021-02-13T16:00Z'),
                    randomTeam(),
                    randomTeam())
    
        when:
            def result = createIncomingMatch.add(command)
    
        then:
            result.isFailure()
        
        and:
            result.cause.message.contains('one hour')
    }
    
    private void setCurrentDateTime(ZonedDateTime dateTime) {
        dateProviderMock.getCurrentDateTime() >> dateTime
    }
}
