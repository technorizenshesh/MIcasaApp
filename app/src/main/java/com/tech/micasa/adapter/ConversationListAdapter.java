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
import com.tech.micasa.retrofit.models.SuccessResGetChatRequest;
import com.tech.micasa.retrofit.models.SuccessResGetConversation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.MyView> {

    private final Context context;
    private AdapterChatlistBinding binding;

    List<SuccessResGetConversation.Result> conversationList ;
    public String myId;

    public ConversationListAdapter(Context context, List<SuccessResGetConversation.Result> conversationList  , String myId) {
        this.context = context;
        this.myId = myId;
        this.conversationList = conversationList;
    }

    @NotNull
    @Override
    public ConversationListAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_chatlist,parent,false);

        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);

        return new ConversationListAdapter.MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final ConversationListAdapter.MyView holder, final int position) {

        /*SuccessResGetChatRequest.ProductDetails productDetails = conversationList.get(position).getProductDetails();
        SuccessResGetChatRequest.UserDetails userDetail = conversationList.get(position).getUserDetails();*/
/*

        if(productDetails!=null)
        {
            binding.tvProductName.setText(productDetails.getTitle());
            binding.tvName.setText(userDetail.getFirstName());
            binding.tvDesc.setText(sellerChatList.get(position).getMessage());
            binding.tvTime.setText(sellerChatList.get(position).getDateTime());
            Glide.with(context)
                    .load(userDetail.getImage())
                    .centerCrop()
                    .into(binding.img1);
            Glide.with(context)
                    .load(productDetails.getImage())
                    .centerCrop()
                    .into(binding.ivProduct);
        }
*/


        Glide.with(context)
                .load(conversationList.get(position).getImage())
                .centerCrop()
                .into(binding.img1);
        Glide.with(context)
                .load(conversationList.get(position).getItemImage())
                .centerCrop()
                .into(binding.ivProduct);
        binding.tvProductName.setText(conversationList.get(position).getItemName());
        binding.tvName.setText(conversationList.get(position).getFirstName());
        binding.tvDesc.setText(conversationList.get(position).getLastMessage());
        binding.tvTime.setText(conversationList.get(position).getTimeAgo());
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public MyView(AdapterChatlistBinding binding1w) {
            super(binding1w.getRoot());

            binding1w.getRoot().setOnClickListener(v ->
                    {
/*

                        String itemId = sellerChatList.get(getAdapterPosition()).getProductDetails().getId();

                        String receiverId = sellerChatList.get(getAdapterPosition()).getUserDetails().getId();
                        Bundle bundle = new Bundle();
                        bundle.putString("key","0");
                        bundle.putString("seller_id",receiverId);
                        bundle.putString("item_id",itemId);
                        Navigation.findNavController(v).navigate(R.id.action_navigation_chatlist_to_one2OneChatFragment,bundle);
*/

                        String receiverId;

                        String itemId = conversationList.get(getAdapterPosition()).getItemId();

                        if(myId.equalsIgnoreCase(conversationList.get(getAdapterPosition()).getReceiverId()))
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
                    }
                    );

            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);  action_navigation_chatlist_to_one2OneChatFragment

        }
    }

}


