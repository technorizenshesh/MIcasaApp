package com.tech.micasa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.micasa.databinding.ActivityForgotBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResForgetPassword;
import com.tech.micasa.retrofit.models.SuccessResSignIn;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.NetworkAvailablity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.isValidEmail;
import static com.tech.micasa.constants.Constant.showToast;

public class ForgotActivity extends AppCompatActivity {

    private ActivityForgotBinding binding;
    private String strEmail = "";
    MicasaInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiInterface = ApiClient.getClient().create(MicasaInterface.class);

        binding.forgotAction.imgHeader.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        binding.forgotAction.tvHeader.setText(getString(R.string.forgot_password));

        binding.btnSend.setOnClickListener(v ->
                {

                    strEmail = binding.etEmail.getText().toString().trim();

                    if(isValid())
                    {

                        if (NetworkAvailablity.getInstance(this).checkNetworkStatus()) {
                            forgotPass();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
                );
    }

    private void forgotPass() {

        DataManager.getInstance().showProgressMessage(ForgotActivity.this, getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("email",strEmail);
        Call<SuccessResForgetPassword> call = apiInterface.forgotPassword(map);
        call.enqueue(new Callback<SuccessResForgetPassword>() {
            @Override
            public void onResponse(Call<SuccessResForgetPassword> call, Response<SuccessResForgetPassword> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResForgetPassword data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        Toast.makeText(ForgotActivity.this,"Please check mail",Toast.LENGTH_SHORT).show();
//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);
                      binding.etEmail.setText("");
                    } else if (data.status.equals("0")) {
                        showToast(ForgotActivity.this, data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResForgetPassword> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }


    private boolean isValid() {
        if (strEmail.equalsIgnoreCase("")) {
            binding.etEmail.setError("Please enter email.");
            return false;
        } else if (!isValidEmail(strEmail)) {
            binding.etEmail.setError("Please enter valid email.");
            return false;
        }
        return true;
    }


}