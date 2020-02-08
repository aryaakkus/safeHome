package com.pkaushik.safeHome.service;

import com.pkaushik.safeHome.model.Assignment;
import com.pkaushik.safeHome.model.Schedule;
import com.pkaushik.safeHome.model.Walker;

import java.util.UUID;

public interface WalkerServiceIF {

    void setWalkerScheduleService(int mcgillID, Schedule schedule);

    void changeWalkerScheduleService(int mcgillID, Schedule schedule);


    void acceptAssignmentService(int mcgillID, UUID assignmentID);

    void refuseAssignmentService(int mcgillID, UUID assignmentID);

    void walkerIsWalksafeService(int mcgillID, boolean isWalksafe);

    void updateWalkerRatingService(int mcgillID, double newRating);

    void completeTripService(int mcgillID,UUID assignmentID, boolean wasSuccessful);

    Assignment getWalkerProposedAssignmentsService(Walker walkerRole);

}
