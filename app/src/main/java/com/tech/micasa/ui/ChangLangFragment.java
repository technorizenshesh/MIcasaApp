package com.tech.micasa.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentChangLangBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.retrofit.models.SuccessResRequestStatus;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;


public class ChangLangFragment extends Fragment {


    FragmentChangLangBinding binding;

    MicasaInterface micasaInterface;

    String strUserId = "", language = "";


    public ChangLangFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chang_lang, container, false);
        micasaInterface= ApiClient.getClient().create(MicasaInterface.class);

        binding.layoutLang.tvHeader.setText(R.string.change_language);
        binding.layoutLang.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        boolean isEnglish = SharedPreferenceUtility.getInstance(getActivity().getApplicationContext()).getBoolean(Constant.SELECTED_LANGUAGE);

        if(isEnglish)
        {
            binding.radio3.setChecked(true);
        }
        else {
            binding.radio1.setChecked(true);
        }

        binding.radio1.setOnClickListener(v ->
                {
                    updateResources(getActivity(),"en");
                    binding.radio3.setChecked(false);
                    changeLanguage("en");
                    SharedPreferenceUtility.getInstance(getActivity().getApplicationContext()).putBoolean(Constant.SELECTED_LANGUAGE, false);
                }
                );

        binding.radio3.setOnClickListener(v ->
                {
                    updateResources(getActivity(),"es");
                    binding.radio1.setChecked(false);
                    changeLanguage("sp");
                    SharedPreferenceUtility.getInstance(getActivity()).putBoolean(Constant.SELECTED_LANGUAGE, true);
                }
        );
        return binding.getRoot();
    }

    private void changeLanguage(String lang) {

        strUserId = SharedPreferenceUtility.getInstance(getActivity()).getString(USER_ID);


        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
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
                        showToast(getActivity(), data.message);
                        String dataResponse = new Gson().toJson(response.body());



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
            public void onFailure(Call<SuccessResRequestStatus> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });


    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}