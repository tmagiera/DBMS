package com.example.tmagiera.dbms;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;

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
public class ApiXmlParser {

    private static final String ns = null;

    public LoginResponseEntity parse(String xml) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new ByteArrayInputStream(Charset.forName("UTF-16").encode(xml).array()), null);
        parser.nextTag();
        String result = readData(parser);
        if (result == null) {
            Log.d(this.getClass().getSimpleName(), "Cannot find data field in response due to returned error code");
            return null;
        }

        String decoded = "";
        try {
            decoded = "<data>" + URLDecoder.decode(result, "UTF-8") + "</data>";
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), Log.getStackTraceString(e));
            return null;
        }
        //Log.d(this.getClass().getSimpleName(), "decoded: " + decoded);

        parser.setInput(new ByteArrayInputStream(Charset.forName("UTF-16").encode(decoded).array()), null);
        parser.nextTag();
        LoginResponseEntity loginResponseEntity = readDataContent(parser);

        return loginResponseEntity;
    }

    private String readData(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    private LoginResponseEntity readDataContent(XmlPullParser parser) throws XmlPullParserException, IOException {
        LoginResponseEntity loginResponseEntity = new LoginResponseEntity();

        parser.require(XmlPullParser.START_TAG, ns, "data");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the entry tag
            if (!name.isEmpty()) {
                switch (name) {
                    case "session":
                        loginResponseEntity.session = readText(parser);
                        break;
                    case "id":
                        loginResponseEntity.id = Integer.parseInt(readText(parser));
                        break;
                    case "name":
                        loginResponseEntity.name = readText(parser);
                        break;
                    default:
                        skip(parser);
                }
            } else {
                skip(parser);
            }
        }

        return loginResponseEntity;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    public class LoginResponseEntity {
        public String session;
        public String clientCode;
        public int id;
        public String name;
        public boolean testUserRole;
        public int noSynchronizeMaxTimeSpan;
        public int offlineTimeLimit;
        public String userFileServer;
        public Date lastLoginAt;
        public int usedQuota;
        public int availableQuota;
        public int totalQuota;

        LoginResponseEntity() {
        }

        public boolean isLoggedIn() {
            if (!session.isEmpty()) {
                return true;
            }

            return false;
        }
    }
}