package com.example.tmagiera.dbms.tasks;

import android.os.AsyncTask;

import com.example.tmagiera.dbms.ApiHandler;

import de.greenrobot.event.EventBus;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private static String mEmail;
    private static String mPassword;

    public LoginTask(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result;

        try {
            ApiHandler apiHandler = new ApiHandler();
            result = apiHandler.login(mEmail, mPassword);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            EventBus.getDefault().post(new LoginMessageEvent(true));
        } else {
            EventBus.getDefault().post(new LoginMessageEvent(false));
        }
    }

    public class LoginMessageEvent {
        public final boolean result;

        public LoginMessageEvent(boolean result) {
            this.result = result;
        }
    }
}