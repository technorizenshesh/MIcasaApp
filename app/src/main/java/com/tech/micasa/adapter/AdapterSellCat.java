package com.tech.micasa.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.micasa.R;
import com.tech.micasa.databinding.AdapterSellCategoryBinding;
import com.tech.micasa.databinding.ItemTopListingBinding;
import com.tech.micasa.retrofit.models.SuccessResGetAllSubCategories;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class AdapterSellCat  extends RecyclerView.Adapter<AdapterSellCat.MyView> {

    private final int[] strings;
    private final Context context;
    private AdapterSellCategoryBinding binding;
    public String subCategory,category;
    List<SuccessResGetAllSubCategories.SubCategory> subCategoryList = new LinkedList<>();

    public AdapterSellCat(int[] strings, Context context,List<SuccessResGetAllSubCategories.SubCategory> subCategoryList,String category) {
        this.strings = strings;
        this.context = context;
        this.category = category;
        this.subCategoryList = subCategoryList;
    }

    @NotNull
    @Override
    public AdapterSellCat.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        binding= AdapterSellCategoryBinding.inflate(LayoutInflater.from(context));
        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        return new AdapterSellCat.MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final AdapterSellCat.MyView holder, final int position) {

      TextView tvName = holder.itemView.findViewById(R.id.daily_txtId);
      tvName.setText(subCategoryList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public MyView(AdapterSellCategoryBinding binding1w) {
            super(binding1w.getRoot());

                binding1w.getRoot().setOnClickListener(v -> {

                subCategory = subCategoryList.get(getAdapterPosition()).getName();

                Bundle bundle = new Bundle();
                bundle.putString("category",category);
                bundle.putString("subCategory",subCategory);
                bundle.putString("categoryId",subCategoryList.get(getAdapterPosition()).getCategoryId());
                bundle.putString("subCategoryId",subCategoryList.get(getAdapterPosition()).getId());

                Navigation.findNavController(v).navigate(R.id.action_saleCategoryFragment_to_addSaleItemFragment2,bundle);

            });

            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);

        }
    }

}



