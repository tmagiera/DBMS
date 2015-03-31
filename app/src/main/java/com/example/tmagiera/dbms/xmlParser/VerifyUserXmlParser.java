package com.example.tmagiera.dbms.xmlParser;

import android.util.Log;
import android.util.Xml;

import com.example.tmagiera.dbms.XmlParser;

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
public class VerifyUserXmlParser extends XmlParser {

    private static final String ns = null;

    public VerifyUserResponseEntity parse(String xml) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new ByteArrayInputStream(Charset.forName("UTF-16").encode(xml).array()), null);
        parser.nextTag();
        String result = readData(parser);
        if (result == null) {
            Log.d(this.getClass().getSimpleName(), "Cannot find data field in response due to returned error title");
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
        VerifyUserResponseEntity verifyUserResponseEntity = readDataContent(parser);

        return verifyUserResponseEntity;
    }

    private VerifyUserResponseEntity readDataContent(XmlPullParser parser) throws XmlPullParserException, IOException {
        VerifyUserResponseEntity verifyUserResponseEntity = new VerifyUserResponseEntity();

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
                        verifyUserResponseEntity.session = readText(parser);
                        break;
                    case "id":
                        verifyUserResponseEntity.id = Integer.parseInt(readText(parser));
                        break;
                    case "name":
                        verifyUserResponseEntity.name = readText(parser);
                        break;
                    default:
                        skip(parser);
                }
            } else {
                skip(parser);
            }
        }

        return verifyUserResponseEntity;
    }

    public class VerifyUserResponseEntity {
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

        VerifyUserResponseEntity() {
        }

        public boolean isLoggedIn() {
            if (!session.isEmpty()) {
                return true;
            }

            return false;
        }
    }
}