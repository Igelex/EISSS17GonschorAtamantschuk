package com.example.android.harvesthand;

/**
 * Created by Pastuh on 13.05.2017.
 */

public class Entry {
    private String entryId, entryName;
    private int entryArtId, entryPhValue, entryWater, entryMinerals;
    private int [] entryCollaborators;

    public Entry(String entryId, String entryName, int entryArtId, int entryPhValue,
                        int entryWater, int entryMinerals, int[] entryCollaborators) {
        this.entryId = entryId;
        this.entryName = entryName;
        this.entryArtId = entryArtId;
        this.entryPhValue = entryPhValue;
        this.entryWater = entryWater;
        this.entryMinerals = entryMinerals;
        this.entryCollaborators = entryCollaborators;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public int getEntryArtId() {
        return entryArtId;
    }

    public void setEntryArtId(int entryArtId) {
        this.entryArtId = entryArtId;
    }

    public int getEntryPhValue() {
        return entryPhValue;
    }

    public void setEntryPhValue(int entryPhValue) {
        this.entryPhValue = entryPhValue;
    }

    public int getEntryWater() {
        return entryWater;
    }

    public void setEntryWater(int entryWater) {
        this.entryWater = entryWater;
    }

    public int getEntryMinerals() {
        return entryMinerals;
    }

    public void setEntryMinerals(int entryMinerals) {
        this.entryMinerals = entryMinerals;
    }

    public int[] getEntryCollaborators() {
        return entryCollaborators;
    }

    public void setEntryCollaborators(int[] entryCollaborators) {
        this.entryCollaborators = entryCollaborators;
    }

    public CharSequence convertToString(CharSequence c){
        return c.toString();
    }
}