package com.example.android.harvesthand;

/**
 * Created by Pastuh on 13.05.2017.
 */

public class Entry {
    private String entry_id, entry_name;
    private int entry_art_id, entry_ph_value, entry_water, entry_minerals;
    private int [] entry_collaborators;

    public Entry(String entry_id, String entry_name, int entry_art_id, int entry_ph_value, int entry_water, int entry_minerals, int[] entry_collaborators) {
        this.entry_id = entry_id;
        this.entry_name = entry_name;
        this.entry_art_id = entry_art_id;
        this.entry_ph_value = entry_ph_value;
        this.entry_water = entry_water;
        this.entry_minerals = entry_minerals;
        this.entry_collaborators = entry_collaborators;
    }

    public String getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(String entry_id) {
        this.entry_id = entry_id;
    }

    public String getEntry_name() {
        return entry_name;
    }

    public void setEntry_name(String entry_name) {
        this.entry_name = entry_name;
    }

    public int getEntry_art_id() {
        return entry_art_id;
    }

    public void setEntry_art_id(int entry_art_id) {
        this.entry_art_id = entry_art_id;
    }

    public int getEntry_ph_value() {
        return entry_ph_value;
    }

    public void setEntry_ph_value(int entry_ph_value) {
        this.entry_ph_value = entry_ph_value;
    }

    public int getEntry_water() {
        return entry_water;
    }

    public void setEntry_water(int entry_water) {
        this.entry_water = entry_water;
    }

    public int getEntry_minerals() {
        return entry_minerals;
    }

    public void setEntry_minerals(int entry_minerals) {
        this.entry_minerals = entry_minerals;
    }

    public int[] getEntry_collaborators() {
        return entry_collaborators;
    }

    public void setEntry_collaborators(int[] entry_collaborators) {
        this.entry_collaborators = entry_collaborators;
    }
}