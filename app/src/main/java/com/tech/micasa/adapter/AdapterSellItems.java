package com.tech.micasa.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tech.micasa.R;
import com.tech.micasa.databinding.ItemTopListingBinding;
import com.tech.micasa.retrofit.models.SuccessResAllCategories;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class AdapterSellItems extends RecyclerView.Adapter<AdapterSellItems.MyView>{

    private final int[] strings;
    private final Context context;
    private com.tech.micasa.databinding.CategoryItemBinding binding;
    public List<SuccessResAllCategories.Category> categories;
    public boolean comeFromHome;

    public AdapterSellItems(int[] strings, Context context,List<SuccessResAllCategories.Category> categories,boolean comeFromHome) {
        this.strings = strings;
        this.context = context;
        this.categories = categories;
        this.comeFromHome = comeFromHome;
    }

    @NotNull
    @Override
    public AdapterSellItems.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        binding= com.tech.micasa.databinding.CategoryItemBinding.inflate(LayoutInflater.from(context));
        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);

        return new AdapterSellItems.MyView(binding);

    }

    @Override
    public void onBindViewHolder(@NotNull final AdapterSellItems.MyView holder, final int position) {

        Glide
                .with(context)
                .load(categories.get(position).getImage())
                .centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.ivCategory));

      TextView category = holder.itemView.findViewById(R.id.tvCategoryName);
      category.setText(categories.get(position).getCategoryName());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public MyView(com.tech.micasa.databinding.CategoryItemBinding binding1w) {
            super(binding1w.getRoot());

            if(comeFromHome)
            {
                binding1w.getRoot().setOnClickListener(v -> {

                    Bundle bundle = new Bundle();
                    bundle.putString("CategoryID",categories.get(getAdapterPosition()).getId());
                    bundle.putString("category",categories.get(getAdapterPosition()).getCategoryName());
                    Navigation.findNavController(v).navigate(R.id.action_itemAllFragment_to_itemDetailFragment,bundle);

                });
            }
            else
            {

                binding1w.getRoot().setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("CategoryID",categories.get(getAdapterPosition()).getId());
                    bundle.putString("category",categories.get(getAdapterPosition()).getCategoryName());
                    Navigation.findNavController(v).navigate(R.id.action_navigation_add_to_saleCategoryFragment,bundle);
                });
            }

            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);

        }
    }

}


