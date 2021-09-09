package com.tech.micasa.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.adapter.AdapterItemDetail;
import com.tech.micasa.adapter.Item2Adapter;
import com.tech.micasa.adapter.Item3Adapter;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentItemDetailBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.retrofit.models.SuccessResGetProductByCategory;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.GPSTracker;
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


public class ItemDetailFragment extends Fragment {

    FragmentItemDetailBinding binding;
    GPSTracker gpsTracker;

    private AdapterItemDetail adapterItemDetail;

    MicasaInterface micasaInterface;
    String strLat = "",strLng ="",strCategoryID = "";

    public List<SuccessResGetProductByCategory.Result> productsList = new LinkedList<>();

    int[] strings={R.drawable.horse,R.drawable.chairs,R.drawable.horse};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.fragment_item_detail,container,false);
        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);
        gpsTracker = new GPSTracker(getActivity());
        getLocation();

        Bundle bundle = this.getArguments();
        if(bundle!=null)
        {
            strCategoryID = bundle.getString("CategoryID");
        }

        binding.searchView.setOnClickListener(v ->

        {

            Bundle bundle1 = new Bundle();
            bundle1.putString("key","2");
            bundle1.putString("categoryId",strCategoryID);
            Navigation.findNavController(v).navigate(R.id.action_itemDetailFragment_to_searchItemFragment,bundle1);

        });

        getItemsByCategory();
        return binding.getRoot();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("Latittude====", gpsTracker.getLatitude() + "");

                    strLat = Double.toString(gpsTracker.getLatitude()) ;
                    strLng = Double.toString(gpsTracker.getLongitude()) ;

                  //  action_itemDetailFragment_to_searchItemFragment
//
//                    if (isContinue) {
//                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//                    } else {
//                        Log.e("Latittude====", gpsTracker.getLatitude() + "");
//
//                        strLat = Double.toString(gpsTracker.getLatitude()) ;
//                        strLng = Double.toString(gpsTracker.getLongitude()) ;
//
//                    }
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void getItemsByCategory() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> map = new HashMap<>();
        map.put("category_id",strCategoryID);
        map.put("lat",strLat);
        map.put("lon",strLng);

        Call<SuccessResGetProductByCategory> call = micasaInterface.getProductsByCategory(map);

        call.enqueue(new Callback<SuccessResGetProductByCategory>() {
            @Override
            public void onResponse(Call<SuccessResGetProductByCategory> call, Response<SuccessResGetProductByCategory> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetProductByCategory data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        productsList.clear();

                        productsList.addAll(data.getResult());
                        adapterItemDetail.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResGetProductByCategory> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.layoutItemDetailaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        adapterItemDetail = new  AdapterItemDetail(strings,getContext(),productsList);

        binding.layoutItemDetailaction.tvHeader.setText(R.string.item_details);

        //return inflater.inflate(R.layout.fragment_item_all, container, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        binding.rvItemDetail.setLayoutManager(gridLayoutManager);
        binding.rvItemDetail.setAdapter(adapterItemDetail);

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.LOCATION_REQUEST);
        } else {
            Log.e("Latittude====",gpsTracker.getLatitude()+"");
            strLat = Double.toString(gpsTracker.getLatitude()) ;
            strLng = Double.toString(gpsTracker.getLongitude()) ;
        }
    }


}