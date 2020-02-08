package com.pkaushik.safeHome.controller;

import com.pkaushik.safeHome.model.SpecificRequest;
import com.pkaushik.safeHome.service.RequestServiceIF;
import com.pkaushik.safeHome.validation.InputValidationIF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.pkaushik.safeHome.utils.JsonResponseConstants.JSON_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api")
public class RequestController {
    
    @Autowired
    private RequestServiceIF requestService;

    @Autowired 
    private InputValidationIF inputValidator;
    
//CRUD Request
	
/**
 * A request is created by the student for a walker. 
 * For MVP, assume all times are timestamped and are immediate.
 * @param mcgillID
 * @param pickupLatitude
 * @param pickupLongitude
 * @param destinationLatitude
 * @param destinationLongitude
 */
@PostMapping(value="/createRequest/{mcgillID}")
public ResponseEntity<Object> createSpecificRequest(@PathVariable(name="mcgillID") int mcgillID, @RequestParam(name="pickup_lat")  double pickupLatitude, @RequestParam(name="pickup_long") double pickupLongitude,
                                            @RequestParam(name="dest_lat") double destinationLatitude, @RequestParam(name="dest_long") double destinationLongitude) {
    
    //Validation
    try{
        inputValidator.validateMcgillID(mcgillID);
        inputValidator.validatePickup(pickupLatitude, pickupLongitude);
        inputValidator.validateDestination(destinationLatitude, destinationLongitude);
    }
    catch(IllegalArgumentException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    SpecificRequest requestToReturn = null;
    //Service
    try{
        requestToReturn = requestService.createRequestService(mcgillID, pickupLatitude, pickupLongitude, destinationLatitude, destinationLongitude);
    }
    catch(Exception e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(requestToReturn, HttpStatus.OK);
}

@PostMapping(value="/updateRequest/{mcgillID}")
public ResponseEntity<String> updateRequest(@PathVariable(name="mcgillID") int mcgillID, @RequestParam(name="pickup_lat")  double pickupLatitude, @RequestParam(name="pickup_long") double pickupLongitude,
                                            @RequestParam(name="dest_lat") double destinationLatitude, @RequestParam(name="dest_long") double destinationLongitude) {

    try{
        inputValidator.validateMcgillID(mcgillID);
    }
    catch(IllegalArgumentException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    try{
        requestService.updateRequestService(mcgillID, pickupLatitude, pickupLongitude, destinationLatitude, destinationLongitude);
    }
    catch(Exception e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
}

@PostMapping(value="/cancelRequest/{mcgillID}")
public ResponseEntity<String> cancelRequest(@PathVariable(name="mcgillID") int mcgillID) {
	
    try{
        inputValidator.validateMcgillID(mcgillID);
    }
    catch(IllegalArgumentException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    try{
        requestService.cancelRequestService(mcgillID);
    }
    catch(Exception e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(JSON_SUCCESS_MESSAGE, HttpStatus.OK);
}
}