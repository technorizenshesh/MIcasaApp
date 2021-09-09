package com.tech.micasa.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.micasa.LoginActivity;
import com.tech.micasa.R;
import com.tech.micasa.TurnOnGPSActivity;
import com.tech.micasa.adapter.AdapterSellItems;
import com.tech.micasa.adapter.Item3Adapter;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentItemSellBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAllCategories;
import com.tech.micasa.retrofit.models.SuccessResSignIn;
import com.tech.micasa.utility.DataManager;
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

public class ItemSellFragment extends Fragment {


    private FragmentItemSellBinding binding;
    MicasaInterface apiInterface;
    AdapterSellItems adapterSellItems;
    String userID;
    int[] strings={R.drawable.horse,R.drawable.chairs,R.drawable.horse};

    List<SuccessResAllCategories.Category> categories = new LinkedList<>();

    public ItemSellFragment() {
        // Required empty public constructor
    }


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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentItemSellBinding.inflate(LayoutInflater.from(getContext()));
        //return inflater.inflate(R.layout.fragment_item_sell, container, false);
        apiInterface = ApiClient.getClient().create(MicasaInterface.class);


        getCategories();
        adapterSellItems = new AdapterSellItems(strings,getContext(),categories,false);

        binding.layoutItemaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layoutItemaction.tvHeader.setText(R.string.select_a_category);


//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
//        binding.recyclerView1.setLayoutManager(gridLayoutManager);
//        binding.recyclerView1.setAdapter(new AdapterSellItems(strings,getContext()));

        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        binding.recyclerView1.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        binding.recyclerView1.setAdapter(adapterSellItems);

        return binding.getRoot();
    }

    private void getCategories() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);

        Call<SuccessResAllCategories> call = apiInterface.getAllCategories(map);

        call.enqueue(new Callback<SuccessResAllCategories>() {
            @Override
            public void onResponse(Call<SuccessResAllCategories> call, Response<SuccessResAllCategories> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAllCategories data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        categories.clear();

                        categories.addAll(data.getCategory());
                        adapterSellItems.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResAllCategories> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }
}