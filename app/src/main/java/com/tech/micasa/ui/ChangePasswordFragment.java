package com.tech.micasa.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.room.util.DBUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.databinding.FragmentChangePasswordBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResRequestStatus;
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

public class ChangePasswordFragment extends Fragment {

    FragmentChangePasswordBinding binding;
    MicasaInterface micasaInterface;

    String oldPass = "",confirmPass = "" ,newPass = "";

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_change_password, container, false);

        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);
        binding.layoutItemaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        binding.layoutItemaction.tvHeader.setText(R.string.change_pass);

        binding.btnSubmit.setOnClickListener(v ->
                {
                    oldPass = binding.etPassword.getText().toString().trim();
                    confirmPass = binding.etConfirm.getText().toString().trim();
                    newPass = binding.etNewPass.getText().toString().trim();

                    if(isValid())
                    {

                        if (NetworkAvailablity.getInstance(getActivity()).checkNetworkStatus()) {

                            changePassword();

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }

                }
                );

        return binding.getRoot();
    }


    private boolean isValid() {
        if (oldPass.equalsIgnoreCase("")) {
            binding.etPassword.setError(getString(R.string.please_enter_old_pass));
            return false;
        } else if (confirmPass.equalsIgnoreCase("")) {
            binding.etConfirm.setError(getString(R.string.please_enter_confirm_password));
            return false;
        }else if (!confirmPass.equalsIgnoreCase(oldPass)) {
            binding.etConfirm.setError(getString(R.string.password_mismatched));
            return false;
        }else if (newPass.equalsIgnoreCase("")) {
            binding.etNewPass.setError(getString(R.string.enter_new_password));
            return false;
        }
        return true;
    }


    public void changePassword()

    {

        String userId = SharedPreferenceUtility.getInstance(getActivity()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();

        map.put("user_id",userId);
        map.put("current_password",oldPass);
        map.put("password",newPass);

        Call<SuccessResRequestStatus> call = micasaInterface.changePassword(map);

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

                          binding.etNewPass.setText("");
                          binding.etConfirm.setText("");
                          binding.etPassword.setText("");


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

}