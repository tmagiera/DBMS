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
import java.util.ArrayList;
import java.util.List;

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
public class GetEbooksXmlParser extends XmlParser {

    private static final String ns = null;

    public List<GetEbooksResponseEntity> parse(String xml) throws XmlPullParserException, IOException {
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

        parser.setInput(new ByteArrayInputStream(Charset.forName("UTF-16").encode(decoded).array()), null);
        parser.nextTag();

        List<GetEbooksResponseEntity> contentListResponseEntities = new ArrayList<GetEbooksResponseEntity>();
        parser.require(XmlPullParser.START_TAG, ns, "data");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            parser.require(XmlPullParser.START_TAG, ns, "ebooks");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                GetEbooksResponseEntity getEbooksResponseEntity = readDataContent(parser);
                if (getEbooksResponseEntity != null) {
                    Log.d(this.getClass().getSimpleName(), "Retreiving new content details with title " + getEbooksResponseEntity.title);
                    contentListResponseEntities.add(getEbooksResponseEntity);
                }
            }
        }

        return contentListResponseEntities;
    }

    private GetEbooksResponseEntity readDataContent(XmlPullParser parser) throws XmlPullParserException, IOException {
        GetEbooksResponseEntity getEbooksResponseEntity = new GetEbooksResponseEntity();

        parser.require(XmlPullParser.START_TAG, ns, "ebook");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the entry tag
            if (!name.isEmpty()) {
                switch (name) {
                    case "title":
                        getEbooksResponseEntity.title = readText(parser);
                        break;
                    case "code":
                        getEbooksResponseEntity.code = readText(parser);
                        break;
                    case "thumbnail":
                        getEbooksResponseEntity.thumbnail = readText(parser);
                        break;
                    default:
                        skip(parser);
                }
            } else {
                skip(parser);
            }
        }

        return getEbooksResponseEntity;
    }

    public class GetEbooksResponseEntity {
        public String title;
        public String code;
        public String thumbnail;

        GetEbooksResponseEntity() {
        }

    }
}