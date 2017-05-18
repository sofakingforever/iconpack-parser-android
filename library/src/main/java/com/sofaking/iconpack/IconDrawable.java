package com.sofaking.iconpack;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by nadavfima on 14/05/2017.
 */

public class IconDrawable {

    private final int resId;
    private final String drawableName;
    private String title;

    public IconDrawable(String drawableName, int resId) {
        this.drawableName = drawableName;
        this.resId = resId;


    }


    public static String replaceName(@NonNull Context context, boolean iconReplacer, String name) {
        if (iconReplacer) {
            String[] replacer = context.getResources().getStringArray(R.array.icon_name_replacer);
            for (String replace : replacer) {
                String[] strings = replace.split(",");
                if (strings.length > 0)
                    name = name.replace(strings[0], strings.length > 1 ? strings[1] : "");
            }
        }
        name = name.replaceAll("_", " ");
        name = name.trim().replaceAll("\\s+", " ");
        char character = Character.toUpperCase(name.charAt(0));
        return character + name.substring(1);
    }

    public String getDrawableName() {
        return drawableName;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
