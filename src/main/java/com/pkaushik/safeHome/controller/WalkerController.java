package com.pkaushik.safeHome.controller;

import com.pkaushik.safeHome.SafeHomeApplication;
import com.pkaushik.safeHome.exceptions.DateConventionException;
import com.pkaushik.safeHome.exceptions.ScheduleDateTimeException;
import com.pkaushik.safeHome.model.Schedule;
import com.pkaushik.safeHome.service.WalkerServiceIF;
import com.pkaushik.safeHome.service.impl.WalkerService;
import com.pkaushik.safeHome.validation.DateTimeValidationIF;
import com.pkaushik.safeHome.validation.InputValidationIF;
import com.pkaushik.safeHome.validation.WalkerPropertiesValidationIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

import static com.pkaushik.safeHome.utils.JsonResponseConstants.JSON_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api")
public class WalkerController {

    @Autowired
    private WalkerServiceIF walkerService;

    @Autowired
    private DateTimeValidationIF dateValidator;

    @Autowired
    private InputValidationIF inputValidator;

    @Autowired
    private WalkerPropertiesValidationIF walkerPropertiesValidationIF;

    @PostMapping(value="/acceptAssignment")
    public ResponseEntity<String> acceptAssignment(@RequestParam(name="mcgillID") int mcgillID,
                                                   @RequestParam(name="assignmentID") UUID assignmentID){
        try{
            inputValidator.validateMcgillID(mcgillID);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try{
            walkerService.acceptAssignmentService(mcgillID, assignmentID);
        }
        catch(IllegalAccessError e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PostMapping("/refuseAssignment")
    public ResponseEntity<String> refuseAssignment(@RequestParam(name="mcgillID") int mcgillID,
                                 @RequestParam(name="assignmentID") UUID assignmentID){
        try{
            inputValidator.validateMcgillID(mcgillID);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try{
            walkerService.refuseAssignmentService(mcgillID, assignmentID);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PostMapping("/setSchedule/{mcgillID}")
    public ResponseEntity<String> setWalkerSchedule(@PathVariable(name="mcgillID") int mcgillID, @RequestBody Schedule schedule) {

        try{
            dateValidator.validateSchedule(schedule);
        } catch(DateConventionException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(ScheduleDateTimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try{
            walkerService.setWalkerScheduleService(mcgillID, schedule);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PostMapping("/changeSchedule/{mcgillID}")
    public ResponseEntity<String> changeWalkerSchedule(@PathVariable(name="mcgillID") int mcgillID, @RequestBody Schedule schedule) {
        try{
            dateValidator.validateSchedule(schedule);
        }
        catch(DateConventionException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch(ScheduleDateTimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try{
            walkerService.changeWalkerScheduleService(mcgillID, schedule);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PostMapping(value="/walkertowalksafe/{mcgillID}")
    public ResponseEntity<String> walkerIsWalksafe(@PathVariable("mcgillID") int mcgillID, @RequestParam boolean isWalksafe){

        try{
            inputValidator.validateMcgillID(mcgillID);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try{
            walkerService.walkerIsWalksafeService(mcgillID, isWalksafe);
        }
        catch(IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PostMapping(value="/updateRating/{mcgillID}")
    public ResponseEntity<String> updateWalkerRating(@PathVariable(name="mcgillID") int mcgillID, @RequestParam double newRating){

        try{
            inputValidator.validateMcgillID(mcgillID);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if(walkerPropertiesValidationIF.validateRating(newRating)){
            try{
                walkerService.updateWalkerRatingService(mcgillID, newRating);
            }
            catch(IllegalStateException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else{
            //tell fe that rating is not valid.
        }

        return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
    }

    @PostMapping(value = "/completeTrip/{mcgillID}/{assignmentID}")
    public ResponseEntity<String> walkerCompleteTrip(@PathVariable(name="mcgillID") int mcgillID, @PathVariable(name="assignmentID") UUID assignmentID, @RequestParam boolean wasSuccessful){

        try{
            inputValidator.validateMcgillID(mcgillID);
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try{
            walkerService.completeTripService(mcgillID, assignmentID, wasSuccessful);
        }
        catch(IllegalStateException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
    }
}
