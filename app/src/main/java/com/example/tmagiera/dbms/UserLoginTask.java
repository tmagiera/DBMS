package com.example.tmagiera.dbms;

import android.os.AsyncTask;

import de.greenrobot.event.EventBus;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };


    private final String mEmail;
    private final String mPassword;

    UserLoginTask(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result;

        try {
            ApiManager apiManager = new ApiManager();
            result = apiManager.login(mEmail, mPassword);
        } catch (Exception e) {
            result = false;
        }


//
//        for (String credential : DUMMY_CREDENTIALS) {
//            String[] pieces = credential.split(":");
//            if (pieces[0].equals(mEmail)) {
//                // Account exists, return true if the password matches.
//                return pieces[1].equals(mPassword);
//            }
//        }

//        // TODO: register the new account here.
        return result;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //mAuthTask = null;
        //showProgress(false);

        if (success) {
            EventBus.getDefault().post(new LoginMessageEvent(true));
        } else {
            EventBus.getDefault().post(new LoginMessageEvent(false));
            //mPasswordView.setError(getString(R.string.error_incorrect_password));
            //mPasswordView.requestFocus();
        }
    }

    public class LoginMessageEvent {
        public final boolean result;

        public LoginMessageEvent(boolean result) {
            this.result = result;
        }
    }
}