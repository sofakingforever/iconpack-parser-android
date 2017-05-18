package com.sofaking.iconpack;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.sofaking.iconpack.exceptions.AppFilterNotLoadedException;
import com.sofaking.iconpack.exceptions.XMLNotFoundException;
import com.sofaking.iconpack.utils.ResourceHelper;
import com.sofaking.iconpack.utils.RoundsExecutor;
import com.sofaking.iconpack.utils.XmlParserGenerator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by nadavfima on 14/05/2017.
 */

public class IconPack {


    private static final String FILE_APPFILTER = "appfilter";
    private static final String FILE_DRAWABLE = "drawable";
    private static final String COMPONENT = "component";
    private static final String DRAWABLE = "drawable";

    private final Handler mHandler;
    private final String mPackageName;
    private final WeakReference<Context> mContextReference;
    private final PackageManager mPackageManager;

    private String mTitle;
    private Resources mPackResources;
    private IconMasking mIconMasking;


    private HashMap<String, String> mAppFilterMap = new HashMap<String, String>();
    private LinkedHashMap<String, ArrayList<IconDrawable>> mDrawableMap = new LinkedHashMap<String, ArrayList<IconDrawable>>();

    private boolean mAppFilterLoaded;
    private boolean mAppFilterLoading;
    private boolean mDrawableMapLoaded;


    public IconPack(Context context, String packageName, Handler handler) throws PackageManager.NameNotFoundException {
        super();
        mContextReference = new WeakReference<Context>(context);
        mPackageName = packageName;
        mPackageManager = context.getPackageManager();
        mHandler = handler;
        mIconMasking = new IconMasking();

        initIconPack();

    }

    /**
     * @return Icon Pack's title as a @{@link String}
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns a @{@link Resources} object that contains the Icon Pack's resources (drawables and everything)
     * <p>
     * You basically don't really need that.
     *
     * @return
     */
    public Resources getResources() {
        return mPackResources;
    }


    /**
     * Start parsing the AppFilter.XML file asynchronously.
     *
     * @param listener
     * @param initMasking
     */
    @MainThread
    public void initAppFilterAsync(final AppFilterListener listener, final boolean initMasking) {


        RoundsExecutor.execute(new Runnable() {
            @Override
            public void run() {


                initAppFilter(initMasking, listener);


            }
        });


    }

