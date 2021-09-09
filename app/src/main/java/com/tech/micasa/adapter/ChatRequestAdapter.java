package com.tech.micasa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tech.micasa.R;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.ChatRequestItemBinding;
import com.tech.micasa.databinding.NotificationItemBinding;
import com.tech.micasa.retrofit.models.SuccessResGetChatRequest;
import com.tech.micasa.retrofit.models.SuccessResGetNotifications;
import com.tech.micasa.utility.RequestStatusInterface;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ravindra Birla on 18,March,2021
 */
public class ChatRequestAdapter extends RecyclerView.Adapter<ChatRequestAdapter.NotificationViewHolder>{

    ChatRequestItemBinding binding;

    private RequestStatusInterface requestStatusInterface;
    private Context context;
    List<SuccessResGetChatRequest.Result> chatRequestList = new LinkedList<>();

    public ChatRequestAdapter(Context context, List<SuccessResGetChatRequest.Result> chatRequestList, RequestStatusInterface requestStatusInterface)
    {
        this.context = context;
        this.chatRequestList = chatRequestList;
        this.requestStatusInterface = requestStatusInterface;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.chat_request_item, parent, false);
        return new NotificationViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

          SuccessResGetChatRequest.ProductDetails productDetails = chatRequestList.get(position).getProductDetails();
          SuccessResGetChatRequest.UserDetails userDetail = chatRequestList.get(position).getUserDetails();

          binding.tvTitle.setText(userDetail.getFirstName());

          boolean isEnglish = SharedPreferenceUtility.getInstance(context).getBoolean(Constant.SELECTED_LANGUAGE);

          if(isEnglish)
          {
              binding.tvDescription.setText(chatRequestList.get(position).getMessageSp());

          }else
          {
              binding.tvDescription.setText(chatRequestList.get(position).getMessage());

          }

          binding.tvTime.setText(chatRequestList.get(position).getTimeAgo());
         Glide.with(context)
                .load(userDetail.getImage())
                .centerCrop()
                .into(binding.ivProfile);


        Glide.with(context)
                .load(productDetails.getImage())
                .centerCrop()
                .into(binding.ivProduct);
        binding.tvProductName.setText(productDetails.getTitle());

        binding.btnAccept.setOnClickListener(v ->
                {
                    requestStatusInterface.myCallback(chatRequestList.get(position).getId(),"Accepted",chatRequestList.get(position).getType(),chatRequestList.get(position).getOfferPrice());
                }
        );

        binding.btnDecline.setOnClickListener(v ->
                {
                    requestStatusInterface.myCallback(chatRequestList.get(position).getId(),"Rejected",chatRequestList.get(position).getType(),chatRequestList.get(position).getOfferPrice());
                }
        );

/*
        if(notificationsList.get(position).getType().equalsIgnoreCase("ChatRequest"))
        {
            if(notificationsList.get(position).getStatus().equalsIgnoreCase("2"))
            {
                binding.llAccNDec.setVisibility(View.VISIBLE);
            }

            if(notificationsList.get(position).getStatus().equalsIgnoreCase("1"))
            {
                binding.llAccNDec.setVisibility(View.GONE);
                binding.tvStatus.setText(context.getString(R.string.request_accepted));
                binding.tvStatus.setTextColor(context.getColor(R.color.green));
            }

            if(notificationsList.get(position).getStatus().equalsIgnoreCase("0"))
            {
                binding.llAccNDec.setVisibility(View.GONE);
                binding.tvStatus.setText(context.getString(R.string.request_declined));
                binding.tvStatus.setTextColor(context.getColor(R.color.red));
            }

            Glide.with(context)
                    .load(notificationsList.get(position).getUserImage())
                    .centerCrop()
                    .into(binding.ivProfile);
        }
        else
        {
            binding.llAccNDec.setVisibility(View.GONE);
        }
        binding.tvTime.setText(notificationsList.get(position).getTimeAgo());
        binding.tvDescription.setText(notificationsList.get(position).getMessage());

        binding.btnAccept.setOnClickListener(v ->
                {
                     requestStatusInterface.myCallback(notificationsList.get(position),"Accepted","1");
                }
                );

        binding.btnDecline.setOnClickListener(v ->
                {
                    requestStatusInterface.myCallback(notificationsList.get(position),"Rejected","0");
                }
        );*/
    }

    @Override
    public int getItemCount() {
        return chatRequestList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        public NotificationViewHolder(ChatRequestItemBinding notificationItemBinding) {
            super(notificationItemBinding.getRoot());
        }
    }


}
