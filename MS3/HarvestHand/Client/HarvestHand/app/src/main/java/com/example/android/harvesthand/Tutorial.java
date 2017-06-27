package com.example.android.harvesthand;

/**
 * Created by Pastuh on 26.06.2017.
 */

public class Tutorial {
    private int mImageId;
    private String mDescription;
    private String mOptional;
    private String mNorm;

    public Tutorial(int mImageId, String norm, String description) {
        this.mImageId = mImageId;
        this.mNorm = norm;
        this.mDescription = description;
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


}
