package com.pkaushik.safeHome.validation;

import com.pkaushik.safeHome.model.Schedule;

import java.util.Calendar;

public interface DateTimeValidationIF {
    void validateSchedule(Schedule schedule);
}
