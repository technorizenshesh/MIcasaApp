package com.tech.micasa.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.tech.micasa.BuildConfig;
import com.tech.micasa.HomeActivity;
import com.tech.micasa.LoginActivity;
import com.tech.micasa.R;
import com.tech.micasa.SignUpActivity;
import com.tech.micasa.TurnOnGPSActivity;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentAddSaleItemBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResItemAdded;
import com.tech.micasa.retrofit.models.SuccessResSignIn;
import com.tech.micasa.retrofit.models.SuccessResSignUp;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.DecimalDigitsInputFilter;
import com.tech.micasa.utility.GPSTracker;
import com.tech.micasa.utility.NetworkAvailablity;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.isValidEmail;
import static com.tech.micasa.constants.Constant.showToast;


public class AddSaleItemFragment extends Fragment {

    public static final String TAG = "AddSaleItemFragment";

    private FragmentAddSaleItemBinding binding;
    ArrayAdapter ad;
    List<String> statusList = new LinkedList<>();
    public int spinnerPosition = 0;

    GPSTracker gpsTracker;

    MicasaInterface apiInterface;

    String strCategoryId ="",strSubCategoryId = "",strCategory ="",strSubCategory = "",strCondition = "",strTitle ="",strDescription ="",strAddress="",strPrice="",strLat="",strLon="";

    public static final int PICK_IMAGE = 1;
    String str_image_path="";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

    public AddSaleItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentAddSaleItemBinding.inflate(LayoutInflater.from(getContext()));

        apiInterface = ApiClient.getClient().create(MicasaInterface.class);
        gpsTracker = new GPSTracker(getActivity());
        statusList = Arrays.asList(getActivity().getResources().getStringArray(R.array.select_status));
        setSpinner();
        getLocation();
        binding.etPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)});

        Bundle bundle = this.getArguments();
        if(bundle!=null)
        {
            strCategory = bundle.getString("category");
            strSubCategory = bundle.getString("subCategory");

            strCategoryId = bundle.getString("categoryId");
            strSubCategoryId = bundle.getString("subCategoryId");


            binding.etCategory.setText(strCategory);
            binding.etSubCategory.setText(strSubCategory);
        }

        binding.etDescription.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.etDescription) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        //return inflater.inflate(R.layout.fragment_add_sale_item, container, false);
        binding.layoutItemaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layoutItemaction.tvHeader.setText(R.string.add_item);

        binding.btnSubmit.setOnClickListener(v ->
                {
                    addItem();
                }
                );

        binding.ivLogo.setOnClickListener(v ->
                {
                    if(checkPermisssionForReadStorage())
                        showImageSelection();
                }
                );
        return binding.getRoot();
    }

    private void setSpinner() {

        ad = new ArrayAdapter(
                getActivity().getApplicationContext(),
                R.layout.spinner_selected_item,
                statusList);
        ad.setDropDownViewResource(
                R.layout.spinner_selected_item);

        binding.spinnerStatus.setAdapter(ad);

        binding.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerPosition = position;
                Log.d(TAG, "onItemSelected: "+position);

              //  ((TextView) view).setTextColor(Color.RED);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void addItem() {
        strCondition = binding.spinnerStatus.getSelectedItem().toString();
        strTitle = binding.etTitle.getText().toString().trim();
        strDescription = binding.etDescription.getText().toString().trim();
        strPrice = binding.etPrice.getText().toString().trim();
        strAddress = binding.etAddress.getText().toString().trim();

        if(isValid())
        {
            if (NetworkAvailablity.getInstance(getActivity()).checkNetworkStatus()) {

                callAddItemApi();

            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
            }
        }else {
     //       Toast.makeText(getActivity(),  getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void showImageSelection() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);
        LinearLayout layoutGallary = (LinearLayout) dialog.findViewById(R.id.layoutGallary);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });
        layoutGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                getPhotoFromGallary();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void getPhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);
    }

    private void openCamera() {

        File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/Micasa/Images/");

        if (!dirtostoreFile.exists()) {
            dirtostoreFile.mkdirs();
        }

        String timestr = DataManager.getInstance().convertDateToString(Calendar.getInstance().getTimeInMillis());

        File tostoreFile = new File(Environment.getExternalStorageDirectory() + "/Micasa/Images/" + "IMG_" + timestr + ".jpg");

        str_image_path = tostoreFile.getPath();

        uriSavedImage = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                tostoreFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");
            if (requestCode == SELECT_FILE) {
                str_image_path = DataManager.getInstance().getRealPathFromURI(getActivity(), data.getData());
                Glide.with(getActivity())
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivLogo);

            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(getActivity())
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivLogo);
          }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)

            ) {

                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
               requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
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
                    strLon = Double.toString(gpsTracker.getLongitude()) ;

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

            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(getActivity(), " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
                break;
            }
        }
    }

    private void callAddItemApi() {
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"), strCategoryId);
        RequestBody subCategory = RequestBody.create(MediaType.parse("text/plain"),strSubCategoryId);
        RequestBody condition = RequestBody.create(MediaType.parse("text/plain"), strCondition);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"),strTitle);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), strDescription);
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), strPrice);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), strAddress);
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), strLat);
        RequestBody longitude = RequestBody.create(MediaType.parse("text/plain"), strLon);
        RequestBody registerID = RequestBody.create(MediaType.parse("text/plain"),userId);


        Call<SuccessResItemAdded> signupCall = apiInterface.addItem(registerID,category,subCategory,condition,title,
                description, price,address,latitude,longitude,filePart);
        signupCall.enqueue(new Callback<SuccessResItemAdded>() {
            @Override
            public void onResponse(Call<SuccessResItemAdded> call, Response<SuccessResItemAdded> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResItemAdded data = response.body();
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        showToast(getActivity(), data.message);
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        startActivity(new Intent(getActivity(), HomeActivity.class));
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
            public void onFailure(Call<SuccessResItemAdded> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });
    }


    private boolean isValid() {
         if (strPrice.equalsIgnoreCase("")) {
            binding.etPrice.setError(getString(R.string.ente_price));
            return false;
        }else if (spinnerPosition == 0) {
            Toast.makeText(getActivity(),getString(R.string.select_status),Toast.LENGTH_SHORT).show();
            return false;
        }else if (strAddress.equalsIgnoreCase("")) {
             binding.etAddress.setError(getString(R.string.enter_address));
             return false;
         } else if (strTitle.equalsIgnoreCase("")) {
            binding.etTitle.setError(getString(R.string.enter_title));
            return false;
        }else if (strDescription.equalsIgnoreCase("")) {
             binding.etDescription.setError(getString(R.string.enter_desc));
             return false;
         }else if (str_image_path.equalsIgnoreCase("")) {
            Toast.makeText(getActivity(),getString(R.string.please_upload_image),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.LOCATION_REQUEST);
        } else {
            Log.e("Latittude====",gpsTracker.getLatitude()+"");
            strLat = Double.toString(gpsTracker.getLatitude()) ;
            strLon = Double.toString(gpsTracker.getLongitude()) ;
        }
    }

}