    /**
     * Start parsing the AppFilter.XML file synchronously.
     *
     * @param initMasking
     * @param listener
     */
    public void initAppFilter(boolean initMasking, final AppFilterListener listener) {
        mAppFilterLoading = true;
        try {
            final XmlPullParser parser = XmlParserGenerator.getXmlPullParser(mPackResources, mPackageName, FILE_APPFILTER);

            onLoadAppFilter(parser, initMasking);

            mAppFilterLoaded = true;

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (listener != null)
                        listener.onAppFilterLoaded();
                }
            });
        } catch (final Exception e) {

            e.printStackTrace();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onLoadingFailed(e);

                }
            });
        }

        mAppFilterLoading = false;
    }

    /**
     * Start parsing the Drawable.XML file asynchronously.
     *
     * @param listener
     */
    @MainThread
    public void initDrawableMapAsync(final DrawableMapListener listener) {


        RoundsExecutor.execute(new Runnable() {
            @Override
            public void run() {

                initDrawableMap(listener);

            }

        });


    }

    private void initDrawableMap(final DrawableMapListener listener) {
        try {
            final XmlPullParser parser = getDrawableXmlPullParser();

            onLoadDrawableMap(parser);

            mDrawableMapLoaded = true;

            mHandler.post(new Runnable() {
                @Override
                public void run() {


                    listener.onDrawableMapLoaded();

                }
            });
        } catch (final Exception e) {

            e.printStackTrace();

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    listener.onLoadingFailed(e);

                }
            });
        }
    }

    public XmlPullParser getDrawableXmlPullParser() throws XMLNotFoundException, XmlPullParserException {
        return XmlParserGenerator.getXmlPullParser(mPackResources, mPackageName, FILE_DRAWABLE);
    }

    @WorkerThread
    public Drawable getDrawableIconForName(String drawableName) {
        return loadDrawable(drawableName);
    }

    /**
     * This method will first try to look up the icon in the AppFilterMap.
     * If no icon is found in the AppFilter, a masked icon will be generated for this app.
     *
     * @param info
     * @param maskFallback
     * @return
     */
    @WorkerThread
    public Drawable getDefaultIconForPackage(ResolveInfo info, boolean maskFallback) {

        if (!mAppFilterLoaded) {
            throw new AppFilterNotLoadedException();
        }

        String appPackageName = info.activityInfo.packageName;

        PackageManager pm = mContextReference.get().getPackageManager();

        Drawable defaultIcon = info.loadIcon(pm);

        Intent launchIntent = pm.getLaunchIntentForPackage(appPackageName);

        String componentName = null;

        if (launchIntent != null) {
            componentName = pm.getLaunchIntentForPackage(appPackageName).getComponent().toString();
        }

        String drawableName = mAppFilterMap.get(componentName);

        if (drawableName != null) {

            // found in app filter

            Drawable drawable = loadDrawable(drawableName);

            if (drawable == null) {
                return maskFallback ? generateMaskedIcon(defaultIcon) : defaultIcon;
            } else {
                return drawable;
            }

        } else {

            // not found

            // try to get a resource with the component filename
            if (componentName != null) {
                int start = componentName.indexOf("{") + 1;
                int end = componentName.indexOf("}", start);
                if (end > start) {
                    drawableName = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");

                    if (ResourceHelper.getDrawableResourceId(mPackResources, drawableName, mPackageName) > 0)
                        return loadDrawable(drawableName);
                }
            }
        }

        return maskFallback ? generateMaskedIcon(defaultIcon) : defaultIcon;
    }


    @MainThread
    private void initIconPack() throws PackageManager.NameNotFoundException {


        ApplicationInfo ai = mPackageManager.getApplicationInfo(mPackageName, PackageManager.GET_META_DATA);

        mTitle = mPackageManager.getApplicationLabel(ai).toString();

        mPackResources = mPackageManager.getResourcesForApplication(mPackageName);


    }

    @WorkerThread
    private void onLoadDrawableMap(XmlPullParser parser) {

        try {

            int eventType = parser.getEventType();
            String currentTitle = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("category")) {

                        currentTitle = onAddCategoryToMap(parser.getAttributeValue(null, "title"));

                    } else if (parser.getName().equals("item")) {

                        String name = parser.getAttributeValue(null, DRAWABLE);

                        onAddIconToCategory(currentTitle, name);
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @WorkerThread
    private void onAddIconToCategory(String currentTitle, String name) {
        int id = ResourceHelper.getDrawableResourceId(mPackResources, name, mPackageName);

        if (id > 0) {

            IconDrawable icon = new IconDrawable(name, id);
            icon.setTitle(IconDrawable.replaceName(mContextReference.get(), true, icon.getDrawableName()));
            mDrawableMap.get(currentTitle).add(icon);

        }
    }

    @WorkerThread
    private String onAddCategoryToMap(String title) {

        if (title != null && title.length() > 0) {

            mDrawableMap.put(title, new ArrayList<IconDrawable>());
            return title;

        }
        return "";
    }

    @WorkerThread
    private void onLoadAppFilter(XmlPullParser parser, boolean initMasking) {
        try {
            if (parser != null) {
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        if (initMasking) {
                            onLoadMask(parser);
                        }

                        if (parser.getName().equals(IconMasking.ITEM)) {
                            onLoadAppFilter(parser);
                        }
                    }
                    eventType = parser.next();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    private void onLoadAppFilter(XmlPullParser parser) {
        String componentName = null;
        String drawableName = null;

        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (parser.getAttributeName(i).equals(COMPONENT)) {
                componentName = parser.getAttributeValue(i);
            } else if (parser.getAttributeName(i).equals(DRAWABLE)) {
                drawableName = parser.getAttributeValue(i);
            }
        }
        if (!mAppFilterMap.containsKey(componentName)) {
            mAppFilterMap.put(componentName, drawableName);
        }
    }

    @WorkerThread
    private void onLoadMask(XmlPullParser parser) {
        if (parser.getName().equals(IconMasking.BACKGROUND)) {
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                if (parser.getAttributeName(i).startsWith(IconMasking.BACKGROUND_IMG)) {
                    String drawableName = parser.getAttributeValue(i);
                    Bitmap iconback = loadDrawable(drawableName).getBitmap();
                    if (iconback != null) {
                        mIconMasking.addBackgroundBitmap(iconback);
                    }

                }
            }
        } else if (parser.getName().equals(IconMasking.MASK)) {
            if (parser.getAttributeCount() > 0 && parser.getAttributeName(0).equals(IconMasking.IMG_1_VALUE)) {
                String drawableName = parser.getAttributeValue(0);

                mIconMasking.setMaskBitmap(loadDrawable(drawableName).getBitmap());

            }
        } else if (parser.getName().equals(IconMasking.FRONT)) {
            if (parser.getAttributeCount() > 0 && parser.getAttributeName(0).equals(IconMasking.IMG_1_VALUE)) {
                String drawableName = parser.getAttributeValue(0);
                mIconMasking.setFrontBitmap(loadDrawable(drawableName).getBitmap());
            }
        } else if (parser.getName().equals(IconMasking.SCALE)) {
            // mFactor
            if (parser.getAttributeCount() > 0 && parser.getAttributeName(0).equals(IconMasking.FACTOR)) {
                mIconMasking.setFactor(Float.valueOf(parser.getAttributeValue(0)));

            }
        }
    }

    @WorkerThread
    private BitmapDrawable loadDrawable(String drawableName) {
        try {

            int id = ResourceHelper.getDrawableResourceId(mPackResources, drawableName, mPackageName);

            if (id > 0) {
                Drawable drawable = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    drawable = mPackResources.getDrawable(id, mContextReference.get().getTheme());
                } else {
                    drawable = mPackResources.getDrawable(id);
                }

                return (BitmapDrawable) drawable;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @WorkerThread
    private Drawable generateMaskedIcon(Drawable defaultIcon) {

        Bitmap defaultBitmap = ((BitmapDrawable) defaultIcon).getBitmap();

        // if no support images in the icon pack return the bitmap itself
        List<Bitmap> backgroundImages = mIconMasking.getBackgroundImages();

        if (backgroundImages.size() == 0) return defaultIcon;

        Random r = new Random();
        int backImageInd = r.nextInt(backgroundImages.size());
        Bitmap backImage = backgroundImages.get(backImageInd);
        int w = backImage.getWidth();
        int h = backImage.getHeight();

        // create a bitmap for the result
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        // draw the background first
        canvas.drawBitmap(backImage, 0, 0, null);

        // create a mutable mask bitmap with the same mask
        Bitmap scaledBitmap;
        if (defaultBitmap.getWidth() > w || defaultBitmap.getHeight() > h) {

            float factor = mIconMasking.getFactor();

            scaledBitmap = Bitmap.createScaledBitmap(defaultBitmap, (int) (w * factor), (int) (h * factor), false);
        } else {
            scaledBitmap = Bitmap.createBitmap(defaultBitmap);
        }

        if (mIconMasking.mMaskImage != null) {
            // draw the scaled bitmap with mask
            Bitmap mutableMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(mutableMask);
            maskCanvas.drawBitmap(mIconMasking.mMaskImage, 0, 0, new Paint());

            // paint the bitmap with mask into the result
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2, (h - scaledBitmap.getHeight()) / 2, null);
            canvas.drawBitmap(mutableMask, 0, 0, paint);
            paint.setXfermode(null);
        } else {
            // draw the scaled bitmap with the back image as mask
            Bitmap mutableMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(mutableMask);
            maskCanvas.drawBitmap(backImage, 0, 0, new Paint());

            // paint the bitmap with mask into the result
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2, (h - scaledBitmap.getHeight()) / 2, null);
            canvas.drawBitmap(mutableMask, 0, 0, paint);
            paint.setXfermode(null);

        }

        if (mIconMasking.mFrontImage != null) {
            // paint the front
            canvas.drawBitmap(mIconMasking.mFrontImage, 0, 0, null);
        }


        return new BitmapDrawable(mPackResources, result);
    }

    public String getPackageName() {
        return mPackageName;
    }

    public boolean isAppFilterLoaded() {
        return mAppFilterLoaded;
    }

    public boolean isLoadingAppFilter() {
        return mAppFilterLoading;
    }


    public interface AppFilterListener {

        void onAppFilterLoaded();

        void onLoadingFailed(Exception e);
    }

    public interface DrawableMapListener {

        void onDrawableMapLoaded();

        void onLoadingFailed(Exception e);
    }
}
