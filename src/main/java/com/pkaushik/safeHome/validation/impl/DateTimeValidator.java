package com.pkaushik.safeHome.validation.impl;

import com.pkaushik.safeHome.model.Schedule;
import com.pkaushik.safeHome.validation.DateTimeValidationIF;
import org.springframework.stereotype.Component;
import java.time.DateTimeException;

@Component
public class DateTimeValidator implements DateTimeValidationIF {

    @Override
    public void validateSchedule(Schedule schedule) {
        if(schedule.getStartDate().isAfter(schedule.getEndDate()))
            throw new DateTimeException("Start date can not be after end date!");
    }
}
