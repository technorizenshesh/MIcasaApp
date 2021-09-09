package com.tech.micasa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.micasa.R;
import com.tech.micasa.databinding.ItemRecommandTwoBinding;
import com.tech.micasa.databinding.ItemTopListingBinding;

import org.jetbrains.annotations.NotNull;


public class Item3Adapter extends RecyclerView.Adapter<Item3Adapter.MyView>{

    private final int[] strings;
    private final Context context;
    private ItemTopListingBinding binding;


    public Item3Adapter(int[] strings, Context context) {
        this.strings = strings;
        this.context = context;

    }

    @NotNull
    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

         binding= ItemTopListingBinding.inflate(LayoutInflater.from(context));
        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);

        return new MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyView holder, final int position) {



    }

    @Override
    public int getItemCount() {
        return 5;
    }


    static class MyView extends RecyclerView.ViewHolder {


        public MyView(ItemTopListingBinding binding1w) {
            super(binding1w.getRoot());

            binding1w.getRoot().setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_itemAllFragment_to_itemDetailFragment);

            });
            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);

        }
    }

}
