package com.sofaking.iconpack.utils;

import android.content.res.Resources;

/**
 * Created by nadavfima on 14/05/2017.
 */

public class ResourceHelper {

    public static int getDrawableResourceId(Resources packResources, String resName, String iconPack) {
        try {
            return packResources.getIdentifier(resName, "drawable", iconPack);
        } catch (Exception e) {


        }
        return -1;
    }

}
