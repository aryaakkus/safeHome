package com.pkaushik.safeHome.model;

import java.util.HashMap;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pkaushik.safeHome.SafeHomeApplication;
import com.pkaushik.safeHome.model.enumerations.WalkerStatus;

@Entity
@Table(name="walker")
public class Walker extends UserRole {

	//Attributes

	@Id
	@Column(name="walker_id")
	private int walkerid;

	@Column(name="walker_rating")
	private double rating;

	@Column(name = "walker_walksafe")
	private boolean isWalksafe;

	@Transient
	private Schedule schedule;

	@Enumerated(EnumType.STRING)
	@Column(name = "walker_status")
	private WalkerStatus status;

	@OneToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="walker_assignment_fk")
	@JsonManagedReference
	private Assignment currentAssignment;

	Walker(){super();}

	public Walker(int walkerid, boolean isWalksafe) {
		rating = 0;
		this.isWalksafe = isWalksafe;
		status = WalkerStatus.INACTIVE;
		this.walkerid=walkerid;
	}

	public static UserRole getRole(int mcgillID){
		return SafeHomeApplication.getLoggedInUsersMap().get(mcgillID);
	}

	
	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}
	
	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	/**
	 * @return the isWalksafe
	 */
	public boolean isWalksafe() {
		return isWalksafe;
	}
	
	/**
	 * @param isWalksafe the isWalksafe to set
	 */
	public void setWalksafe(boolean isWalksafe) {
		this.isWalksafe = isWalksafe;
	}
	
	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
			return schedule;
	}
	
	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the status
	 */
	public WalkerStatus getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(WalkerStatus status) {
		this.status = status;
	}
	
	public Assignment getCurrentAssignment() {
		return this.currentAssignment;
	}
	
	public void setCurrentAssignment(Assignment currentAssignment) {
		this.currentAssignment = currentAssignment;
	}
	
	
}

