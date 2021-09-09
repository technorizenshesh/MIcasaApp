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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.AccountPicker;
import com.google.gson.Gson;
import com.tech.micasa.BuildConfig;
import com.tech.micasa.R;
import com.tech.micasa.databinding.FragmentUpdateItemBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResProductDetail;
import com.tech.micasa.retrofit.models.SuccessResUpdateItem;
import com.tech.micasa.retrofit.models.SuccessResUpdateProfile;
import com.tech.micasa.utility.DataManager;
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

public class UpdateItemFragment extends Fragment {


    FragmentUpdateItemBinding binding;
    MicasaInterface micasaInterface;
    String strTitle ="",strPrice ="",strCondition ="",strAddress ="",strDescription = "",strLat = "",strLng = "";
    String str_image_path="";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;
    String productId;
    private SuccessResProductDetail.Result productDetail;

    ArrayAdapter ad;
    List<String> statusList = new LinkedList<>();
    public int spinnerPosition = 0;


    // TODO: Rename and change types of parameters

    public UpdateItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_update_item, container, false);
        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);
        statusList = Arrays.asList(getActivity().getResources().getStringArray(R.array.select_status));
        Bundle bundle = this.getArguments();
        setSpinner();
        toolbar();

        if(bundle!= null)
        {
            productId = bundle.getString("product_id");
        }
        getProduct();

        binding.ivUpload.setOnClickListener(v -> {
            if(checkPermisssionForReadStorage())
                showImageSelection();
        });

        binding.btnUpdate.setOnClickListener(v ->
                {

//                    strCondition = binding.etCondition.getText().toString().trim();
                    strCondition = binding.spinnerStatus.getSelectedItem().toString();
                    strTitle = binding.etTitle.getText().toString().trim();
                    strDescription = binding.etDescription.getText().toString().trim();
                    strPrice = binding.etPrice.getText().toString().trim();
                    strAddress = binding.etAddress.getText().toString().trim();

                    if (isValid()) {

                        if (NetworkAvailablity.getInstance(getActivity()).checkNetworkStatus()) {

                            updateItem();

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
              //          Toast.makeText(getActivity(),  getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
                    }
                }
                );

        return binding.getRoot();
    }

    private void toolbar() {

        binding.layoutItemaction.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layoutItemaction.tvHeader.setText(R.string.update_item);

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

                //  ((TextView) view).setTextColor(Color.RED);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private boolean isValid() {
        if (strPrice.equalsIgnoreCase("")) {
            binding.etPrice.setError("Please Enter Price.");
            return false;
        }else if (spinnerPosition == 0) {
            Toast.makeText(getActivity(),getString(R.string.select_status),Toast.LENGTH_SHORT).show();
            return false;
        }else if (strAddress.equalsIgnoreCase("")) {
            binding.etAddress.setError("Please Enter Address.");
            return false;
        } else if (strTitle.equalsIgnoreCase("")) {
            binding.etTitle.setError("Please Enter Title.");
            return false;
        }else if (strDescription.equalsIgnoreCase("")) {
            binding.etDescription.setError("Please Enter Description.");
            return false;
        }
        return true;
    }

    private void getProduct() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        Map<String,String> map = new HashMap<>();
        map.put("item_id",productId);

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

    public void setDetail()
    {

        Glide
                .with(getActivity())
                .load(productDetail.getImage())
                .centerCrop()
                .into(binding.ivLogo);

        binding.etAddress.setText(productDetail.getAddress());
        binding.etPrice.setText(productDetail.getPrice());
        int position = getIndex(binding.spinnerStatus, productDetail.getConditions());
        binding.spinnerStatus.setSelection(position);
        binding.etTitle.setText(productDetail.getTitle());
        binding.etDescription.setText(productDetail.getDescription());
        str_image_path = productDetail.getImage();

    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    public void updateItem()
    {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {

            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            if(file!=null)
            {
                filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            }
            else
            {
                filePart = null;
            }

//            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
//            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody itemId = RequestBody.create(MediaType.parse("text/plain"), productId);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"),strTitle);
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), strPrice);
        RequestBody conditions = RequestBody.create(MediaType.parse("text/plain"), strCondition);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), strAddress);

        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), strLat);
        RequestBody lng = RequestBody.create(MediaType.parse("text/plain"), strLng);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"),strDescription);

        Call<SuccessResUpdateItem> call = micasaInterface.updateItem(itemId,title,price,conditions,address,lat,lng,description,filePart);

        call.enqueue(new Callback<SuccessResUpdateItem>() {
            @Override
            public void onResponse(Call<SuccessResUpdateItem> call, Response<SuccessResUpdateItem> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResUpdateItem data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                    getProduct();

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
            public void onFailure(Call<SuccessResUpdateItem> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
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
            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied_boo), Toast.LENGTH_SHORT).show();
                }
                // return;
            }

        }
    }

}