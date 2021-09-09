package com.tech.micasa.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tech.micasa.R;
import com.tech.micasa.databinding.AdapterChatlistBinding;
import com.tech.micasa.retrofit.models.SuccessResAvialableChatRequest;
import com.tech.micasa.retrofit.models.SuccessResGetChatRequest;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuyerChatListAdapter extends RecyclerView.Adapter<BuyerChatListAdapter.MyView> {


    private final Context context;
    private AdapterChatlistBinding binding;

    List<SuccessResAvialableChatRequest.Result> buyerChatList ;
    public String myId;

    public BuyerChatListAdapter(Context context, List<SuccessResAvialableChatRequest.Result> buyerChatList  , String myId) {
        this.context = context;
        this.myId = myId;
        this.buyerChatList = buyerChatList;
    }

    @NotNull
    @Override
    public BuyerChatListAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_chatlist,parent,false);
        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        return new BuyerChatListAdapter.MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final BuyerChatListAdapter.MyView holder, final int position) {

        SuccessResAvialableChatRequest.SellerDetails sellerDetails = buyerChatList.get(position).getSellerDetails();
        SuccessResAvialableChatRequest.ProductDetails productDetails = buyerChatList.get(position).getProductDetails();

        if(productDetails!=null)
        {
            binding.tvProductName.setText(productDetails.getTitle());
            binding.tvName.setText(sellerDetails.getFirstName());

            if(buyerChatList.get(position).getType().equalsIgnoreCase("Offer"))
            {
                String text = context.getString(R.string.yor)+" "+buyerChatList.get(position).getOfferPrice()+" "+context.getString(R.string.is_accept);
                binding.tvDesc.setText(text);
            }
            else
            {
                binding.tvDesc.setText(R.string.chat_request_accepted);
            }

     //       binding.tvDesc.setText(buyerChatList.get(position).getMessage());

            binding.tvTime.setText(buyerChatList.get(position).getTimeAgo());
            Glide.with(context)
                    .load(sellerDetails.getImage())
                    .centerCrop()
                    .into(binding.img1);
            Glide.with(context)
                    .load(productDetails.getImage())
                    .centerCrop()
                    .into(binding.ivProduct);
        }


        /*
        Glide.with(context)
                .load(conversationList.get(position).getImage())
                .centerCrop()
                .into(binding.img1);
        Glide.with(context)
                .load(conversationList.get(position).getItemImage())
                .centerCrop()
                .into(binding.ivProduct);
        binding.tvProductName.setText(conversationList.get(position).getFirstName());
        binding.tvName.setText(conversationList.get(position).getFirstName());
        binding.tvDesc.setText(conversationList.get(position).getLastMessage());
        binding.tvTime.setText(conversationList.get(position).getTimeAgo());
*/
    }

    @Override
    public int getItemCount() {
        return buyerChatList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public MyView(AdapterChatlistBinding binding1w) {
            super(binding1w.getRoot());

            binding1w.getRoot().setOnClickListener(v ->
                    {
                        String itemId = buyerChatList.get(getAdapterPosition()).getProductDetails().getId();
                        String receiverId = buyerChatList.get(getAdapterPosition()).getSellerId();
                        Bundle bundle = new Bundle();
                        bundle.putString("key","0");
                        bundle.putString("seller_id",receiverId);
                        bundle.putString("item_id",itemId);
                        Navigation.findNavController(v).navigate(R.id.action_navigation_chatlist_to_one2OneChatFragment,bundle);


/*
                        String itemId = buyerChatList.get(getAdapterPosition()).getItemId();

                        String senderID,receiverId;*/
/*
                        if(myId.equalsIgnoreCase(sellerChatList.get(getAdapterPosition()).getReceiverId()))
                        {

                            receiverId = conversationList.get(getAdapterPosition()).getSenderId();

                        }
                        else
                        {
                            receiverId = conversationList.get(getAdapterPosition()).getReceiverId();
                        }


                        Bundle bundle = new Bundle();
                        bundle.putString("key","0");
                        bundle.putString("seller_id",receiverId);
                        bundle.putString("item_id",itemId);
                        Navigation.findNavController(v).navigate(R.id.action_navigation_chatlist_to_one2OneChatFragment,bundle);
*/
                    }
                    );

            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);  action_navigation_chatlist_to_one2OneChatFragment

        }
    }

}


