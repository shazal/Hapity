package com.skhalid.hapity.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.Jsonexample;
import com.skhalid.hapity.R;
import com.skhalid.hapity.VolleySingleton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.VISIBLE;
import static android.view.View.getDefaultSize;

/**
 * A login screen that offers login via email/password.
 */
public class LoginFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private Button loginButton;
    CallbackManager callbackManager;
    Button fbLoginButton;
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
    private View mLoginFormView;
    public static String type;
    private String UserID;
    HashMap<String, String> params;
    public static SharedPreferences pref;
    public static final String PREFS_NAME = "MyPrefsFile";
    private TwitterAuthClient mTwitterAuthClient;
    String pic = "";
    String ba1 = "";

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
        DashboardActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        pref = getActivity().getSharedPreferences(PREFS_NAME, 0);
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
                DashboardActivity.hapityPref.edit().putString("type",type).commit();
                attemptLogin();
            }
        });

        Button signup = (Button) getActivity().findViewById(R.id.sign_up_fragment_button);
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                SignupFragment signup = new SignupFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.dash_container, signup, "SignupFragment");
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//		transaction.addToBackStack("login");
//      getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.commit();
            }
        });

        Button selectPicture = (Button) getActivity().findViewById(R.id.picture_button);

        mLoginFormView = getActivity().findViewById(R.id.login_form);


       
        fbLoginButton = (Button) getActivity().findViewById(R.id.fb_login_button);
        loginButton = (Button) getActivity().findViewById(R.id.twitter_login_button);


        mTwitterAuthClient= new TwitterAuthClient();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                mTwitterAuthClient.authorize(getActivity(), new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {

                        DashboardActivity.showCustomProgress(getActivity(), "", false);
                        type = "twitter";
                        TwitterSession session =
                                Twitter.getSessionManager().getActiveSession();
                        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);

                        twitterApiClient.getAccountService().verifyCredentials(true, false, new Callback<User>() {
                            @Override
                            public void success(Result<User> result) {
                                pic = result.data.profileImageUrl;
                                ImageRequest ir = new ImageRequest(pic, new Response.Listener<Bitmap>() {

                                    @Override
                                    public void onResponse(Bitmap response) {
                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                        response.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                                        byte[] ba = bytes.toByteArray();
                                        ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                                        DashboardActivity.hapityPref.edit().putString("ba1",ba1).commit();

                                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                                        scheduler.schedule(new Runnable() {

                                            @Override
                                            public void run() {

                                                String url = "http://testing.egenienext.com/project/hapity/webservice/insert_profile_picture";
                                                HashMap<String, String> params1 = new HashMap<String, String>();
                                                params1.put("user_id", DashboardActivity.hapityPref.getInt("userid", 0) + "");
                                                params1.put("profile_picture", DashboardActivity.hapityPref.getString("ba1", "0"));
                                                loadAPI_picupload(url, params1);

                                            }
                                        }, 5, TimeUnit.SECONDS);


                                    }
                                }, 0, 0, null, null);
                                VolleySingleton.getInstance(getActivity()).addToRequestQueue(ir);
                            }

                            @Override
                            public void failure(TwitterException e) {

                            }
                        });
                        DashboardActivity.hapityPref.edit().putString("type",type).commit();
                        String url = "http://testing.egenienext.com/project/hapity/webservice/signin/";
                        params = new HashMap<String,String>();
                        UserID = twitterSessionResult.data.getUserId() + "";
                        params.put("twitter_id", UserID);
                        loadAPI(url, params);

                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });


            }
        });

        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().setLoginBehavior(LoginBehavior.SSO_WITH_FALLBACK);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        try {
                            DashboardActivity.showCustomProgress(getActivity(), "", false);
                            type = "facebook_id";
                            DashboardActivity.hapityPref.edit().putString("type",type).commit();
                            DashboardActivity.aToken = loginResult.getAccessToken();
                            params = new HashMap<String, String>();
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            // Application code
                                            try {

                                                UserID = object.getString("id");
                                                pic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                                ImageRequest ir = new ImageRequest(pic, new Response.Listener<Bitmap>() {

                                                    @Override
                                                    public void onResponse(Bitmap response) {
                                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                                        response.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                                        byte[] ba = bytes.toByteArray();
                                                        ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                                                        DashboardActivity.hapityPref.edit().putString("ba1",ba1).commit();
                                                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                                                        scheduler.schedule(new Runnable() {

                                                            @Override
                                                            public void run() {

                                                                        String url = "http://testing.egenienext.com/project/hapity/webservice/insert_profile_picture";
                                                                HashMap<String, String> params1 = new HashMap<String, String>();
                                                                        params1.put("user_id", DashboardActivity.hapityPref.getInt("userid", 0) + "");
                                                                        params1.put("profile_picture", DashboardActivity.hapityPref.getString("ba1", "0"));
                                                                        loadAPI_picupload(url, params1);

                                                            }
                                                        }, 5, TimeUnit.SECONDS);
                                                    }
                                                }, 0, 0, null, null);
                                                VolleySingleton.getInstance(getActivity()).addToRequestQueue(ir);
                                                params.put(type, UserID);
                                                String url = "http://testing.egenienext.com/project/hapity/webservice/signin/";
                                                loadAPI(url, params);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday, picture.width(300)");
                            request.setParameters(parameters);
                            request.executeAsync();
//                    Profile.fetchProfileForCurrentAccessToken();
//                    UserID = Profile.getCurrentProfile().getId();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithPublishPermissions(getActivity(), Arrays.asList("publish_actions"));
            }
        });

        if(DashboardActivity.hapityPref.getString("loggedin","0").equalsIgnoreCase("1")) {
            setFullscreen(false);
            DashboardActivity.action_bar.show();
            BottomFragment.isHomeActive = true;
            BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_pressed));
            BroadcastListFragment twitsFragment = new BroadcastListFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.dash_container, twitsFragment, "BroadcastListFragment");

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack("posts");
//                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.commitAllowingStateLoss();
            DashboardActivity.bottom_fragment.getView().setVisibility(VISIBLE);
        }

        TextView fPW = (TextView) getActivity().findViewById(R.id.forgotpw);
        fPW.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
        if(requestCode == 195278 || requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if(requestCode == 140) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
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
            DashboardActivity.showCustomProgress(getActivity(), "", false);
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
        return password.length() >= 4;
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
            DashboardActivity.dismissCustomProgress();

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
            DashboardActivity.dismissCustomProgress();
        }
    }

    private void loadAPI(String url, HashMap<String, String> params) {



        GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                Request.Method.POST,
                url,
                Jsonexample.class,
                null,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        myReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }

    private Response.Listener<Jsonexample> createMyReqSuccessListener() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {

                    DashboardActivity.dismissCustomProgress();
                    DashboardActivity.hapityPref.edit().putInt("userid",response.user_id).commit();
                    DashboardActivity.hapityPref.edit().putString("loggedin", "1").commit();
                    setFullscreen(false);
                    DashboardActivity.action_bar.show();
                    BottomFragment.isHomeActive = true;
                    BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_pressed));
                    BroadcastListFragment twitsFragment = new BroadcastListFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.dash_container, twitsFragment, "BroadcastListFragment");

                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack("posts");
