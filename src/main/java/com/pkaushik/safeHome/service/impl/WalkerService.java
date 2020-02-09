package com.pkaushik.safeHome.service.impl;

import com.pkaushik.safeHome.SafeHomeApplication;
import com.pkaushik.safeHome.model.*;
import com.pkaushik.safeHome.model.enumerations.RequestStatus;
import com.pkaushik.safeHome.model.enumerations.WalkerStatus;
import com.pkaushik.safeHome.repository.WalkerRepository;
import com.pkaushik.safeHome.service.WalkerServiceIF;
import com.pkaushik.safeHome.validation.DateTimeValidationIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.UUID;

@Service
public class WalkerService implements WalkerServiceIF {

    @Autowired
    private WalkerRepository walkerRepo;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private DateTimeValidationIF dateValidator;

    @Override
    public void setWalkerScheduleService(int mcgillID, Schedule schedule) {

        if(!walkerRepo.existsById(mcgillID)) throw new EntityNotFoundException("Walker must exist in DB");

        if (SafeHomeUser.getUser(mcgillID) != null && SafeHomeUser.getUser(mcgillID).getRoles() != null) {
            Walker walkerRole = (Walker) (SafeHomeUser.getUser(mcgillID)).getRoles().stream()
                    .filter((role) -> (role instanceof Walker))
                    .findAny()
                    .orElse(null);

            if (walkerRole == null)
                throw new IllegalStateException("The walker whose schedule you are trying to set does not exist");

            walkerRole.setSchedule(schedule);
            walkerRepo.save(walkerRole);
        }
    }

    @Override
    public void changeWalkerScheduleService(int mcgillID, Schedule newSchedule) {

        if(!walkerRepo.existsById(mcgillID)) throw new EntityNotFoundException("Walker must exist in DB");

        if (SafeHomeUser.getUser(mcgillID) != null && SafeHomeUser.getUser(mcgillID).getRoles() != null) {
            Walker walkerRole = (Walker) (SafeHomeUser.getUser(mcgillID)).getRoles().stream()
                    .filter((role) -> (role instanceof Walker))
                    .findAny()
                    .orElse(null);

            if (walkerRole == null)
                throw new IllegalStateException("The walker whose schedule you are trying to set does not exist");


            Schedule currentSchedule = walkerRole.getSchedule();

            if (currentSchedule == null)
                throw new IllegalStateException("No schedule exists to update. Please create a schedule first");

            walkerRole.setSchedule(newSchedule);
        }
    }

    @Override
    public void acceptAssignmentService(int mcgillID, UUID assignmentID) {

        Walker walker = (Walker) Walker.getRole(mcgillID);

        if(!walker.getStatus().equals(WalkerStatus.SELECTED))
            throw new IllegalStateException(" Walker must be selected before accepting an assignment");

        Assignment assignmentForWalker = assignmentForWalker = assignmentService.findAssignmentByUUIDService(assignmentID);

        if(assignmentForWalker==null) throw new IllegalStateException("Assignment with this id does not exist!");

        //found assignment.
        if(assignmentForWalker.hasAccepted()) throw new IllegalStateException("The assignment has to be created and open to accept it");

        SpecificRequest requestForAssignment = assignmentForWalker.getRequest();
        if(requestForAssignment == null) throw new IllegalStateException("A request must be created to accept assignment");

        //assignment operations
        assignmentService.acceptAssignmentByWalkerService(assignmentForWalker);

        //walker operations
        walker.setCurrentAssignment(assignmentForWalker);
        walker.setStatus(WalkerStatus.ASSIGNED);

        assignmentService.save(assignmentForWalker);

        //TODO: student will have to be notified on this.
        //idea: create a url based on assignment uuid that fe will make async requests to
        //we will update what that finds here.
    }

    @Override
    public void refuseAssignmentService(int mcgillID, UUID assignmentID) {

        Walker walker = (Walker) Walker.getRole(mcgillID);

        Assignment assignmentForWalker = assignmentForWalker = assignmentService.findAssignmentByUUIDService(assignmentID);

        if(assignmentForWalker==null) throw new IllegalStateException("Assignment with this id does not exist!");

        SpecificRequest requestForAssignment = assignmentForWalker.getRequest();

        //assignment operation
        assignmentForWalker.isAccepted(false);

        //TODO: ping student to select another walker
    }

    @Override
    public void walkerIsWalksafeService(int mcgillID, boolean isWalksafe) {

        Walker walker = (Walker) Walker.getRole(mcgillID); //might have to get from db?

        if(walker == null)
            throw new IllegalStateException("No walker with ID exists");

        walker.setWalksafe(isWalksafe);
        walkerRepo.save(walker);
    }

    @Override
    public void updateWalkerRatingService(int mcgillID, double newRating) {
        Walker walker = (Walker) Walker.getRole(mcgillID); //might have to get from db?

        if(walker == null)
            throw new IllegalStateException("No walker with ID exists");

        walker.setRating(newRating);
        walkerRepo.save(walker);
    }

    @Override
    public void completeTripService(int mcgillID, UUID assignmentID, boolean wasSuccessful){
        Walker walker = (Walker) Walker.getRole(mcgillID);

        if(walker==null){
            throw new IllegalStateException("No walker with ID exists");
        }

        Assignment assignment = assignmentService.findAssignmentByUUIDService(assignmentID);

        if(!walker.getCurrentAssignment().equals(assignment)){
            throw new IllegalStateException("The assignment passed is not for this walker");
        }

        if(!wasSuccessful) assignmentService.cancelAssignmentService(assignment);

        if(walker.getStatus().equals(WalkerStatus.INACTIVE) || walker.getStatus().equals(WalkerStatus.LOGGED_IN))
            throw new IllegalStateException("The walker has either not accepted an assignment, or has already completed the trip");

        //complete trip
        walker.setStatus(WalkerStatus.LOGGED_IN);
        //TODO: set assignment status.
        walker.getCurrentAssignment().getRequest().setRequestStatus(RequestStatus.COMPLETE);
        walker.getCurrentAssignment().getRequest().getStudent().getPastRequests().add(walker.getCurrentAssignment().getRequest());
        walker.getCurrentAssignment().getRequest().getStudent().setRequest(null);
        walker.setCurrentAssignment(null);

        assignmentService.save(assignment);
        walkerRepo.save(walker);
    }

    @Override
    @Async
    public Assignment getWalkerProposedAssignmentsService(Walker walkerRole) {
        Assignment assignment = null;
        //stream through mapentry set.. find if any are for walker.
        for(Map.Entry<Assignment, Walker> entry : SafeHomeApplication.getOpenAssignmentsMap().entrySet()){
            //if any of the walker instances are equal to the one we want, return.
            if(entry.getValue().equals(walkerRole)){
                assignment = entry.getKey();
                break;
                //found assignment.
            }
        }
        return assignment;
    }

}