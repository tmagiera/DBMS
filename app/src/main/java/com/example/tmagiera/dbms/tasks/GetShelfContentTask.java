package com.example.tmagiera.dbms.tasks;

import android.os.AsyncTask;

import com.example.tmagiera.dbms.ApiHandler;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class GetShelfContentTask extends AsyncTask<Void, Void, Boolean> {

    private static String mSessionId;
    private static List<String> mResults;

    public GetShelfContentTask(String sessionId) {
        mSessionId = sessionId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            ApiHandler apiHandler = new ApiHandler();
            mResults = apiHandler.getContentList(apiHandler.getSessionId());
        } catch (Exception e) {
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            EventBus.getDefault().post(new ShelfContentMessageEvent(mResults));
        } else {
            EventBus.getDefault().post(new ShelfContentMessageEvent(mResults));
        }
    }

    public class ShelfContentMessageEvent {
        public final List<String> results;

        public ShelfContentMessageEvent(List<String> results) {
            this.results = results;
        }
    }
}