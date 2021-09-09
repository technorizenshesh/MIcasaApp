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
import com.tech.micasa.databinding.AdapterItemBinding;
import com.tech.micasa.retrofit.models.SuccessResAllCategories;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;



public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyView>{

    private final int[] strings;
    private final Context context;
    private AdapterItemBinding binding;
    List<SuccessResAllCategories.Category> categories;


    public ItemAdapter(int[] strings, Context context, List<SuccessResAllCategories.Category> categories) {
        this.strings = strings;
        this.context = context;
        this.categories = categories;
    }

    @NotNull
    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

         binding= AdapterItemBinding.inflate(LayoutInflater.from(context));

        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);

        return new MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyView holder, final int position) {

       TextView tvItem = holder.itemView.findViewById(R.id.tvItem);
        ImageView ivItem = holder.itemView.findViewById(R.id.ivItem);

        tvItem.setText(categories.get(position).getCategoryName());
        Glide.with(context)
                .load(categories.get(position).getImage())
                .fitCenter()
                .into(ivItem);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }



    public class MyView extends RecyclerView.ViewHolder {


        public MyView(AdapterItemBinding binding1w) {
            super(binding1w.getRoot());

            binding1w.getRoot().setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putString("CategoryID",categories.get(getAdapterPosition()).getId());
                bundle.putString("category",categories.get(getAdapterPosition()).getCategoryName());
                Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_itemDetailFragment,bundle);

            });

            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);

        }
    }

}
