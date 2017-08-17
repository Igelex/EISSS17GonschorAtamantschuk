package com.example.android.harvesthand;

/**
 * Hier Wird ein Tutorial - Eintrag gespeichert
 */

public class Tutorial {
    private int mImageId;
    private String mDescription;
    private String mNorm;
    private boolean video;

    public Tutorial(int mImageId, String norm, String description) {
        this.mImageId = mImageId;
        this.mNorm = norm;
        this.mDescription = description;
        this.video = false;
    }

    public Tutorial(int mImageId, String norm, String description, boolean video) {
        this.mImageId = mImageId;
        this.mNorm = norm;
        this.mDescription = description;
        this.video = video;
    }

    public int getmImageId() {
        return mImageId;
    }

    public void setmImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public String getmNorm() {
        return mNorm;
    }

    public void setmNorm(String mNorm) {
        this.mNorm = mNorm;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public boolean isVideo() { return this.video; }
}
