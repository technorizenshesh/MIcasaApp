package com.tech.micasa.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.adapter.NotificationAdapter;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentNotificationBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.retrofit.models.SuccessResGetNotifications;
import com.tech.micasa.retrofit.models.SuccessResRequestStatus;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.RequestStatusInterface;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.setLocale;
import static com.tech.micasa.constants.Constant.showToast;

public class NotificationsFragment extends Fragment{

    FragmentNotificationBinding binding;

    MicasaInterface micasaInterface;
    NotificationAdapter notificationAdapter;

    List<SuccessResGetNotifications.Result> notificationsList = new LinkedList<>();


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

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notification, container, false);
//        notificationAdapter = new NotificationAdapter(getContext(),notificationsList,this);
        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);

        binding.ivBack.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });


        binding.rvNotifications.setHasFixedSize(true);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotifications.setAdapter(notificationAdapter);
//        getNotifications();
        return binding.getRoot();
    }
/*
    public void getNotifications()
    {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        Call<SuccessResGetNotifications> call = micasaInterface.getNotifications(map);
        call.enqueue(new Callback<SuccessResGetNotifications>() {
            @Override
            public void onResponse(Call<SuccessResGetNotifications> call, Response<SuccessResGetNotifications> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetNotifications data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        notificationsList.clear();
                        notificationsList.addAll(data.getResult());
                        notificationAdapter.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResGetNotifications> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }*/
/*
    @Override
    public void myCallback(String status,String notiStatus) {

        String userId = notification.getUserId();
        String sellerId = notification.getSellerId();
        String itemId = notification.getItemId();
        String notificationId = notification.getId();

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("seller_id",sellerId);
        map.put("item_id",itemId);
        map.put("noti_id",notificationId);
        map.put("noti_status",notiStatus);
        map.put("status",status);

        Call<SuccessResRequestStatus> call = micasaInterface.updateRequestStatus(map);
        call.enqueue(new Callback<SuccessResRequestStatus>() {
            @Override
            public void onResponse(Call<SuccessResRequestStatus> call, Response<SuccessResRequestStatus> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResRequestStatus data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        getNotifications();

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

    }*/
}