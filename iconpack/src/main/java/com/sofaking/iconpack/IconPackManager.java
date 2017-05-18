package com.sofaking.iconpack;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.support.annotation.MainThread;

import com.sofaking.iconpack.exceptions.IconPacksNotFoundException;
import com.sofaking.iconpack.exceptions.IconPacksNotLoadedException;
import com.sofaking.iconpack.utils.RoundsExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The @{@link IconPackManager} holds the objects for the installed icon packs.
 * You should only have one of these in your app.
 */
public class IconPackManager {

    private HashMap<String, IconPack> mInstalledIconPacks;
    private boolean mInstalledPacksLoaded;

    /**
     * You should only have one of these in your app.
     */
    public IconPackManager() {

        // init the hasmap
        mInstalledIconPacks = new HashMap<>();

    }


    /**
     * @param packageName - the packageName of the installed @{@link IconPack}s
     * @return the @{@link IconPack} object matching the packageName
     */
    public IconPack getInstalledIconPack(String packageName) {
        if (!mInstalledPacksLoaded) {
            throw new IconPacksNotLoadedException();
        }

        if (!mInstalledIconPacks.containsKey(packageName)) {
            throw new IconPacksNotFoundException(packageName);
        }

        return mInstalledIconPacks.get(packageName);
    }

    /**
     * @return list of all currently installed @{@link IconPack}s
     */
    public ArrayList<IconPack> getInstalledIconPacksList() {
        if (!mInstalledPacksLoaded) {

            throw new IconPacksNotLoadedException();

        }
        return new ArrayList<>(mInstalledIconPacks.values());
    }

    /**
     * @return map of all currently installed @{@link IconPack}s
     */
    public HashMap<String, IconPack> getInstalledIconPacksMap() {
        if (!mInstalledPacksLoaded) {

            throw new IconPacksNotLoadedException();
        }
        return mInstalledIconPacks;
    }


    /**
     * Load the currently installed @{@link IconPack}s asynchronously.
     * <p>
     * This does not load the XML files.
     * you must do this manually for each @{@link IconPack} before trying to get an icon.
     *
     * @param context
     * @param listener - Attach a @{@link Listener} so you know when the installed packs were loaded
     */
    @MainThread
    public void loadInstalledIconPacksAsync(final Context context, final Listener listener) {

        final Handler handler = new Handler();

        RoundsExecutor.execute(new Runnable() {
            @Override
            public void run() {

                queryInstalledIconPacks(context, handler);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onIconPacksLoaded();
                    }
                });
            }
        });

    }


    /**
     * This privately queries the @{@link PackageManager} for apps that are icon packs.
     *
     * @param context
     * @param handler
     */
    private void queryInstalledIconPacks(Context context, Handler handler) {
        PackageManager pm = context.getPackageManager();


        // merge those lists
        List<ResolveInfo> rinfo = IntentHelper.getResolveInfos(pm);


        for (ResolveInfo ri : rinfo) {

            try {
                IconPack pack = new IconPack(context, ri.activityInfo.packageName, handler);

                mInstalledIconPacks.put(pack.getPackageName(), pack);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


        }

        mInstalledPacksLoaded = true;
    }

    public interface Listener {

        void onIconPacksLoaded();
    }
}
