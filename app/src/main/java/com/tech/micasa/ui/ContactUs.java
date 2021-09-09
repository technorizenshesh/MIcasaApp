package com.tech.micasa.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.databinding.FragmentContactUsBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAddOfferItem;
import com.tech.micasa.retrofit.models.SuccessResContactUs;
import com.tech.micasa.retrofit.models.SuccessResProfileData;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.NetworkAvailablity;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.isValidEmail;
import static com.tech.micasa.constants.Constant.showToast;

public class ContactUs extends Fragment {

    FragmentContactUsBinding binding;
    MicasaInterface micasaInterface;

    String strName ="",strEmail = "",strMessage = "";
    SuccessResProfileData.Result sellerDetail=null;


    public ContactUs() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_contact_us, container, false);

        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);

        setToolbar();
        getProfile();

        binding.btnLogin.setOnClickListener(v -> {

            strName = binding.etName.getText().toString().trim();
            strEmail = binding.etEmail.getText().toString().trim();
            strMessage = binding.etMessage.getText().toString().trim();

            if(isValid())
            {

                if (NetworkAvailablity.getInstance(getContext()).checkNetworkStatus()) {

                    contactUs();

                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getActivity(), getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
            }

        });
        return binding.getRoot();
    }

    private void setToolbar() {

        binding.layoutItemaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layoutItemaction.tvHeader.setText(getContext().getString(R.string.contact_us));

    }

    private boolean isValid() {
        if (strName.equalsIgnoreCase("")) {
            binding.etEmail.setError(getString(R.string.enter_name));
            return false;
        }else if (strEmail.equalsIgnoreCase("")) {
            binding.etEmail.setError(getString(R.string.enter_email));
            return false;
        }  else if (!isValidEmail(strEmail)) {
            binding.etEmail.setError(getString(R.string.enter_valid_email));
            return false;
        }else if (strMessage.equalsIgnoreCase("")) {
            binding.etMessage.setError(getString(R.string.please_enter_pass));
            return false;
        }
        return true;
    }


    public void contactUs()

    {

        binding.etMessage.setText("");
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("email",strEmail);
        map.put("phone","");
        map.put("message",strMessage);
        Call<SuccessResContactUs> call = micasaInterface.contactUs(map);

        call.enqueue(new Callback<SuccessResContactUs>() {
            @Override
            public void onResponse(Call<SuccessResContactUs> call, Response<SuccessResContactUs> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResContactUs data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        showToast(getActivity(), data.message);

                        String dataResponse = new Gson().toJson(response.body());

/*

                        myItemsList.clear();
                        myItemsList.addAll(data.getResult());
                        adapterMyItems.notifyDataSetChanged();
*/

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
            public void onFailure(Call<SuccessResContactUs> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }



    private void getProfile() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);

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
                        setProfileDetails();

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

    private void setProfileDetails() {

        binding.etName.setText(sellerDetail.getFirstName());
        binding.etEmail.setText(sellerDetail.getEmail());

    }

}