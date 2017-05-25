package com.sofaking.iconpack;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadavfima on 14/05/2017.
 */

class IconMasking {


    static final float DEFAULT_FACTOR = 1.0f;

    List<Bitmap> mBackImages = new ArrayList<Bitmap>();
    Bitmap mMaskImage = null;
    Bitmap mFrontImage = null;

    private float mFactor = DEFAULT_FACTOR;


    public void addBackgroundBitmap(Bitmap iconback) {
        mBackImages.add(iconback);
    }

    public void setMaskBitmap(Bitmap bitmap) {
        mMaskImage = bitmap;
    }

    public void setFrontBitmap(Bitmap bitmap) {
        mFrontImage = bitmap;
    }

    public void setFactor(Float factor) {
        mFactor = factor;
    }

    public List<Bitmap> getBackgroundImages() {
        return mBackImages;
    }

    public float getFactor() {
        return mFactor;
    }
}
