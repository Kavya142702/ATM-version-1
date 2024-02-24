package com.users;

import java.util.Date;

public class Users {
	private String name;
    private Long  mobileNumber;
    private String Email;
    private String Address;
    private Date dob;
    private String gender;
    
	public Users(String name, Long mobileNumber, String email, String address, Date dob, String gender) {
		super();
		this.name = name;
		this.mobileNumber = mobileNumber;
		Email = email;
		Address = address;
		this.dob = dob;
		this.gender = gender;
	}

	public Users() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(Long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
