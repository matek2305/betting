package com.github.matek2305.betting.core.match.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.core.match.infrastructure.InMemoryMatchRepository
import com.github.matek2305.betting.date.DateProvider
import spock.lang.Subject

import java.time.ZonedDateTime

import static com.github.matek2305.betting.core.match.domain.CreateIncomingMatchPolicy.atLeastOneHourBeforeMatchStart
import static java.time.ZonedDateTime.parse

class CreateIncomingMatchTest extends DomainSpecification implements MatchFixtures {

    def dateProviderMock = Mock(DateProvider)

    def matches = withEventsPublisher({
        new InMemoryMatchRepository(new MatchBettingPolicy.Default(dateProviderMock), it, dateProviderMock)
    })

    @Subject
    def createIncomingMatch = new CreateIncomingMatch(
            atLeastOneHourBeforeMatchStart(dateProviderMock), matches)

    def "should create incoming match"() {
        given:
            setCurrentDateTime(parse('2021-02-13T15:14Z'))
        
        and:
            def command = new CreateIncomingMatchCommand(
                    parse('2021-02-13T17:30Z'),
                    randomTeam(),
                    randomTeam())
        
        when:
            createIncomingMatch.create(command)
        
        then:
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
            def command = new CreateIncomingMatchCommand(
                    parse('2021-02-13T16:00Z'),
                    randomTeam(),
                    randomTeam())
    
        when:
            createIncomingMatch.create(command)
    
        then:
            def ex = thrown(IllegalArgumentException)
            ex.message.contains('Create incoming match rejected')
    }
    
    private void setCurrentDateTime(ZonedDateTime dateTime) {
        dateProviderMock.getCurrentDateTime() >> dateTime
    }
}
