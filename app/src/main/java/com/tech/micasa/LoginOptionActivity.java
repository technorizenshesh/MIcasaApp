package com.tech.micasa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.ActivityLoginOptionBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResRequestStatus;
import com.tech.micasa.retrofit.models.SuccessResSignUp;
import com.tech.micasa.retrofit.models.SuccessResSocialLogin;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.GPSTracker;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class LoginOptionActivity extends AppCompatActivity {


    // [END declare_auth]

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    private ActivityLoginOptionBinding binding;
    String strName = "", strEmail = "", strAddress = "", strLat = "", strLng = "",strSocialId ="",deviceToken = "",strPhoneNumber = "";
    MicasaInterface micasaInterface;
    Uri imageUri;

    GPSTracker gpsTracker;


    String str_image_path = "";
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    // [START declare_auth]
    // [END declare_auth]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);
        gpsTracker = new GPSTracker(this);
        getLocation();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        getToken();

        String text = getString(R.string.termsofservice) +
                " <font color=#000000>&</font> <font color=#ef7c0c>Privacy Policy</font>";

       binding.privacyPolicy.setText(Html.fromHtml(text));

       binding.tvLogin.setOnClickListener(v -> {
           startActivity(new Intent(this,LoginActivity.class));
       });
        binding.createId.setOnClickListener(v -> {
            startActivity(new Intent(this,SignUpActivity.class));
        });

        binding.btnGoogleLogin.setOnClickListener(v ->
                {
                    signIn();
                }
                );

        binding.btnFacebookSignin.setOnClickListener(v ->
                {
                    FacebookSdk.setApplicationId("872245676666947");
                    FacebookSdk.sdkInitialize(LoginOptionActivity.this);
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
                    LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Log.e("kjsgdfkjdgsf","onCancel");
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.e("kjsgdfkjdgsf","error = " + error.getMessage());
                        }

                    });
                }
        );

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

                strName = account.getDisplayName();
                strEmail = account.getEmail();
                str_image_path = account.getPhotoUrl().toString();
                strSocialId = account.getId();
                socialLogin();

               // firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void socialLogin()

    {

//        strLat ="22.698986";
//        strLng = "75.867851";

       DataManager.getInstance().showProgressMessage(LoginOptionActivity.this, getString(R.string.please_wait));
    /*    MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }*/
        RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), strName);
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"),strEmail);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), strAddress);
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), strLat);
        RequestBody lng = RequestBody.create(MediaType.parse("text/plain"), strLng);
        RequestBody registerID = RequestBody.create(MediaType.parse("text/plain"),deviceToken);
        RequestBody socialId = RequestBody.create(MediaType.parse("text/plain"),strSocialId);
        RequestBody image = RequestBody.create(MediaType.parse("text/plain"),str_image_path);

        Call<SuccessResSocialLogin> signupCall = micasaInterface.socialLogin(firstName,email,  address,lat,lng,registerID,socialId,image);
        signupCall.enqueue(new Callback<SuccessResSocialLogin>() {
            @Override
            public void onResponse(Call<SuccessResSocialLogin> call, Response<SuccessResSocialLogin> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResSocialLogin data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        SharedPreferenceUtility.getInstance(getApplication()).putBoolean(Constant.IS_USER_LOGGED_IN, true);
                        SharedPreferenceUtility.getInstance(LoginOptionActivity.this).putString(Constant.USER_ID,data.getResult().getId());

                        boolean val =  SharedPreferenceUtility.getInstance(LoginOptionActivity.this).getBoolean(Constant.SELECTED_LANGUAGE);

                        if(!val)
                        {
                            changeLanguage("en");
                        }else
                        {
                            changeLanguage("sp");

                        }


//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);
                    } else if (data.status.equals("0")) {
                        showToast(LoginOptionActivity.this, data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResSocialLogin> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            strName = user.getDisplayName();
                            strEmail = user.getEmail();
                            if(strEmail==null)
                            {
                                strEmail = "";
                            }
                            str_image_path = user.getPhotoUrl().toString();
                            strSocialId = user.getUid();
                            strPhoneNumber = user.getPhoneNumber();
                            socialLogin();
                        //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginOptionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                          //  updateUI(null);
                        }
                    }
                });
    }


    private void getToken() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            // Get new FCM registration token
                            String token = task.getResult();
                            deviceToken = token;
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(LoginOptionActivity.this, "Error=>" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("Latittude====", gpsTracker.getLatitude() + "");

                    strLat = Double.toString(gpsTracker.getLatitude()) ;
                    strLng = Double.toString(gpsTracker.getLongitude()) ;

//
//                    if (isContinue) {
//                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//                    } else {
//                        Log.e("Latittude====", gpsTracker.getLatitude() + "");
//
//                        strLat = Double.toString(gpsTracker.getLatitude()) ;
//                        strLng = Double.toString(gpsTracker.getLongitude()) ;
//
//                    }
                } else {
                    Toast.makeText(LoginOptionActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }

         /*   case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(LoginOptionActivity.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
                break;
            }*/
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(LoginOptionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginOptionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.LOCATION_REQUEST);
        } else {
            Log.e("Latittude====",gpsTracker.getLatitude()+"");
            strLat = Double.toString(gpsTracker.getLatitude()) ;
            strLng = Double.toString(gpsTracker.getLongitude()) ;
        }
    }

    private void changeLanguage(String lang) {

        String   strUserId = SharedPreferenceUtility.getInstance(LoginOptionActivity.this).getString(USER_ID);


//        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();

        map.put("user_id",strUserId);
        map.put("language",lang);

        Call<SuccessResRequestStatus> call = micasaInterface.changeLanguage(map);

        call.enqueue(new Callback<SuccessResRequestStatus>() {
            @Override
            public void onResponse(Call<SuccessResRequestStatus> call, Response<SuccessResRequestStatus> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResRequestStatus data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        //       showToast(LoginActivity.this, data.message);
                        String dataResponse = new Gson().toJson(response.body());
                        Toast.makeText(LoginOptionActivity.this, ""+R.string.logged_in_successfully,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginOptionActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);
                    } else if (data.status.equals("0")) {
                        showToast(LoginOptionActivity.this, data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResRequestStatus> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });


    }

}

