 package com.tech.micasa.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tech.micasa.R;
import com.tech.micasa.databinding.ItemRecommandTwoBinding;
import com.tech.micasa.databinding.MyItemsBinding;
import com.tech.micasa.retrofit.models.SuccessResGetMyItems;
import com.tech.micasa.utility.MyCallback;
import com.tech.micasa.utility.RemoveItemInterface;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterMyItems extends RecyclerView.Adapter<AdapterMyItems.MyView> {


    private final Context context;
    private MyItemsBinding binding;
    MyCallback myCallback;
    private int status;
    private RemoveItemInterface removeItemInterface;


    List<SuccessResGetMyItems.Result> myItemsList;
    public AdapterMyItems(Context context, List<SuccessResGetMyItems.Result> myItemsList, int status, RemoveItemInterface removeItemInterface) {
        this.context = context;
        this.myItemsList = myItemsList;
        this.status = status;
        this.removeItemInterface = removeItemInterface;
    }

    @NotNull
    @Override
    public AdapterMyItems.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        binding= MyItemsBinding.inflate(LayoutInflater.from(context));
        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        return new AdapterMyItems.MyView(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull final AdapterMyItems.MyView holder, final int position) {

        Glide
                .with(context)
                .load(myItemsList.get(position).getImage())
                .centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.img_near_by));

        TextView tvTitle = holder.itemView.findViewById(R.id.tvCategoryName);
        tvTitle.setText(myItemsList.get(position).getTitle());

        binding.btnEdit.setOnClickListener(v ->
                {
                    String productID = myItemsList.get(position).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id",productID);
                    Navigation.findNavController(v).navigate(R.id.action_myItemsFragment_to_updateItemFragment,bundle);

                }
                );

        binding.ivDelete.setOnClickListener(v ->
                {

                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete_item)
                            .setMessage(R.string.sure_delete_item)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(context, "Yaay", Toast.LENGTH_SHORT).show();
                                    removeItemInterface.removeItem(myItemsList.get(position).getId());

                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                }
                );
    }

    @Override
    public int getItemCount() {
        return myItemsList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        public MyView(MyItemsBinding binding1w) {
            super(binding1w.getRoot());

            binding1w.getRoot().setOnClickListener(v -> {
                if(status ==1)
                {

                    String productID = myItemsList.get(getAdapterPosition()).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id",productID);
                    Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_detailFragment,bundle);

                }

            });
            //SubCat_image_view = (ImageView) view.findViewById(R.id.ite);

        }
    }

}


