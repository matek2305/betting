package com.github.matek2305.betting.date;

import javax.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;

@ApplicationScoped
public class DateProvider {

    public ZonedDateTime getCurrentDateTime() {
        return ZonedDateTime.now();
    }
}
