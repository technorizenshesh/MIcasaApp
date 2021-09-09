package com.tech.micasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.ActivityLoginBinding;
import com.tech.micasa.databinding.ActivityLoginOptionBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResRequestStatus;
import com.tech.micasa.retrofit.models.SuccessResSignIn;
import com.tech.micasa.retrofit.models.SuccessResSignUp;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.NetworkAvailablity;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.isValidEmail;
import static com.tech.micasa.constants.Constant.showToast;

public class LoginActivity extends AppCompatActivity {

    MicasaInterface apiInterface;
    public static String TAG = "LoginActivity";

    private ActivityLoginBinding binding;
    private String strEmail ="",strPassword= "",deviceToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiInterface = ApiClient.getClient().create(MicasaInterface.class);

        getToken();

        binding.loginAction.imgHeader.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        binding.buttonFotgotAction.setOnClickListener(v -> {
            startActivity(new Intent(this,ForgotActivity.class));
        });

        binding.tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(this,SignUpActivity.class));
        });

        binding.btnLogin.setOnClickListener(v -> {

            strEmail = binding.etEmail.getText().toString().trim();
            strPassword = binding.etPassword.getText().toString().trim();

            if(isValid())
            {

                if (NetworkAvailablity.getInstance(this).checkNetworkStatus()) {

                    login();

                } else {
                    Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this,  getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void login() {

        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
         Map<String,String> map = new HashMap<>();
         map.put("email",strEmail);
         map.put("password",strPassword);
         map.put("register_id",deviceToken);

      /*  RequestBody email = RequestBody.create(MediaType.parse("text/plain"),strEmail);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), strPassword);
        RequestBody registerID = RequestBody.create(MediaType.parse("text/plain"),deviceToken);
*/
//        Call<SuccessResSignIn> call = apiInterface.login(email,password,registerID);
        Call<SuccessResSignIn> call = apiInterface.login(map);

        call.enqueue(new Callback<SuccessResSignIn>() {
             @Override
             public void onResponse(Call<SuccessResSignIn> call, Response<SuccessResSignIn> response) {

                 DataManager.getInstance().hideProgressMessage();
                 try {
                     SuccessResSignIn data = response.body();
                     Log.e("data",data.status);
                     if (data.status.equals("1")) {
                         String dataResponse = new Gson().toJson(response.body());
                         Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                         SharedPreferenceUtility.getInstance(getApplication()).putBoolean(Constant.IS_USER_LOGGED_IN, true);
                         SharedPreferenceUtility.getInstance(LoginActivity.this).putString(Constant.USER_ID,data.getResult().getId());

                         boolean val =  SharedPreferenceUtility.getInstance(LoginActivity.this).getBoolean(Constant.SELECTED_LANGUAGE);

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
                         showToast(LoginActivity.this,""+ getString(R.string.you_have_enteed));
                     }

                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }

             @Override
             public void onFailure(Call<SuccessResSignIn> call, Throwable t) {
                 call.cancel();
                 DataManager.getInstance().hideProgressMessage();
             }
         });
    }

    private boolean isValid() {
       if (strEmail.equalsIgnoreCase("")) {
            binding.etEmail.setError(getString(R.string.enter_email));
            return false;
        } else if (!isValidEmail(strEmail)) {
            binding.etEmail.setError(getString(R.string.enter_valid_email));
            return false;
        }else if (strPassword.equalsIgnoreCase("")) {
            binding.etPassword.setError(getString(R.string.please_enter_pass));
            return false;
        }
        return true;
    }

    /**
     * This method is used to get fcm token
     */
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
            Toast.makeText(LoginActivity.this, "Error=>" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void changeLanguage(String lang) {

     String   strUserId = SharedPreferenceUtility.getInstance(LoginActivity.this).getString(USER_ID);


//        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();

        map.put("user_id",strUserId);
        map.put("language",lang);

        Call<SuccessResRequestStatus> call = apiInterface.changeLanguage(map);

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

                        Toast.makeText(LoginActivity.this,""+ R.string.logged_in_successfull,Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,TurnOnGPSActivity.class));
                        finish();

//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);
                    } else if (data.status.equals("0")) {
                        showToast(LoginActivity.this, data.message);
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