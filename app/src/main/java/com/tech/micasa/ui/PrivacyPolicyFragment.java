package com.tech.micasa.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.databinding.FragmentPrivacyPolicyBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResBannersList;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivacyPolicyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacyPolicyFragment extends Fragment {

    FragmentPrivacyPolicyBinding binding;
    MicasaInterface micasaInterface ;
    String description = "";

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    public static PrivacyPolicyFragment newInstance(String param1, String param2) {
        PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_privacy_policy, container, false);

        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);

        binding.layoutItemaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        getPrivacyPolicy();

        binding.layoutItemaction.tvHeader.setText(R.string.privacy_policy1);
        return binding.getRoot();

    }

    private void getPrivacyPolicy() {


        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);

        Call<SuccessResPrivacyPolicy> call = micasaInterface.getPrivacyPolicy(map);

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

    }
}