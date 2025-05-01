package com.hackathon.leaderboardservice.model;

public class ErrorModel {

	private String errorCode;
	private String errorMessage;

	public ErrorModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ErrorModel(String errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
