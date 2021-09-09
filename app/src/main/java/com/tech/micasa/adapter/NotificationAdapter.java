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
import com.tech.micasa.databinding.NotificationItemBinding;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResGetNotifications;
import com.tech.micasa.utility.RequestStatusInterface;

import java.util.List;

/**
 * Created by Ravindra Birla on 18,March,2021
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    NotificationItemBinding binding;

    private RequestStatusInterface requestStatusInterface;
    private Context context;
    public List<SuccessResGetNotifications.Result> notificationsList;

    public NotificationAdapter(Context context,List<SuccessResGetNotifications.Result> notificationsList, RequestStatusInterface requestStatusInterface)

    {
        this.context = context;
        this.notificationsList = notificationsList;
        this.requestStatusInterface = requestStatusInterface;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.notification_item, parent, false);
        return new NotificationViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
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
        return notificationsList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        public NotificationViewHolder(NotificationItemBinding notificationItemBinding) {
            super(notificationItemBinding.getRoot());
        }
    }


}
