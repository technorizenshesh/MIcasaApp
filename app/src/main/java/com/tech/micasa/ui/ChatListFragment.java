package com.tech.micasa.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.tech.micasa.R;
import com.tech.micasa.adapter.AdapterSellCat;
import com.tech.micasa.adapter.BuyerChatListAdapter;
import com.tech.micasa.adapter.ChatListAdapter;
import com.tech.micasa.adapter.ChatRequestAdapter;
import com.tech.micasa.adapter.ConversationListAdapter;
import com.tech.micasa.adapter.NotificationAdapter;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.FragmentChatListBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAvialableChatRequest;
import com.tech.micasa.retrofit.models.SuccessResGetChat;
import com.tech.micasa.retrofit.models.SuccessResGetChatRequest;
import com.tech.micasa.retrofit.models.SuccessResGetConversation;
import com.tech.micasa.retrofit.models.SuccessResInsertChat;
import com.tech.micasa.retrofit.models.SuccessResRequestStatus;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.RequestStatusInterface;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.setLocale;
import static com.tech.micasa.constants.Constant.showToast;

public class ChatListFragment extends Fragment implements RequestStatusInterface {

    private FragmentChatListBinding binding;

    MicasaInterface micasaInterface;
    List<SuccessResGetChatRequest.Result> chatRequestList = new LinkedList<>();

    List<SuccessResGetChatRequest.Result> sellerChatList = new LinkedList<>();

    List<SuccessResAvialableChatRequest.Result> buyerAvailableList = new LinkedList<>();
    BuyerChatListAdapter buyerChatListAdapter;


    List<SuccessResGetConversation.Result> conversationList = new LinkedList<>();
    ChatListAdapter sellerChatAdapter;
    ConversationListAdapter conversationListAdapter;

    ChatRequestAdapter chatRequestAdapter;

    public ChatListFragment() {
        // Required empty public constructor
    }

    NotificationAdapter notificationAdapter;



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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentChatListBinding.inflate(LayoutInflater.from(getContext()));
        micasaInterface = ApiClient.getClient().create(MicasaInterface.class);

        setToolbar();

        String myId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);

        sellerChatAdapter = new ChatListAdapter(getContext(),sellerChatList,myId);
        chatRequestAdapter = new ChatRequestAdapter(getContext(),chatRequestList,this);
        buyerChatListAdapter = new BuyerChatListAdapter(getContext(),buyerAvailableList,myId);
        conversationListAdapter = new ConversationListAdapter(getContext(),conversationList,myId);

