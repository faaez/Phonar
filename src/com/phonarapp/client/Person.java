package com.phonarapp.client;

public class Person {
	private double latitude;
	private double longitude;
	private double altitude;
	private String name;
	private String phoneNumber;

	public Person(String phoneNumber, String name, double latitude,
			double longitude, double altitude) {
		this.phoneNumber = phoneNumber;
		this.name = name;
		this.altitude = altitude;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
