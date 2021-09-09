package com.tech.micasa.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tech.micasa.R;
import com.tech.micasa.databinding.ItemRecommandTwoBinding;
import com.tech.micasa.retrofit.ApiClient;
import com.tech.micasa.retrofit.MicasaInterface;
import com.tech.micasa.retrofit.models.SuccessResAddFavourite;
import com.tech.micasa.retrofit.models.SuccessResGetProductByCategory;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.SharedPreferenceUtility;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tech.micasa.constants.Constant.USER_ID;
import static com.tech.micasa.constants.Constant.showToast;

public class AdapterItemDetail extends RecyclerView.Adapter<AdapterItemDetail.MyView> {

    private final int[] strings;
    private final Context context;
    private ItemRecommandTwoBinding binding;
    MicasaInterface micasaInterface = ApiClient.getClient().create(MicasaInterface.class);

    private List<SuccessResGetProductByCategory.Result> productsList ;

    public AdapterItemDetail(int[] strings, Context context,List<SuccessResGetProductByCategory.Result> productsList) {
        this.strings = strings;
        this.context = context;
        this.productsList = productsList;
    }

    @NotNull
    @Override
    public AdapterItemDetail.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        binding= ItemRecommandTwoBinding.inflate(LayoutInflater.from(context));

        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);

        return new AdapterItemDetail.MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final AdapterItemDetail.MyView holder, final int position) {

        Glide
                .with(context)
                .load(productsList.get(position).getImage())
                .centerCrop()
                .into(binding.ivProduct);

        binding.ivShare.setOnClickListener(v ->
                {
                    String shareBody = "Product :"+productsList.get(position).getTitle()+"\n\n Product image :"+productsList.get(position).getImage()+"\n\n Price :"+productsList.get(position).getPrice();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(sharingIntent,context.getResources().getString(R.string.share_using)));
                }
        );

        ImageView ivLiked = holder.itemView.findViewById(R.id.ivLike);

        ivLiked.setOnClickListener(v ->
                {

                    if(productsList.get(position).getLiked().equalsIgnoreCase("0"))
                    {
                        ivLiked.setImageResource(R.drawable.ic_like);
                        productsList.get(position).setLiked("1");
                        setStatus("1",productsList.get(position).getId());
                    }
                    else {
                        ivLiked.setImageResource(R.drawable.ic_like1);
                        productsList.get(position).setLiked("0");
                        setStatus("0",productsList.get(position).getId());
                    }
                }
        );

        binding.tvTitle.setText(productsList.get(position).getTitle());
        binding.tvDaysAgo.setText(productsList.get(position).getTimeAgo());
        binding.tvPrice.setText(productsList.get(position).getPrice()+" "+productsList.get(position).getConditions());
        binding.tvLocation.setText(productsList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }


    public class MyView extends RecyclerView.ViewHolder {

        public MyView(ItemRecommandTwoBinding binding1w) {
            super(binding1w.getRoot());

            binding1w.getRoot().setOnClickListener(v -> {


                String productID = productsList.get(getAdapterPosition()).getId();

                Bundle bundle = new Bundle();
                bundle.putString("product_id",productID);
                Navigation.findNavController(v).navigate(R.id.action_itemDetailFragment_to_detailFragment,bundle);


            });
            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);

        }
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

}


