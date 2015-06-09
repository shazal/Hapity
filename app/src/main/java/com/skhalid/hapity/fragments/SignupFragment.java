package com.skhalid.hapity.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.skhalid.hapity.DashboardActivity;
import com.skhalid.hapity.GsonRequest;
import com.skhalid.hapity.Jsonexample;
import com.skhalid.hapity.R;
import com.skhalid.hapity.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import static android.view.View.VISIBLE;


public class SignupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Button btnSelect;
    String ba1;
    Uri mImageUri;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPaasswordConfirmView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) getActivity().findViewById(R.id.email_sign_up);
        mNameView = (AutoCompleteTextView) getActivity().findViewById(R.id.name_sign_up);

        mPasswordView = (EditText) getActivity().findViewById(R.id.password_sign_up);
        mPaasswordConfirmView = (EditText) getActivity().findViewById(R.id.password_sign_up_confirm);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button mEmailSignInButton = (Button) getActivity().findViewById(R.id.email_sign_up_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DashboardActivity.hapityPref.edit().putString("type","manual").commit();
                attemptLogin();
            }
        });

        Button selectPicture = (Button) getActivity().findViewById(R.id.picture_button);
        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                selectImage();
            }
        });

        mLoginFormView = getActivity().findViewById(R.id.sign_up_form);
        mProgressView = getActivity().findViewById(R.id.signup_progress);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_signup, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);
        mPaasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();
        String cpw = mPaasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_empty_name));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(!password.equalsIgnoreCase(cpw)){
            mPaasswordConfirmView.setError("Password does not match");
            focusView = mPaasswordConfirmView;
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

            String url = "http://testing.egenienext.com/project/hapity/webservice/signup/";
            HashMap<String, String> params = new HashMap<String,String>();
            params.put("password", mPasswordView.getText().toString().trim());
            params.put("email", mEmailView.getText().toString().trim());
            params.put("screen_name", mNameView.getText().toString().trim());
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


    private void loadAPI(String url, HashMap<String, String> params) {



        GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                Request.Method.POST,
                url,
                Jsonexample.class,
                null,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());


        DashboardActivity.showCustomProgress(getActivity(), "", false);
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
                    if(DashboardActivity.hapityPref.getString("ba1","0").length()>2){
                        String url = "http://testing.egenienext.com/project/hapity/webservice/insert_profile_picture";
                        HashMap<String, String> params1 = new HashMap<String, String>();
                        params1.put("user_id", DashboardActivity.hapityPref.getInt("userid", 0) + "");
                        params1.put("profile_picture", DashboardActivity.hapityPref.getString("ba1", "0"));
                        loadAPI_picupload(url, params1);
                    }
                    setFullscreen(false);
                    DashboardActivity.action_bar.show();
                    BottomFragment.isHomeActive = true;
                    BottomFragment.homeButton.setImageDrawable(getResources().getDrawable(R.drawable.lists_pressed));
                    BroadcastListFragment twitsFragment = new BroadcastListFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.dash_container, twitsFragment);

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
                DashboardActivity.dismissCustomProgress();
                if (error.networkResponse != null){
                int statuscode = error.networkResponse.statusCode;

                if (statuscode == 404) {
                    Toast.makeText(getActivity(), "user already exist", Toast.LENGTH_LONG).show();
                }

            }else {
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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = null;
                    try
                    {
                        // place where to store camera taken picture
                        photo = new File(Environment.getExternalStorageDirectory(),
                                System.currentTimeMillis() + ".jpg");
                        photo.delete();
                    }
                    catch(Exception e)
                    {

                    }
                    mImageUri= Uri.fromFile(photo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    //start camera intent
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        getActivity().getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            bitmap.recycle();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] ba = bytes.toByteArray();
            ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            DashboardActivity.hapityPref.edit().putString("ba1",ba1).commit();
        }
        catch (Exception e)
        {

        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDither=false;                     //Disable Dithering mode
        options.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        options.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        options.inTempStorage=new byte[32 * 1024];
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        bm.recycle();
        byte[] ba = bytes.toByteArray();
        ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
        DashboardActivity.hapityPref.edit().putString("ba1",ba1).commit();
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
                if (error.networkResponse != null) {
                    int statuscode = error.networkResponse.statusCode;
                    Toast.makeText(getActivity(), "Picture Not Uploaded", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getActivity(), "Some Problem with Network", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

}
