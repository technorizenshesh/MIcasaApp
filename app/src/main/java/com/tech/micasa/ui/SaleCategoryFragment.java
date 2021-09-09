package com.tech.micasa.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.adapter.AdapterSellCat;
import com.tech.micasa.adapter.Item3Adapter;
import com.tech.micasa.databinding.FragmentSaleCategoryBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAllCategories;
import com.tech.micasa.retrofit.models.SuccessResGetAllSubCategories;
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
import static com.tech.micasa.constants.Constant.showToast;


public class SaleCategoryFragment extends Fragment {

    private FragmentSaleCategoryBinding binding;
    MicasaInterface apiInterface;
    public String categoryID;
    public String categoryName;
    List<SuccessResGetAllSubCategories.SubCategory> subCategoryList = new LinkedList<>();
    public AdapterSellCat adapterSellCat;

    public SaleCategoryFragment() {
        // Required empty public constructor
    }

    int[] strings={R.drawable.ic_rectan,R.drawable.ic_rectan,R.drawable.ic_rectan};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentSaleCategoryBinding.inflate(LayoutInflater.from(getContext()));
        //return inflater.inflate(R.layout.fragment_sale_category, container, false);
        apiInterface = ApiClient.getClient().create(MicasaInterface.class);

        Bundle bundle = this.getArguments();
        if(bundle!=null)
        {
           categoryID = bundle.getString("CategoryID");
           categoryName = bundle.getString("category");
        }
        adapterSellCat = new AdapterSellCat(strings,getContext(),subCategoryList,categoryName);

        getAllSubcategories();

        binding.layoutItemSaleaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layoutItemSaleaction.tvHeader.setText(categoryName);

        binding.rvSaleCat.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.rvSaleCat.setAdapter(adapterSellCat);

        return binding.getRoot();
    }

    private void getAllSubcategories() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> map = new HashMap<>();
        map.put("category_id",categoryID);
        map.put("user_id",userId);
        Call<SuccessResGetAllSubCategories> call = apiInterface.getAllSubCategories(map);

        call.enqueue(new Callback<SuccessResGetAllSubCategories>() {
            @Override
            public void onResponse(Call<SuccessResGetAllSubCategories> call, Response<SuccessResGetAllSubCategories> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetAllSubCategories data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        subCategoryList.clear();

                        subCategoryList.addAll(data.getAllSubCategoryList());
                        adapterSellCat.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResGetAllSubCategories> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}