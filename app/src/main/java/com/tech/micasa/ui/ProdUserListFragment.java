package com.tech.micasa.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.adapter.AdapterMyItems;
import com.tech.micasa.adapter.Item2Adapter;
import com.tech.micasa.databinding.FragmentProdUserListBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAllCategories;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.SELLER_ID;
import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;

public class ProdUserListFragment extends Fragment {


    private FragmentProdUserListBinding binding;
    List<SuccessResAllCategories.Category> categories = new LinkedList<>();

    ArrayAdapter ad;

    String[] courses = { "Sun Jan25", "Mon Jan25",
            "Sun Jan25", "Sun Jan25",
            "Sun Jan25", "Sun Jan25" };


    public ProdUserListFragment() {
        // Required empty public constructor
    }

    MicasaInterface apiInterface;

    List<SuccessResGetMyItems.Result> myItemsList = new LinkedList<>();

    Item2Adapter adapterMyItems;

    int[] strings={R.drawable.felix,R.drawable.felix,R.drawable.felix};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentProdUserListBinding.inflate(LayoutInflater.from(getContext()));

       // return inflater.inflate(R.layout.fragment_prod_user_list, container, false);

        //return inflater.inflate(R.layout.fragment_chat_list, container, false);

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(SELLER_ID);

        binding.searchView.setOnClickListener(v ->
                {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("key","3");
                    bundle1.putString("userId",userId);
                    Navigation.findNavController(v).navigate(R.id.action_produUserProfileFragment_to_searchItemFragment,bundle1);
                }
        );

        adapterMyItems = new Item2Adapter(getContext(),myItemsList,2);
        apiInterface = ApiClient.getClient().create(MicasaInterface.class);
        getCategories();

        getAllMyItems();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        binding.rvProUserList.setLayoutManager(gridLayoutManager);
        binding.rvProUserList.setAdapter(adapterMyItems);
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
                        setAdapterList(categories);

                        //adapterSellItems.notifyDataSetChanged();

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

    List<String> spinnerList = new LinkedList<>();
    public void setAdapterList(List<SuccessResAllCategories.Category> categories)
    {

        spinnerList.clear();
        spinnerList.add(getString(R.string.all_categories));
        for(SuccessResAllCategories.Category category:categories)
        {
            spinnerList.add(category.getCategoryName());
        }
        setSpinner();
    }

    public void getItemByCategoryId(int position)
    {
        int mainListPosition;
        if(position==0)
        {
            getAllMyItems();
        }else
        {
           mainListPosition  = position-1;
           String categoryId = categories.get(mainListPosition).getId();
            String userId = SharedPreferenceUtility.getInstance(getContext()).getString(SELLER_ID);
            DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
            Map<String,String> map = new HashMap<>();
            map.put("category_id",categoryId);
            map.put("user_id",userId);
            Call<SuccessResGetMyItems> call = apiInterface.userProductByCategory(map);
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

    }


    private void setSpinner() {

        ad = new ArrayAdapter(
                getActivity(),
                R.layout.spinner_selected_item,
                spinnerList);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        binding.spinnerFilter.setAdapter(ad);

        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getContext().getColor(R.color.colorGray));

                getItemByCategoryId(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getAllMyItems() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(SELLER_ID);
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

}