package com.tech.micasa.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.adapter.AdapterMyItems;
import com.tech.micasa.databinding.FragmentMyItemsBinding;
import com.tech.micasa.databinding.FragmentMyProfileBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResDeleteItem;
import com.tech.micasa.retrofit.models.SuccessResGetAllSubCategories;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.RemoveItemInterface;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;


public class MyItemsFragment extends Fragment implements RemoveItemInterface {

    FragmentMyItemsBinding binding;


    public MyItemsFragment() {
        // Required empty public constructor
    }

    MicasaInterface apiInterface;

    List<SuccessResGetMyItems.Result> myItemsList = new LinkedList<>();

    AdapterMyItems adapterMyItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentMyItemsBinding.inflate(LayoutInflater.from(getContext()));

        apiInterface = ApiClient.getClient().create(MicasaInterface.class);

        binding.layoutItemaction.imgHeader.setOnClickListener(v ->
                {
                    getActivity().onBackPressed();
                }
                );

        getAllMyItems();

        adapterMyItems = new AdapterMyItems(getContext(),myItemsList,2,this);

        binding.layoutItemaction.tvHeader.setText(R.string.my_items);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        binding.rvMyItems.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        binding.rvMyItems.setAdapter(adapterMyItems);

        return binding.getRoot();
    }

    private void getAllMyItems() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        Call<SuccessResGetMyItems> call = apiInterface.getAllMyItems(map);

        call.enqueue(new Callback<SuccessResGetMyItems>() {
            @Override
            public void onResponse(Call<SuccessResGetMyItems> call, Response<SuccessResGetMyItems> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetMyItems data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        myItemsList.clear();

                        myItemsList.addAll(data.getResult());
                        adapterMyItems.notifyDataSetChanged();

//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);
                    } else if (data.status.equals("0")) {
                        showToast(getActivity(), data.message);
                        myItemsList.clear();

                        myItemsList.addAll(data.getResult());
                        adapterMyItems.notifyDataSetChanged();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetMyItems> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void removeItem(String productId) {
        if(productId!=null)
        {
            String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
            DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
            Map<String,String> map = new HashMap<>();
            map.put("user_id",userId);
            map.put("item_id",productId);
            Call<SuccessResDeleteItem> call = apiInterface.deleteItem(map);

            call.enqueue(new Callback<SuccessResDeleteItem>() {
                @Override
                public void onResponse(Call<SuccessResDeleteItem> call, Response<SuccessResDeleteItem> response) {

                    DataManager.getInstance().hideProgressMessage();
                    try {
                        SuccessResDeleteItem data = response.body();
                        Log.e("data",data.status);
                        if (data.status.equals("1")) {
                     //       showToast(getActivity(), data.message);
                              getAllMyItems();
//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);
                        } else if (data.status.equals("0")) {
                            showToast(getActivity(), data.message);
                            getAllMyItems();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SuccessResDeleteItem> call, Throwable t) {
                    call.cancel();
                    DataManager.getInstance().hideProgressMessage();
                }
            });


        }

    }
}