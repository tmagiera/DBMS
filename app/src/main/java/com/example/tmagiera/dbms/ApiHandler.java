package com.example.tmagiera.dbms;

import android.util.Log;

import com.example.tmagiera.dbms.xmlParsers.GetEbooksForUserXmlParser;
import com.example.tmagiera.dbms.xmlParsers.VerifyUserXmlParser;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
        VerifyUserXmlParser.LoginResponseEntity data;
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

    public List<String> getContentList(String sessionId) {
        String result = networkLibrary.download(apiBaseUrl + "getEbooksForUser?sessionId=" + sessionId + "&apiVersion=2");

        GetEbooksForUserXmlParser getEbooksForUserXmlParser = new GetEbooksForUserXmlParser();
        List<GetEbooksForUserXmlParser.ContentListResponseEntity> data;
        List<String> response = new ArrayList<String>();
        try {
            data = getEbooksForUserXmlParser.parse(result);
            if (data == null) {
                Log.d(this.getClass().getSimpleName(), "Cannot get content data");
                return null;
            }
            for (GetEbooksForUserXmlParser.ContentListResponseEntity content : data) {
                response.add(content.code);
            }
            Log.d(this.getClass().getSimpleName(), "Content data items: " + data.size());

            return response;
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), Log.getStackTraceString(e));
        }

        return null;
    }

    public static String getSessionId() {
        return sessionId;
    }
;}