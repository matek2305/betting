package com.github.matek2305.betting.livescore;

import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;

@Value
public class FootballApiFixtureListResponse {
    List<Entry> response;

    @Value
    public static class Entry {
        Fixture fixture;
        Teams teams;
    }

    @Value
    public static class Fixture {
        int id;
        ZonedDateTime date;
    }

    @Value
    public static class Teams {
        Team home;
        Team away;
    }

    @Value
    public static class Team {
        String name;
    }
}