//                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commitAllowingStateLoss();
                    DashboardActivity.bottom_fragment.getView().setVisibility(VISIBLE);

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
                if (error.networkResponse != null){
                    int statuscode = error.networkResponse.statusCode;

                if (statuscode == 500) {
                    if (type.equalsIgnoreCase("manual")) {
                        DashboardActivity.dismissCustomProgress();
                        Toast.makeText(getActivity(), "User Not Found/Server Error", Toast.LENGTH_LONG).show();
                    } else {
                        String url = "http://testing.egenienext.com/project/hapity/webservice/signup/";

                        loadAPI_SignUP(url, params);
                    }
                }

            } else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                    DashboardActivity.dismissCustomProgress();
                }
            }
        };
    }

    private void loadAPI_SignUP(String url, HashMap<String, String> params) {



        GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                Request.Method.POST,
                url,
                Jsonexample.class,
                null,
                params,
                createMyReqSuccessListener_SignUP(),
                createMyReqErrorListener_SignUP());


        DashboardActivity.showCustomProgress(getActivity(), "", false);
        myReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }

    private Response.Listener<Jsonexample> createMyReqSuccessListener_SignUP() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {
                    DashboardActivity.dismissCustomProgress();
                    DashboardActivity.hapityPref.edit().putString("loggedin", "1").commit();
                    DashboardActivity.hapityPref.edit().putInt("userid", response.user_id).commit();
                    setFullscreen(false);
                    DashboardActivity.action_bar.show();
                    BottomFragment.isHomeActive = true;
                    BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_pressed));
                    BroadcastListFragment twitsFragment = new BroadcastListFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.dash_container, twitsFragment, "BroadcastListFragment");

                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack("posts");
//                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commitAllowingStateLoss();
                    DashboardActivity.bottom_fragment.getView().setVisibility(VISIBLE);

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
                DashboardActivity.dismissCustomProgress();
                if (error.networkResponse != null){
                    int statuscode = error.networkResponse.statusCode;

                if (statuscode == 404) {
                    Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_LONG);
                }
            } else {
                    Toast.makeText(getActivity(), "Some Problem With Network", Toast.LENGTH_LONG).show();
                }

            }
        };
    }

    private void setFullscreen(boolean fullscreen)
    {
        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        if (fullscreen)
        {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        else
        {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getActivity().getWindow().setAttributes(attrs);
    }



    private void loadAPI_picupload(String url, HashMap<String, String> params) {



        GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                Request.Method.POST,
                url,
                Jsonexample.class,
                null,
                params,
                createMyReqSuccessListener_picupload(),
                createMyReqErrorListener_picupload());

        myReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(myReq);
    }

    private Response.Listener<Jsonexample> createMyReqSuccessListener_picupload() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {
                    DashboardActivity.hapityPref.edit().putString("ba1","0").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListener_picupload() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DashboardActivity.hapityPref.edit().putString("ba1","0").commit();
                if (error.networkResponse != null){
                    int statuscode = error.networkResponse.statusCode;


                }
            }
        };
    }
}



