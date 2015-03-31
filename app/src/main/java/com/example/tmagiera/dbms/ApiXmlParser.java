package com.example.tmagiera.dbms;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by tmagiera on 2015-02-19.
 *
 * This XML file does not appear to have any style information associated with it. The document tree is shown below.
 <.?xml version="1.0" encoding="UTF-8" ?>
 <response>
 <error msg="0"/>
 <data>%3Csession%3E61329a6af34462eb4525e79ccf04d731.9a24a4dbacebb23d05f205a4e74cb1a5%3C...</data>
 </response>
 */
abstract public class ApiXmlParser {

    protected static final String ns = null;

    protected String readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the entry tag
            if (name.equals("data")) {
                return readText(parser);
            } else {
                skip(parser);
            }
        }

        return null;
    }

    // For the tags title and summary, extracts their text values.
    protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}