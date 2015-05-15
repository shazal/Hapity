package com.skhalid.hapity.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.support.v4.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.Jsonexample;
import com.skhalid.hapity.R;
import com.skhalid.hapity.VolleySingleton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import static android.view.View.VISIBLE;

/**
 * A login screen that offers login via email/password.
 */
public class LoginFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private TwitterLoginButton loginButton;
    CallbackManager callbackManager;
    LoginButton fbLoginButton;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String type;
    private String UserID;
    HashMap<String, String> params;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=  inflater.inflate(R.layout.activity_login, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) getActivity().findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) getActivity().findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) getActivity().findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "manual";
                attemptLogin();
            }
        });

        Button signup = (Button) getActivity().findViewById(R.id.sign_up_fragment_button);
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                SignupFragment signup = new SignupFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.dash_container, signup);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//		transaction.addToBackStack("login");
//      getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.commit();
            }
        });

        mLoginFormView = getActivity().findViewById(R.id.login_form);
        mProgressView = getActivity().findViewById(R.id.login_progress);

        loginButton = (TwitterLoginButton) getActivity().findViewById(R.id.twitter_login_button);
        fbLoginButton = (LoginButton) getActivity().findViewById(R.id.fb_login_button);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                try {
                    showProgress(true);
                    type = "twitter";
                    String url = "http://testing.egenienext.com/project/hapity/webservice/signin/";
                     params = new HashMap<String,String>();
                    UserID = result.data.getUserId() + "";
                    params.put("twitter_id", UserID);
                    loadAPI(url, params);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });

        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setLoginBehavior(LoginBehavior.SSO_WITH_FALLBACK);
        fbLoginButton.setFragment(this);
        // If using in a fragment

        callbackManager = CallbackManager.Factory.create();
        // Other app specific specialization

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                try {
                    showProgress(true);
                    type = "facebook_id";
                    String url = "http://testing.egenienext.com/project/hapity/webservice/signin/";
                     params = new HashMap<String,String>();
                    Profile.fetchProfileForCurrentAccessToken();
                    UserID = Profile.getCurrentProfile().getId();
                    params.put(type, UserID);
                    loadAPI(url, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                exception.toString();
                Toast.makeText(getActivity(),exception.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
        if(requestCode == 195278 || requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
                // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            String url = "http://testing.egenienext.com/project/hapity/webservice/signin/";
            params = new HashMap<String,String>();
            params.put("password", mPasswordView.getText().toString().trim());
            params.put("email", mEmailView.getText().toString().trim());
            loadAPI(url, params);

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : VISIBLE);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                getActivity().finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void loadAPI(String url, HashMap<String, String> params) {



        GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                url,
                Jsonexample.class,
                null,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());


        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }

    private Response.Listener<Jsonexample> createMyReqSuccessListener() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {
                    showProgress(false);
                    TestFragment test_fragment = TestFragment.newInstance("Test Fragment");
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.dash_container, test_fragment);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack("posts");
//                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commitAllowingStateLoss();
                    DashboardActivity.bottom_fragment.getView().setVisibility(VISIBLE);
                    BottomFragment.isHomeActive = true;
                    BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_normal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int statuscode = error.networkResponse.statusCode;

                if(statuscode == 500){
                    if(type.equalsIgnoreCase("manual")) {
                        showProgress(false);
                        Toast.makeText(getActivity(), "User Not Found/Server Error", Toast.LENGTH_LONG);
                    } else {
                        String url = "http://testing.egenienext.com/project/hapity/webservice/signup/";

                        loadAPI_SignUP(url, params);
                    }
                }


            }
        };
    }

    private void loadAPI_SignUP(String url, HashMap<String, String> params) {



        GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                url,
                Jsonexample.class,
                null,
                params,
                createMyReqSuccessListener_SignUP(),
                createMyReqErrorListener_SignUP());


        showProgress(true);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }

    private Response.Listener<Jsonexample> createMyReqSuccessListener_SignUP() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {
                    showProgress(false);
                    TestFragment test_fragment = TestFragment.newInstance("Test Fragment");
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.dash_container, test_fragment);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack("posts");
//                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commitAllowingStateLoss();
                    DashboardActivity.bottom_fragment.getView().setVisibility(VISIBLE);
                    BottomFragment.isHomeActive = true;
                    BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_normal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListener_SignUP() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                int statuscode = error.networkResponse.statusCode;

                if(statuscode == 404){
                    Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_LONG);
                }


            }
        };
    }

}



