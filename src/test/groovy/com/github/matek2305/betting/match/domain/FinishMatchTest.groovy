package com.github.matek2305.betting.match.domain

import com.github.matek2305.betting.commons.DomainSpecification
import com.github.matek2305.betting.date.DateProvider
import com.github.matek2305.betting.match.infrastructure.InMemoryMatchRepository
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import spock.lang.Subject

import java.time.ZonedDateTime

import static java.time.ZonedDateTime.parse

class FinishMatchTest extends DomainSpecification {
    
    def dateProviderMock = Mock(DateProvider)
    
    def matches = withEventsPublisher({
        new InMemoryMatchRepository(new MatchBettingPolicies(dateProviderMock), it)
    })
    
    @Subject
    def finishMatch = new FinishMatch(matches, matches)
    
    def "should finish incoming match"() {
        given:
            def match = incomingMatchWithDefaultPolicy(parse('2021-02-06T13:30Z'))
        
        when:
            def result = finishMatch(match)
        
        then:
            def event = findPublishedEvent(MatchEvent.MatchFinished)
            event.matchId() == match.matchId()
            event.result() == result
        
        and:
            def defaultRewards = MatchRewardingPolicy.defaultRewards()
            event.rewards().pointsForExactResultHit() == defaultRewards.pointsForExactResultHit
            event.rewards().pointsForWinningTeamHit() == defaultRewards.pointsForWinningTeamHit
            event.rewards().pointsForDrawHit() == defaultRewards.pointsForDraw
            event.rewards().pointsForMissingBet() == defaultRewards.pointsForMissingBet
    }
    
    private MatchScore finishMatch(Match match) {
        def command = new FinishMatchCommand(match.matchId(), randomScore())
        finishMatch.finishMatch(command)
        return command.result()
    }
    
    private Match incomingMatchWithDefaultPolicy(ZonedDateTime startDateTime) {
        return matches.publish(new MatchEvent.NewMatchAdded(randomMatchId(), startDateTime, randomRivals()));
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
