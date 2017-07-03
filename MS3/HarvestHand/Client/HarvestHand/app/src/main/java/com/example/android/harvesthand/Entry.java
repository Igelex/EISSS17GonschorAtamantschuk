package com.example.android.harvesthand;

import java.util.ArrayList;

/**
 * Entry-Pbject zum Speichern eines Entrys
 */

public class Entry {
    private String entryId, tutorialId, entryName, ownerID, location;
    private int area, soil, airTemp, soilTemp, cropId, soilMoisture, phValue, height;
    private ArrayList collaborators;

    public Entry(String entryId, String entryName, int cropId, String location, int area, String tutorialId) {
        this.entryId = entryId;
        this.entryName = entryName;
        this.cropId = cropId;
        this.location = location;
        this.area = area;
        this.tutorialId = tutorialId;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(String tutorialId) {
        this.tutorialId = tutorialId;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public int getCropId() {
        return cropId;
    }

    public void setCropId(int artId) {
        this.cropId = artId;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getSoil() {
        return soil;
    }

    public void setSoil(int soil) {
        this.soil = soil;
    }

    public int getAirTemp() {
        return airTemp;
    }

    public void setAirTemp(int airTemp) {
        this.airTemp = airTemp;
    }

    public int getSoilTemp() {
        return soilTemp;
    }

    public void setSoilTemp(int soilTemp) {
        this.soilTemp = soilTemp;
    }

    public int getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(int soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public int getPhValue() {
        return phValue;
    }

    public void setPhValue(int phValue) {
        this.phValue = phValue;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(ArrayList collaborators) {
        this.collaborators = collaborators;
    }
}