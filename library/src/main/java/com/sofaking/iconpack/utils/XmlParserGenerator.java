package com.sofaking.iconpack.utils;

import android.content.res.Resources;

import com.sofaking.iconpack.exceptions.XMLNotFoundException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nadavfima on 14/05/2017.
 */

public class XmlParserGenerator {

    private static final String DEF_XML = "xml";
    private static final String EXT_XML = "." + DEF_XML;
    private static final String UTF_8 = "UTF-8";


    public static XmlPullParser getXmlPullParser(Resources res, String packageName, String file) throws XMLNotFoundException, XmlPullParserException {

        XmlPullParser xpp = null;

        // try to get identifier of the XML file
        int xmlId = res.getIdentifier(file, DEF_XML, packageName);


        if (xmlId > 0) {
            // if found - getXml()
            xpp = res.getXml(xmlId);
        } else {
            // no resource found, try to open it from assests folder
            try {
                InputStream appfilterstream = res.getAssets().open(file + EXT_XML);

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
