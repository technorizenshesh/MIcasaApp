package com.tech.micasa.ui;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tech.micasa.HomeActivity;
import com.tech.micasa.R;
import com.tech.micasa.SignUpActivity;
import com.tech.micasa.adapter.AdapterMyItems;
import com.tech.micasa.adapter.AdapterSellItems;
import com.tech.micasa.adapter.Item2Adapter;
import com.tech.micasa.adapter.ItemAdapter;
import com.tech.micasa.adapter.SliderAdapter;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentHomeBinding;
import com.tech.micasa.model.SliderItem;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAllCategories;
import com.tech.micasa.retrofit.models.SuccessResBannersList;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.GPSTracker;
import com.tech.micasa.utility.GpsUtils;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.tech.micasa.constants.Constant.SELLER_ID;
import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.setLocale;
import static com.tech.micasa.constants.Constant.showToast;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    List<SliderItem> sliderItems;
    private SliderAdapter adapter;
    public ItemAdapter itemAdapter;

    MicasaInterface micasaInterface;
    List<SuccessResBannersList.Result> bannersList = new LinkedList<>();
    List<SuccessResAllCategories.Category> categories = new LinkedList<>();

    List<SuccessResGetMyItems.Result> myItemsList = new LinkedList<>();

    Item2Adapter adapterMyItems;

    public static String TAG ="HomeFragment";


//    private boolean isGPS = false;
//    private boolean isContinue = false;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;

    GPSTracker gpsTracker;

    int[] strings={R.drawable.horse,R.drawable.chairs,R.drawable.horse};

    String strLat= "", strLng="";


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
          gpsTracker = new GPSTracker(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700, this.getActivity().getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }
        binding= FragmentHomeBinding.inflate(inflater, container, false);

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
//        new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
//            @Override
//            public void gpsStatus(boolean isGPSEnable) {
//                // turn on GPS
//                isGPS = isGPSEnable;
//            }
//        });
//
//        if (!isGPS) {
//            Toast.makeText(getActivity(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
//
//        }
//        isContinue = false;

        getLocation();

        Log.d(TAG, "onCreate: "+strLat+strLng);

        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);
        adapterMyItems = new Item2Adapter(getActivity(),myItemsList,1);

        itemAdapter = new ItemAdapter(strings,getContext(),categories);

        getBannerList();
        getCategories();

       // View root = inflater.inflate(R.layout.fragment_home, container, false);
        sliderItems=new ArrayList<>();

        sliderItems.add(new SliderItem("","","https://images.indianexpress.com/2019/09/teacher-day_1.jpg",""));
        sliderItems.add(new SliderItem("","","https://images.indianexpress.com/2019/09/teacher-day_1.jpg",""));
        sliderItems.add(new SliderItem("","","https://images.indianexpress.com/2019/09/teacher-day_1.jpg",""));

        adapter = new SliderAdapter(getContext(),bannersList );
        getAllItems();

        binding.imageSlider.setSliderAdapter(adapter);
        adapter.notifyDataSetChanged();
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(5); //set scroll delay in seconds :
        binding.imageSlider.startAutoCycle();

        binding.recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, true));
        binding.recyclerView1.setAdapter(itemAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        binding.recyclerView2.setLayoutManager(gridLayoutManager);
        binding.recyclerView2.setAdapter(adapterMyItems);
/*

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 2);
        binding.recyclerView3.setLayoutManager(gridLayoutManager1);
        binding.recyclerView3.setAdapter(new Item2Adapter(strings,getContext()));
*/

        binding.viewAll.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_itemAllFragment);
        });
        binding.btnView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_itemAllFragment);
        });

        binding.searchView.setOnClickListener(v ->
                {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("key","1");
                    bundle1.putString("categoryId","");
                    Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_searchItemFragment,bundle1);
                }
                );


        AdView adView = new AdView(getActivity());

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

      /*  MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });*/

       SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

      /*  binding.searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        binding.searchView.setMaxWidth(Integer.MAX_VALUE);
*/

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); //

            binding.tvLocation.setText(city+", "+state);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }

    private void getBannerList() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));

        Call<SuccessResBannersList> call = micasaInterface.getBanners();

        call.enqueue(new Callback<SuccessResBannersList>() {
            @Override
            public void onResponse(Call<SuccessResBannersList> call, Response<SuccessResBannersList> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResBannersList data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        bannersList.clear();

                        bannersList.addAll(data.getResult());
                        adapter.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResBannersList> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    private void getCategories() {
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        Call<SuccessResAllCategories> call = micasaInterface.getAllCategories(map);

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
                        itemAdapter.notifyDataSetChanged();

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
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.permisson_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            }


        }
    }




}