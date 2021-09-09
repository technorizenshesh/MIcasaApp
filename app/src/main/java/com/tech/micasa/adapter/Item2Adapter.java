package com.tech.micasa.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.tech.micasa.R;
import com.tech.micasa.databinding.AdapterItemBinding;
import com.tech.micasa.databinding.ItemRecommandTwoBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAddFavourite;
import com.tech.micasa.retrofit.models.SuccessResDeleteItem;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.SharedPreferenceUtility;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;


public class Item2Adapter extends RecyclerView.Adapter<Item2Adapter.MyView>{

    private final Context context;
    private ItemRecommandTwoBinding binding;
    List<SuccessResGetMyItems.Result> myItemsList;
    private int status;
    MicasaInterface micasaInterface = ApiClient.getClient().create(MicasaInterface.class);

    public Item2Adapter(Context context, List<SuccessResGetMyItems.Result> myItemsList,int status) {
        this.context = context;
        this.myItemsList = myItemsList;
        this.status = status;
    }

    @NotNull
    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        binding= ItemRecommandTwoBinding.inflate(LayoutInflater.from(context));
        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        return new MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyView holder, final int position) {

        Glide
                .with(context)
                .load(myItemsList.get(position).getImage())
                .centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.ivProduct));
        TextView tvTitle = holder.itemView.findViewById(R.id.tvTitle);
        tvTitle.setText(myItemsList.get(position).getTitle());

        ImageView ivLiked = holder.itemView.findViewById(R.id.ivLike);

        if(myItemsList.get(position).getLiked().equalsIgnoreCase("0"))
        {

            ivLiked.setImageResource(R.drawable.ic_like1);

        }
        else
        {
            ivLiked.setImageResource(R.drawable.ic_like);
        }


        ivLiked.setOnClickListener(v ->
                {

                    String status = myItemsList.get(position).getLiked();

                    if(myItemsList.get(position).getLiked().equalsIgnoreCase("0"))
                    {
                        ivLiked.setImageResource(R.drawable.ic_like);
                        myItemsList.get(position).setLiked("1");
                        setStatus("1",myItemsList.get(position).getId());
                    }
                    else {
                        ivLiked.setImageResource(R.drawable.ic_like1);
                        myItemsList.get(position).setLiked("0");
                        setStatus("0",myItemsList.get(position).getId());
                    }
                }
        );

        binding.ivShare.setOnClickListener(v ->
                {
                    String shareBody = "Product :"+myItemsList.get(position).getTitle()+"\n\n Product image :"+myItemsList.get(position).getImage()+"\n\n Price :"+myItemsList.get(position).getPrice();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(sharingIntent,context.getResources().getString(R.string.share_using)));
                }
        );

        binding.tvDaysAgo.setText(myItemsList.get(position).getTimeAgo());
        binding.tvPrice.setText((myItemsList.get(position).getPrice()+" "+myItemsList.get(position).getConditions()));
    }

    private void setStatus(String status,String productId) {

        String userId = SharedPreferenceUtility.getInstance(context).getString(USER_ID);

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
                        showToast((Activity) context, data.result);
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

    @Override
    public int getItemCount() {
        return myItemsList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public MyView(ItemRecommandTwoBinding binding1w) {
            super(binding1w.getRoot());

            binding1w.getRoot().setOnClickListener(v -> {

                if(status ==1)
                {

                    String productID = myItemsList.get(getAdapterPosition()).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id",productID);
                    Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_detailFragment,bundle);

                }  else if(status ==2)
                {

                    String productID = myItemsList.get(getAdapterPosition()).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id",productID);
                    Navigation.findNavController(v).navigate(R.id.action_produUserProfileFragment_to_detailFragment,bundle);

                }else if(status ==3)
                {
                    String productID = myItemsList.get(getAdapterPosition()).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id",productID);
                    Navigation.findNavController(v).navigate(R.id.action_searchItemFragment_to_detailFragment,bundle);
                }

                //         action_prodUserListFragment_to_detailFragment

                // action_produUserProfileFragment_to_detailFragment


            });
            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);

        }
    }

}