//        chatBuyerAdapter = new ChatRequestAdapter(getContext(),chatRequestList,this);

        setAllRecyclerView();

        binding.rvChatEnquiry.setVisibility(View.VISIBLE);
        binding.rvSellerChat.setVisibility(View.GONE);
        binding.rvBuyerChat.setVisibility(View.GONE);
        getChatRequestStatus();


        //return inflater.inflate(R.layout.fragment_chat_list, container, false);


        binding.tabLayoutEventDay.addTab(binding.tabLayoutEventDay.newTab().setText(R.string.chat_request));
        binding.tabLayoutEventDay.addTab(binding.tabLayoutEventDay.newTab().setText(R.string.seller));
        binding.tabLayoutEventDay.addTab(binding.tabLayoutEventDay.newTab().setText(R.string.Buyer));
        binding.tabLayoutEventDay.addTab(binding.tabLayoutEventDay.newTab().setText(R.string.Chat));
        binding.tabLayoutEventDay.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayoutEventDay.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentTabSelected= tab.getPosition();

                if(currentTabSelected==0)
                {
                    binding.rvChatEnquiry.setVisibility(View.VISIBLE);
                    binding.rvSellerChat.setVisibility(View.GONE);
                    binding.rvBuyerChat.setVisibility(View.GONE);
                    binding.rvConversation.setVisibility(View.GONE);
                    getChatRequestStatus();
                    //Go for Today
                }else if(currentTabSelected==1)
                {

                    binding.rvChatEnquiry.setVisibility(View.GONE);
                    binding.rvSellerChat.setVisibility(View.VISIBLE);
                    binding.rvBuyerChat.setVisibility(View.GONE);
                    binding.rvConversation.setVisibility(View.GONE);
                    getSellerChat();

                } else if(currentTabSelected==2)
                {
                    binding.rvChatEnquiry.setVisibility(View.GONE);
                    binding.rvSellerChat.setVisibility(View.GONE);
                    binding.rvBuyerChat.setVisibility(View.VISIBLE);
                    binding.rvConversation.setVisibility(View.GONE);
                    //Go for past
                    getBuyerChat();

                }
                else if(currentTabSelected==3)
                {
                    binding.rvChatEnquiry.setVisibility(View.GONE);
                    binding.rvSellerChat.setVisibility(View.GONE);
                    binding.rvBuyerChat.setVisibility(View.GONE);
                    binding.rvConversation.setVisibility(View.VISIBLE);
                    //Go for past
                    getConversation();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        Bundle bundle = this.getArguments();
        if (bundle != null) {
           String Goto = bundle.getString("Goto");
           if(Goto.equalsIgnoreCase("3"))
           {
               binding.tabLayoutEventDay.getTabAt(2).select();
           }
        }

//        binding.tabLayoutEventDay.getTabAt(2).select();

        return binding.getRoot();
    }

    private void getBuyerChat() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        Map<String,String> map = new HashMap<>();
        map.put("user_id",userId);

        Call<SuccessResAvialableChatRequest> call = micasaInterface.getAvailableChatRequest(map);
        call.enqueue(new Callback<SuccessResAvialableChatRequest>() {
            @Override
            public void onResponse(Call<SuccessResAvialableChatRequest> call, Response<SuccessResAvialableChatRequest> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResAvialableChatRequest data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        // ChatListAdapter chatListAdapter = new ChatListAdapter()

                        buyerAvailableList.clear();
                        buyerAvailableList.addAll(data.getResult());
                        buyerChatListAdapter.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResAvialableChatRequest> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void setToolbar() {
        binding.layoutHeaderprofile.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.layoutHeaderprofile.tvHeader.setText(R.string.messages);

    }

    private void setAllRecyclerView() {

        //ForChatEnquiry
        binding.rvChatEnquiry.setHasFixedSize(true);
        binding.rvChatEnquiry.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChatEnquiry.setAdapter(chatRequestAdapter);

        //For SellerCHat
        binding.rvSellerChat.setHasFixedSize(true);
        binding.rvSellerChat.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvSellerChat.setAdapter(sellerChatAdapter);

        //For BuyerChat
        binding.rvBuyerChat.setHasFixedSize(true);
        binding.rvBuyerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBuyerChat.setAdapter(buyerChatListAdapter);

        //For Conversation
        binding.rvConversation.setHasFixedSize(true);
        binding.rvConversation.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvConversation.setAdapter(conversationListAdapter);

    }

    private void getSellerChat() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("seller_id",userId);
        map.put("status","Accepted");

        Call<SuccessResGetChatRequest> call = micasaInterface.getChatRequest(map);
        call.enqueue(new Callback<SuccessResGetChatRequest>() {
            @Override
            public void onResponse(Call<SuccessResGetChatRequest> call, Response<SuccessResGetChatRequest> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetChatRequest data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        sellerChatList.clear();
                        sellerChatList.addAll(data.getResult());
                        sellerChatAdapter.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResGetChatRequest> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    private void getChatRequestStatus() {
        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);
        Map<String,String> map = new HashMap<>();
        map.put("seller_id",userId);
        map.put("status","Pending");

        Call<SuccessResGetChatRequest> call = micasaInterface.getChatRequest(map);
        call.enqueue(new Callback<SuccessResGetChatRequest>() {
            @Override
            public void onResponse(Call<SuccessResGetChatRequest> call, Response<SuccessResGetChatRequest> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetChatRequest data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        chatRequestList.clear();
                        chatRequestList.addAll(data.getResult());
                        chatRequestAdapter.notifyDataSetChanged();

//                        SessionManager.writeString(RegisterAct.this, Constant.driver_id,data.result.id);
//                        App.showToast(RegisterAct.this, data.message, Toast.LENGTH_SHORT);

                    } else if (data.status.equals("0")) {
                        showToast(getActivity(), data.message);
                        chatRequestList.clear();
                        chatRequestAdapter.notifyDataSetChanged();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetChatRequest> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private void getConversation() {

        String userId = SharedPreferenceUtility.getInstance(getContext()).getString(USER_ID);

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("receiver_id",userId);

        Call<SuccessResGetConversation> call = micasaInterface.getConversation(map);
        call.enqueue(new Callback<SuccessResGetConversation>() {
            @Override
            public void onResponse(Call<SuccessResGetConversation> call, Response<SuccessResGetConversation> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResGetConversation data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        conversationList.clear();
                        conversationList.addAll(data.getResult());
                        conversationListAdapter.notifyDataSetChanged();

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
            public void onFailure(Call<SuccessResGetConversation> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void myCallback(String requestId, String status,String type,String price) {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String,String> map = new HashMap<>();
        map.put("request_id",requestId);
        map.put("status",status);
        map.put("type",type);
        map.put("offer_price",price);

        Call<SuccessResRequestStatus> call = micasaInterface.updateRequestStatus(map);
        call.enqueue(new Callback<SuccessResRequestStatus>() {
            @Override
            public void onResponse(Call<SuccessResRequestStatus> call, Response<SuccessResRequestStatus> response) {

                DataManager.getInstance().hideProgressMessage();
                try {
                    SuccessResRequestStatus data = response.body();
                    Log.e("data",data.status);
                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());

                        getChatRequestStatus();

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
            public void onFailure(Call<SuccessResRequestStatus> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }
}