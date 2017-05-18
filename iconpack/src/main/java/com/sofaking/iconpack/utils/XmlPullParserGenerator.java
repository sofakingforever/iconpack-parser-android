package com.sofaking.iconpack.utils;

import android.content.res.Resources;

import com.sofaking.iconpack.exceptions.XMLNotFoundException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;


public class XmlPullParserGenerator {

    private static final String DEF_XML = "xml";
    private static final String EXT_XML = "." + DEF_XML;
    private static final String UTF_8 = "UTF-8";


    public static XmlPullParser getXmlPullParser(Resources resources, String packageName, String file) throws XMLNotFoundException, XmlPullParserException {

        XmlPullParser xpp = null;

        int xmlId = resources.getIdentifier(file, DEF_XML, packageName);

        if (xmlId > 0) {
            xpp = resources.getXml(xmlId);
        } else {
            // no resource found, try to open it from assets folder
            try {
                InputStream appfilterstream = resources.getAssets().open(file + EXT_XML);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(appfilterstream, UTF_8);
            } catch (IOException e) {
                throw new XMLNotFoundException(file + EXT_XML, e);
            }
        }
        return xpp;
    }
}
