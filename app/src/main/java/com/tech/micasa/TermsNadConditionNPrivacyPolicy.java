package com.tech.micasa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.tech.micasa.databinding.ActivityTermsNadConditionNPrivacyPolicyBinding;
import com.tech.micasa.retrofit.models.SuccessResPrivacyPolicy;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;

public class TermsNadConditionNPrivacyPolicy extends AppCompatActivity {
/*
    ActivityTermsNadConditionNPrivacyPolicyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_terms_nad_condition_n_privacy_policy);

        binding.layoutItemaction.tvHeader.setText(getString(R.string.terms_and_condition_and_privacy_policy));
        binding.layoutItemaction.imgHeader.setOnClickListener(v -> finish());



    }


    private void getTermsAndCondition() {


        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);

        Call<SuccessResPrivacyPolicy> call = micasaInterface.getTermsAndCondition(map);

        call.enqueue(new Callback<SuccessResPrivacyPolicy>() {
            @Override
            public void onResponse(Call<SuccessResPrivacyPolicy> call, Response<SuccessResPrivacyPolicy> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResPrivacyPolicy data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        description = data.getResult().getDescription();
                        setWebView();


//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);
                    } else if (data.status.equals("0")) {
                        showToast(getActivity(), data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResPrivacyPolicy> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    private void setWebView() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadData(description, "text/html; charset=utf-8", "UTF-8");

    }*/

}