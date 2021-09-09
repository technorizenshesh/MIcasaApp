package com.tech.micasa.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.adapter.Item2Adapter;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentSearchItemBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
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

import static android.app.Activity.RESULT_OK;
import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;

public class SearchItemFragment extends Fragment {

    FragmentSearchItemBinding binding;
    MicasaInterface micasaInterface;
    String strLat="",strLng="",strSearch ="",strCategoryID = "",key = "",strUserId = "";
    public int comeFromWhere;

    Item2Adapter adapterMyItems;
    GPSTracker gpsTracker;

    List<SuccessResGetMyItems.Result> myItemsList = new LinkedList<>();


    public SearchItemFragment() {
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
        gpsTracker = new GPSTracker(getActivity());

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_search_item, container, false);
        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);
        adapterMyItems = new Item2Adapter(getActivity(),myItemsList,3);
        getLocation();

        Bundle bundle = this.getArguments();
        if(bundle!=null)
        {
             key = bundle.getString("key");

            if(key.equalsIgnoreCase("2"))
            {
                strCategoryID = bundle.getString("categoryId");
            }else if(key.equalsIgnoreCase("3"))
            {
            strUserId = bundle.getString("userId");
            }
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        binding.recyclerView2.setLayoutManager(gridLayoutManager);
        binding.recyclerView2.setAdapter(adapterMyItems);

        binding.layoutItemaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        binding.layoutItemaction.tvHeader.setText(R.string.search_ite);

        binding.searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    strSearch = binding.searchView.getText().toString().trim();

                    if(key.equalsIgnoreCase("1"))
                    {
                       performSearch();
                    }else if(key.equalsIgnoreCase("2"))
                    {
                        searchWithCategory();
                    }else if(key.equalsIgnoreCase("3"))
                    {
                        searchItemForSeller();
                    }

          return true;
                }
                return false;
            }
        });
        return binding.getRoot();
    }

    private void searchWithCategory() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("category_id",strCategoryID);
        map.put("title",strSearch);
        map.put("lat",strLat);
        map.put("lon",strLng);

        Call<SuccessResGetMyItems> call = micasaInterface.searchWithCategory(map);

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

    public void performSearch()

    {
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("title",strSearch);
        map.put("lat",strLat);
        map.put("lon",strLng);

        Call<SuccessResGetMyItems> call = micasaInterface.searchItem(map);

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

    private void searchItemForSeller() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",strUserId);
        map.put("title",strSearch);

        Call<SuccessResGetMyItems> call = micasaInterface.seachSellerItem(map);

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


    private void getAllItems() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("lat",strLat);
        map.put("lon",strLng);

        Call<SuccessResGetMyItems> call = micasaInterface.getAllProducts(map);

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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            if (requestCode == Constant.GPS_REQUEST) {
//                isGPS = true; // flag maintain before get location
//            }
        }
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

                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }


        }
    }


}