package com.tech.micasa.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.tech.micasa.HomeActivity;
import com.tech.micasa.LoginOptionActivity;
import com.tech.micasa.R;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentMyProfileBinding;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.Locale;

public class MyProfileFragment extends Fragment {

    private FragmentMyProfileBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean val =  SharedPreferenceUtility.getInstance(getActivity()).getBoolean(Constant.SELECTED_LANGUAGE);

        if(!val)
        {
            //   updateResources(getActivity().getApplicationContext(),"en");
            setLocale(getActivity(),"en");

        }else
        {
            //    updateResources(getActivity().getApplicationContext(),"es");
            setLocale(getActivity(),"es");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_my_profile,container,false);
       // View root = inflater.inflate(R.layout.fragment_my_profile, container, false);

        binding.layoutMyProfile.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layoutMyProfile.tvHeader.setText(R.string.my_profile);

        boolean val =  SharedPreferenceUtility.getInstance(getActivity()).getBoolean(Constant.SELECTED_LANGUAGE);

        if(!val)
        {
         //   updateResources(getActivity().getApplicationContext(),"en");
            setLocale(getActivity(),"en");

        }else
        {
        //    updateResources(getActivity().getApplicationContext(),"es");
            setLocale(getActivity(),"es");
        }

        binding.llMyProfile.setOnClickListener(v ->
                {
                    Navigation.findNavController(v).navigate(R.id.action_myProfileFragment_to_profileFragment);
                }
                );

        binding.myItemsId.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_myProfileFragment_to_myItemsFragment);
        });

        binding.llPP.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_myProfileFragment_to_privacyPolicyFragment);
        });

        binding.llContactUs.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_myProfileFragment_to_contactUs);
        });

        binding.llTermsNCondition.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_myProfileFragment_to_termsAndConditionFragment);
        });

        binding.chnglangId.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_myProfileFragment_to_changLangFragment);
        });

        binding.llLogout.setOnClickListener(v ->
                {
                    SharedPreferenceUtility.getInstance(getActivity().getApplicationContext()).putBoolean(Constant.IS_USER_LOGGED_IN, false);
                    Intent intent = new Intent(getActivity(),
                            LoginOptionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                       getActivity().finish();

                }
                );
        return binding.getRoot();
    }



    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}