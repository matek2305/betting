package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.core.match.domain.MatchRepository;
import com.github.matek2305.betting.core.room.domain.AddIncomingMatch;
import com.github.matek2305.betting.core.room.domain.BettingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Slf4j
@Dependent
@RequiredArgsConstructor
class MatchContextConfiguration {

    @Produces
    public AddIncomingMatch createIncomingMatch(
            BettingRoomRepository bettingRoomRepository, MatchRepository repository) {
        return new AddIncomingMatch(bettingRoomRepository, repository);
    }
}
