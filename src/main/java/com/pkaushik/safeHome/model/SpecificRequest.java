package com.pkaushik.safeHome.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pkaushik.safeHome.model.enumerations.RequestStatus;

@Entity
@Table(name = "request")
public class SpecificRequest {

    @Id
    @GeneratedValue
    @Column(name ="request_id")
    private Long request_id;

    @OneToOne(mappedBy = "request", fetch=FetchType.EAGER)
    @JsonBackReference
    Student student;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="request_pickup_id",referencedColumnName = "location_id")
    Location pickupLocation;

    @Transient
    LocalDateTime pickupTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="request_destination_id",referencedColumnName = "location_id")
    Location destination;

    @Enumerated(EnumType.STRING)
    @Column(name="request_status")
    RequestStatus requestStatus;

    @Transient
    Assignment assignment; 

    SpecificRequest(){}
    
    public SpecificRequest(Student student, Location pickupLocation, Location destination){
        this.requestStatus = RequestStatus.CREATED; 
        this.student = student; 
        this.pickupLocation = pickupLocation; 
        this.destination = destination;
    }
    
    public SpecificRequest(Student student, double pickupLatitude, double pickupLongitude,   
    double destinationLatitude, double destinationLongitude){
        this.requestStatus = RequestStatus.CREATED; 
        this.student = student; 
        this.pickupLocation = new Location(pickupLatitude, pickupLongitude); 
        this.destination = new Location(destinationLatitude, destinationLongitude);
    }
    
    
    public Student getStudent(){
        return student; 
    }
    
    public void setStudent(Student student){
        this.student = student; 
    }
    
    public Location getPickupLocation(){
        return pickupLocation;
    }
    
    public void setPickupLocation(Location pickupLocation){
        this.pickupLocation = pickupLocation;
    }
    
    public Location getDestinationLocation(){
        return destination;
    }
    
    public void setDestinationLocation(Location destination){
        this.destination = destination;
    }

    public void setRequestStatus(RequestStatus status){
        this.requestStatus = status; 
    }
    
    public RequestStatus getRequestStatus(){
        return requestStatus; 
    }
    
    
    public Assignment getAssignment() {
        return this.assignment;
    }
    
    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(Long request_id) {
        this.request_id = request_id;
    }
}
