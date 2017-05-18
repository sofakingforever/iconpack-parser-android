package com.sofaking.iconpack;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadavfima on 14/05/2017.
 */

public class IconMasking {

    static final String BACKGROUND = "iconback";
    static final String BACKGROUND_IMG = "img";
    static final String MASK = "iconmask";
    static final String FRONT = "iconupon";
    static final String SCALE = "scale";
    static final String ITEM = "item";
    static final String FACTOR = "factor";
    static final String IMG_1_VALUE = "img1";


    private List<Bitmap> mBackImages = new ArrayList<Bitmap>();
    Bitmap mMaskImage = null;
    Bitmap mFrontImage = null;


    private float mFactor = 1.0f;


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
