package com.example.propertyapp;

import java.util.Date;

public class Property {

    private String description;
    private String address;
    private String postcode;
    private String tenantName;
    private String contactNo;
    private String contactEmail;
    private String depositAmount;
    private String rentPerMonth;
    private String startDate;
    private String endDate;
    private String extraInfo;
    private String userID;
    private String documentID;
    private String imagePath;

    // Constructor with all necessary fields
    public Property(String description, String address, String postcode, String tenantName, String contactNo,
                    String contactEmail, String depositAmount, String rentPerMonth, String startDate,
                    String endDate, String extraInfo, String userID, String documentID, String imagePath) {

        this.description = description;
        this.address = address;
        this.postcode = postcode;
        this.tenantName = tenantName;
        this.contactNo = contactNo;
        this.contactEmail = contactEmail;
        this.depositAmount = depositAmount;
        this.rentPerMonth = rentPerMonth;
        this.startDate = startDate;
        this.endDate = endDate;
        this.extraInfo = extraInfo;
        this.userID = userID;
        this.documentID = documentID;
        this.imagePath = imagePath;
    }

    // Getters
    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public String getRentPerMonth() {
        return rentPerMonth;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public String getUserID() {
        return userID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getImagePath() {
        return imagePath;
    }

    // Setters
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

}
