package com.hackerspace.signalHeatMap;

public class SignalData {

	double latitude;
	double longitude;
	int serviceType;
	int operatorId;
	float strength;
	String timestamp;
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public SignalData(double latitude, double longitude, int serviceType, int operatorId, float strength, String timestamp) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.serviceType = serviceType;
		this.operatorId = operatorId;
		this.strength = strength;
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
	
		return "lat:"+latitude+" longitude:"+longitude;
	}

}
