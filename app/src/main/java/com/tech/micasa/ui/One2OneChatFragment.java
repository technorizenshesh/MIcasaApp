package com.tech.micasa.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tech.micasa.HomeActivity;
import com.tech.micasa.LoginOptionActivity;
import com.tech.micasa.R;
import com.tech.micasa.SplashActivity;
import com.tech.micasa.adapter.ChatAdapter;
import com.tech.micasa.databinding.FragmentOne2OneChatBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResGetChat;
import com.tech.micasa.retrofit.models.SuccessResGetNotifications;
import com.tech.micasa.retrofit.models.SuccessResInsertChat;
import com.tech.micasa.retrofit.models.SuccessResProfileData;
import com.tech.micasa.retrofit.models.SuccessResRequestStatus;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;


public class One2OneChatFragment extends Fragment {


    private FragmentOne2OneChatBinding binding;
    List<SuccessResGetChat.Result> chatList = new LinkedList<>();
    String userId,str_image_path="";

    ChatAdapter chatAdapter;
    Timer timer = new Timer();
    SuccessResProfileData.Result receiverDetail=null;

    MicasaInterface micasaInterface;
    public String receiverId ="",itemID = "",strChatMessage = "";
    public One2OneChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_one2_one_chat,container,false);
       // return inflater.inflate(R.layout.fragment_one2_one_chat, container, false);
        userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);
        setToolbar();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            receiverId = bundle.getString("seller_id");
            itemID = bundle.getString("item_id");
            getProfile(receiverId);
        }

        chatAdapter = new ChatAdapter(getContext(),chatList,userId);
        binding.recyclerViewChat.setHasFixedSize(true);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewChat.setAdapter(chatAdapter);

        getChat();



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(isLastVisible()){
                    getChat();
                }
            }
        },0,5000);

       // run();
        clickListener();
        return binding.getRoot();

    }

    private boolean isLastVisible() {

        if (chatList != null && chatList.size() != 0) {
            LinearLayoutManager layoutManager = ((LinearLayoutManager) binding.recyclerViewChat.getLayoutManager());
            int pos = layoutManager.findLastCompletelyVisibleItemPosition();
            int numItems = binding.recyclerViewChat.getAdapter().getItemCount();
            return (pos >= numItems - 1);
        }

        return false;

    }

    private void getProfile(String receiverId) {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("user_id",receiverId);

        Call<SuccessResProfileData> call = micasaInterface.getSellerDetails(map);

        call.enqueue(new Callback<SuccessResProfileData>() {
            @Override
            public void onResponse(Call<SuccessResProfileData> call, Response<SuccessResProfileData> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResProfileData data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        receiverDetail = data.getResult();


                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        setReceiverDetail();
                    } else if (data.status.equals("0")) {
                        showToast(getActivity(), data.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResProfileData> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });



    }

    private void setReceiverDetail() {

        Glide
                .with(getActivity())
                .load(receiverDetail.getImage())
                .centerCrop()
                .into(binding.ivReceiverProfile);
        binding.tvName.setText(receiverDetail.getFirstName());

    }


    private void clickListener() {

        binding.ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strChatMessage = binding.edtMessage.getText().toString();
                if(!strChatMessage.equals(""))
                {
                    insertChat();
                }

            }
        });

    }

    private void setToolbar() {
        binding.ivBack.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    private void getChat() {

        String sellerId = receiverId;
        String itemId = itemID;
        Map<String,String> map = new HashMap<>();
        map.put("sender_id",userId);
        map.put("receiver_id",sellerId);
        map.put("item_id",itemId);

        Call<SuccessResGetChat> call = micasaInterface.getChat(map);
        call.enqueue(new Callback<SuccessResGetChat>() {
            @Override
            public void onResponse(Call<SuccessResGetChat> call, Response<SuccessResGetChat> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetChat data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {

                        String dataResponse = new Gson().toJson(response.body());

                        chatList.clear();
                        chatList.addAll(data.getResult());
                        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.recyclerViewChat.setAdapter(chatAdapter);
                        //chatAdapter.notifyDataSetChanged();
                        binding.recyclerViewChat.scrollToPosition(chatList.size()-1);

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
            public void onFailure(Call<SuccessResGetChat> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    public void insertChat() {
        binding.edtMessage.setText("");
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        String sellerId = receiverId;


        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody senderId = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody receiverID = RequestBody.create(MediaType.parse("text/plain"),receiverId);
        RequestBody chatMessage = RequestBody.create(MediaType.parse("text/plain"), strChatMessage);
        RequestBody itemId = RequestBody.create(MediaType.parse("text/plain"),itemID);


/*
       DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("sender_id",userId);
        map.put("receiver_id",sellerId);
        map.put("item_id",itemId);
        map.put("chat_message",strChatMessage);
        map.put("chat_image","");
*/

        Call<SuccessResInsertChat> call = micasaInterface.insertChat(senderId,receiverID,chatMessage,itemId,filePart);
        call.enqueue(new Callback<SuccessResInsertChat>() {
            @Override
            public void onResponse(Call<SuccessResInsertChat> call, Response<SuccessResInsertChat> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResInsertChat data = response.body();
                    Log.e("data",data.status);
                    String dataResponse = new Gson().toJson(response.body());

                    getChat();
/*
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        getChat();

//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);

                    } else if (data.status.equals("0")) {

//                        showToast(getActivity(), data.message);

                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResInsertChat> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}