package com.github.matek2305.betting.core.match.infrastructure;

import com.github.matek2305.betting.core.match.domain.BettingRoom;
import com.github.matek2305.betting.core.match.domain.BettingRooms;
import com.github.matek2305.betting.date.DateProvider;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

import static com.github.matek2305.betting.core.match.domain.BettingRoomPolicy.atLeastOneHourBeforeMatchStart;

@ApplicationScoped
@RequiredArgsConstructor
public class InMemoryBettingRooms implements BettingRooms {

    private final DateProvider dateProvider;

    @Override
    public BettingRoom getGlobal() {
        return new BettingRoom(atLeastOneHourBeforeMatchStart(dateProvider));
    }
}
