package com.example.tmagiera.dbms;

import android.util.Log;

import com.example.tmagiera.dbms.xmlParser.GetEbooksForUserXmlParser;
import com.example.tmagiera.dbms.xmlParser.GetEbooksXmlParser;
import com.example.tmagiera.dbms.xmlParser.VerifyUserXmlParser;
import com.google.common.base.Joiner;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmagiera on 2015-03-26.
 */
public class ApiHandler {

    private String apiBaseUrl = "http://demo-bookshelf-assodb.ydp.eu/ctrl.php/api/";
    private static String sessionId;

    private NetworkLibrary networkLibrary;

    public ApiHandler() {
        this.networkLibrary = new NetworkLibrary();
    }

    public boolean login(String user, String password) {
        String userUrlEncoded = "";
        try {
            userUrlEncoded = URLEncoder.encode(user, "UTF-8");
        } catch (Exception e) {
        }
        String passwordUrlEncoded = "";
        try {
            passwordUrlEncoded = URLEncoder.encode(password, "UTF-8");
        } catch (Exception e) {
        }

        String result = networkLibrary.download(apiBaseUrl + "verifyUser?deviceid=8e3f1bbb73f0f6c952fcf873332eae9f&devicetype=mobile&fields=%7B%7D&login=" + userUrlEncoded + "&password=" + passwordUrlEncoded + "&token=bshf");

        VerifyUserXmlParser verifyUserXmlParser = new VerifyUserXmlParser();
        VerifyUserXmlParser.VerifyUserResponseEntity data;
        try {
            data = verifyUserXmlParser.parse(result);
            if (data == null) {
                Log.d(this.getClass().getSimpleName(), "Cannot log in");
                return false;
            }
            if (!data.isLoggedIn()) {
                Log.d(this.getClass().getSimpleName(), "User is not logged in");
                return false;
            }

            sessionId = data.session;
            Log.d(this.getClass().getSimpleName(), "Logged in with sessionId: " + sessionId);
            return true;
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), Log.getStackTraceString(e));
        }

        return false;
    }

    public List<ContentEntity> getContentList(String sessionId) {
        String result = networkLibrary.download(apiBaseUrl + "getEbooksForUser?sessionId=" + sessionId + "&apiVersion=2");

        GetEbooksForUserXmlParser getEbooksForUserXmlParser = new GetEbooksForUserXmlParser();
        List<GetEbooksForUserXmlParser.GetEbooksForUserResponseEntity> data;
        HashMap<String, List<String>> contentCodes = new HashMap<>();
        try {
            data = getEbooksForUserXmlParser.parse(result);
            if (data == null) {
                Log.d(this.getClass().getSimpleName(), "Cannot get content data");
                return null;
            }
            for (GetEbooksForUserXmlParser.GetEbooksForUserResponseEntity content : data) {
                List<String> codes = contentCodes.get(content.cms);
                if (codes == null) {
                    codes = new ArrayList<>();
                }
                codes.add(content.code);
                contentCodes.put(content.cms, codes);
            }
            Log.d(this.getClass().getSimpleName(), "Content data items: " + data.size());

            List<ContentEntity> contentEntities = new ArrayList<>();

            for (Map.Entry<String, List<String>> entry : contentCodes.entrySet()) {
                String cmsServer = entry.getKey();
                List<String> codes = entry.getValue();

                Log.d(this.getClass().getSimpleName(), "Connection to CMS:" + cmsServer);

                Joiner joiner = Joiner.on(",").skipNulls();
                String resultDetails = networkLibrary.download(cmsServer + "/" + "getEbooks?ebookKey=" + joiner.join(codes));

                GetEbooksXmlParser getEbooksXmlParser = new GetEbooksXmlParser();
                List<GetEbooksXmlParser.GetEbooksResponseEntity> detailsData;
                try {
                    detailsData = getEbooksXmlParser.parse(resultDetails);
                    if (detailsData == null) {
                        Log.d(this.getClass().getSimpleName(), "Cannot get content data details");
                        return null;
                    }
                    for (GetEbooksXmlParser.GetEbooksResponseEntity contentDetails : detailsData) {
                        ContentEntity contentEntity = new ContentEntity();
                        contentEntity.setTitle(contentDetails.title);
                        contentEntity.setCode(contentDetails.code);
                        contentEntity.setThumbnail(contentDetails.url + "/" + contentDetails.thumbnail);
                        contentEntity.setUrl(contentDetails.url);
                        contentEntities.add(contentEntity);
                    }
                    Log.d(this.getClass().getSimpleName(), "Content data details items: " + detailsData.size());

                } catch (Exception e) {
                    Log.d(this.getClass().getSimpleName(), Log.getStackTraceString(e));
                }
            }

            return contentEntities;

        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), Log.getStackTraceString(e));
        }

        return null;
    }

    public static String getSessionId() {
        return sessionId;
    }
;}