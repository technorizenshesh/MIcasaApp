package com.tech.micasa.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.databinding.FragmentDetailBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAddFavourite;
import com.tech.micasa.retrofit.models.SuccessResAddOfferItem;
import com.tech.micasa.retrofit.models.SuccessResChatRequest;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.retrofit.models.SuccessResGetProductByCategory;
import com.tech.micasa.retrofit.models.SuccessResProductDetail;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.MakeOfferCallbackInterface;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;


public class DetailFragment extends Fragment implements OnMapReadyCallback, MakeOfferCallbackInterface {

    GoogleMap gMap;
    FragmentDetailBinding binding;
    private String productId="",sellerId="",strItemId ="";

    public DetailFragment() {
        // Required empty public constructor
    }

    MicasaInterface micasaInterface;

    private SuccessResProductDetail.Result productDetail;
    String strLat = "",strLng = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false);
        //return inflater.inflate(R.layout.fragment_detail, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.ivBack.setOnClickListener(v ->
        {
            getActivity().onBackPressed();
        });

        Bundle bundle = this.getArguments();

        if(bundle!= null)
        {
            productId = bundle.getString("product_id");
        }

        micasaInterface  = ApiClient.getClient().create(MicasaInterface.class);

        getProduct();

        binding.btnChat.setOnClickListener(v -> {
            showRequestDialog();
      //      Navigation.findNavController(v).navigate(R.id.action_detailFragment_to_one2OneChatFragment);
        });

        binding.openProfileId.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("userID",productDetail.getUserId());
            Navigation.findNavController(v).navigate(R.id.action_detailFragment_to_produUserProfileFragment,bundle1);
        });

        binding.ivShare.setOnClickListener(v ->
                {
                    String shareBody = "Product :"+productDetail.getTitle()+"\n\n Product image :"+productDetail.getImage()+"\n\n Price :"+productDetail.getPrice();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent,"Share Using"));
                }
                );

        return binding.getRoot();
    }

    private void showRequestDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.chat_request_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        MaterialButton btnSend = dialog.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void sendRequest() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("seller_id",sellerId);
        map.put("item_id",productId);

        Call<SuccessResChatRequest> call = micasaInterface.chatRequest(map);
        call.enqueue(new Callback<SuccessResChatRequest>() {
            @Override
            public void onResponse(Call<SuccessResChatRequest> call, Response<SuccessResChatRequest> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResChatRequest data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        showToast(getActivity(), data.message);

                    } else if (data.status.equals("0")) {
                        showToast(getActivity(), data.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResChatRequest> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void getProduct() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        Map<String,String> map = new HashMap<>();
        map.put("item_id",productId);
        map.put("user_id",userId);

        Call<SuccessResProductDetail> call = micasaInterface.getProductDetail(map);

        call.enqueue(new Callback<SuccessResProductDetail>() {
            @Override
            public void onResponse(Call<SuccessResProductDetail> call, Response<SuccessResProductDetail> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResProductDetail data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        productDetail = data.getResult().get(0);

                        strLat = productDetail.getLat();
                        strLng = productDetail.getLon();

                        setDetail();

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
            public void onFailure(Call<SuccessResProductDetail> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

        private void setDetail() {

        String myId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);


         if(productDetail.getLiked().equalsIgnoreCase("0"))
        {

            binding.ivLike.setImageResource(R.drawable.ic_like1);

        }
        else
        {
            binding.ivLike.setImageResource(R.drawable.ic_like);
        }

        binding.tvLikes.setText(productDetail.getTotalLikes()+" "+getActivity().getString(R.string.like));

        binding.ivLike.setOnClickListener(v ->
                {

                    if(productDetail.getLiked().equalsIgnoreCase("0"))
                    {
                        binding.ivLike.setImageResource(R.drawable.ic_like);
                        productDetail.setLiked("1");
                        setStatus("1",productDetail.getId());
                    }
                    else {
                        binding.ivLike.setImageResource(R.drawable.ic_like1);
                        productDetail.setLiked("0");
                        setStatus("0",productDetail.getId());
                    }
                }
                );

        if(myId.equalsIgnoreCase(productDetail.getUserId()))
        {
            binding.btnChat.setVisibility(View.GONE);
            binding.btnMakeoffer.setVisibility(View.GONE);
        }

        binding.btnMakeoffer.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment= new BottomSheetFragment(productDetail.getPrice(),this);
            bottomSheetFragment.show(getChildFragmentManager(),"ModalBottomSheet");
        });


        Glide
                .with(getActivity())
                .load(productDetail.getImage())
                .centerCrop()
                .into(binding.ivProduct);

        binding.tvTitle.setText(productDetail.getTitle());
        binding.tvPrice.setText(productDetail.getPrice());
        binding.tvCondition.setText(productDetail.getConditions());
        binding.tvDescription.setText(productDetail.getDescription());
        binding.tvAddress.setText(productDetail.getAddress());
        binding.tvUploadedOn.setText(productDetail.getTimeAgo());

        Glide
                .with(getActivity())
                .load(productDetail.getUserDetails().getImage())
                .centerCrop()
                .placeholder(R.color.colorGray)
                .into(binding.ivSeller);
        binding.tvSellerName.setText((productDetail.getUserDetails().getFirstName()));
        sellerId = productDetail.getUserId();
        strItemId = productDetail.getId();
      setMarker();
    }

    public void setMarker()

    {

        double  lat = Double.parseDouble(strLat);
        double  lng = Double.parseDouble(strLng);
        LatLng sydney = new LatLng(lat, lng);
        gMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker"));
     //   gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();

        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        // Add a marker in Sydney and move the camera
    }

    @Override
    public void myCallback(String price) {
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();

        map.put("user_id",userId);
        map.put("seller_id",sellerId);
        map.put("item_id",strItemId);
        map.put("offer_price",price);

        Call<SuccessResAddOfferItem> call = micasaInterface.addItemOffer(map);

        call.enqueue(new Callback<SuccessResAddOfferItem>() {
            @Override
            public void onResponse(Call<SuccessResAddOfferItem> call, Response<SuccessResAddOfferItem> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAddOfferItem data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                    //    String dataResponse = new Gson().toJson(response.body());
                        showToast(getActivity(), data.message);
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
            public void onFailure(Call<SuccessResAddOfferItem> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void setStatus(String status,String productId) {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);

        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("item_id",productId);
        map.put("liked",status);

        Call<SuccessResAddFavourite> call = micasaInterface.addFavorite(map);

        call.enqueue(new Callback<SuccessResAddFavourite>() {
            @Override
            public void onResponse(Call<SuccessResAddFavourite> call, Response<SuccessResAddFavourite> response) {

                try {
                    SuccessResAddFavourite data = response.body();
                    Log.e("data",data.status);

                    if (data.status.equals("1")) {
                        //       showToast((Activity) context, data.getResult());

                    } else if (data.status.equals("0")) {
                        showToast((Activity) getContext(), data.result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResAddFavourite> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

}