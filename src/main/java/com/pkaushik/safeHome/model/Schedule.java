package com.pkaushik.safeHome.model;

import java.time.LocalDateTime;

public class Schedule {

	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Walker walker;
	

	public Schedule(LocalDateTime startDate, LocalDateTime endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public Walker getWalker() {
		return walker;
	}

	public void setWalker(Walker walker) {
		this.walker = walker;
	}
}
