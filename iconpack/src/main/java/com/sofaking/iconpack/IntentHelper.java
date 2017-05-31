package com.sofaking.iconpack;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by nadavfima on 18/05/2017.
 */

public class IntentHelper {
    @NonNull
    static HashSet<ResolveInfo> getResolveInfos(PackageManager pm) {
        HashSet<ResolveInfo> rinfo = new HashSet<ResolveInfo>();
        rinfo.addAll(pm.queryIntentActivities(new Intent("org.adw.launcher.THEMES"), PackageManager.GET_META_DATA));
        rinfo.addAll(pm.queryIntentActivities(new Intent("com.gau.go.launcherex.theme"), PackageManager.GET_META_DATA));
        rinfo.addAll(pm.queryIntentActivities(new Intent("mobi.bbase.ahome.THEME"), PackageManager.GET_META_DATA));
        rinfo.addAll(pm.queryIntentActivities(new Intent("com.rogro.GDE.THEME.1"), PackageManager.GET_META_DATA));
        rinfo.addAll(pm.queryIntentActivities(new Intent("com.android.dxtop.launcher.THEME"), PackageManager.GET_META_DATA));
        rinfo.addAll(pm.queryIntentActivities(new Intent("com.fede.launcher.THEME_ICONPACK"), PackageManager.GET_META_DATA));
        return rinfo;
    }
}
