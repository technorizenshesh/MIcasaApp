package com.tech.micasa.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tech.micasa.LoginActivity;
import com.tech.micasa.R;
import com.tech.micasa.TurnOnGPSActivity;
import com.tech.micasa.adapter.ViewPagerAdapter;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentProduUserProfileBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResGetUserProfile;
import com.tech.micasa.retrofit.models.SuccessResProfileData;
import com.tech.micasa.retrofit.models.SuccessResSignIn;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.showToast;


public class ProduUserProfileFragment extends Fragment {


    MicasaInterface micasaInterface;

    private FragmentProduUserProfileBinding binding;

    private String userID = "";

    SuccessResProfileData.Result sellerDetail=null;

    public ProduUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_produ_user_profile,container,false);

      //  return inflater.inflate(R.layout.fragment_produ_user_profile, container, false);

        Bundle bundle = this.getArguments();

        if(bundle!=null)
        {
            userID = bundle.getString("userID");
            SharedPreferenceUtility.getInstance(getContext()).putString(Constant.SELLER_ID,userID);
        }

        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);

        getSellerProfile();

        binding.layHeaprodUser.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layHeaprodUser.tvHeader.setText(" ");
        binding.viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager );
        return binding.getRoot();

    }

    private void getSellerProfile() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();

        map.put("user_id",userID);
      /*  RequestBody email = RequestBody.create(MediaType.parse("text/plain"),strEmail);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), strPassword);
        RequestBody registerID = RequestBody.create(MediaType.parse("text/plain"),deviceToken);
*/
//        Call<SuccessResSignIn> call = apiInterface.login(email,password,registerID);
        Call<SuccessResProfileData> call = micasaInterface.getSellerDetails(map);

        call.enqueue(new Callback<SuccessResProfileData>() {
            @Override
            public void onResponse(Call<SuccessResProfileData> call, Response<SuccessResProfileData> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResProfileData data = response.body();
                    sellerDetail = data.getResult();
//                    setSellerData();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        setSellerDetails();

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
            public void onFailure(Call<SuccessResProfileData> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    private void setSellerDetails() {

        Glide
                .with(getActivity())
                .load(sellerDetail.getImage())
                .centerCrop()
                .into(binding.ivProfile);

        binding.tvName.setText(sellerDetail.getFirstName());
        binding.tvEmail.setText(sellerDetail.getEmail());

        if(sellerDetail.getAddress().equalsIgnoreCase(""))
        {
            binding.tvAddress.setVisibility(View.GONE);
        }
        else
        {
            binding.tvAddress.setVisibility(View.VISIBLE);
        }
        binding.tvAddress.setText(sellerDetail.getAddress());

    }


}