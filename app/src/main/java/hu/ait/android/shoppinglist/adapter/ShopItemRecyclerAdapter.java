package hu.ait.android.shoppinglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import hu.ait.android.shoppinglist.MainActivity;
import hu.ait.android.shoppinglist.R;
import hu.ait.android.shoppinglist.data.ShopItem;

public class ShopItemRecyclerAdapter extends RecyclerView.Adapter<ShopItemRecyclerAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvItemName;
        TextView tvItemPrice;
        CheckBox cbBought;
        Button btnDelete;
        Button btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            cbBought = itemView.findViewById(R.id.cbBought);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    private int lastPosition = -1;

    private List<ShopItem> shopItemList;
    private Context context;

    public ShopItemRecyclerAdapter(List<ShopItem> shopItemList, Context context) {
        this.context = context;
        this.shopItemList = shopItemList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View shopItemRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.shop_item_row, parent, false);
        return new ViewHolder(shopItemRow);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ShopItem shopItem = shopItemList.get(position);

        viewHolder.tvItemName.setText(shopItem.getShopItemName());
        viewHolder.tvItemPrice.setText(shopItem.getEstimatedPrice());
        viewHolder.ivIcon.setImageResource(shopItem.getShopItemCategory().getIconId());

        viewHolder.cbBought.setChecked(shopItem.isBought());

        viewHolder.cbBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.cbBought.setChecked(!shopItem.isBought());
                toggleIsBoughtShopItem(viewHolder.getAdapterPosition());
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteShopItem(viewHolder.getAdapterPosition());
            }
        });

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).openEditShopItemActivity(
                        shopItemList.get(viewHolder.getAdapterPosition()).getShopItemID(),
                        viewHolder.getAdapterPosition()
                );
            }
        });

        setAnimation(viewHolder.itemView, position);
    }

    private void toggleIsBoughtShopItem(int shopItemToTogglePosition) {
        ((MainActivity) context).toggleIsBoughtOfShopItem(shopItemList.get(shopItemToTogglePosition));
    }

    public void addShopItem(ShopItem shopItem) {
        shopItemList.add(shopItem);
        notifyDataSetChanged();
    }

    public void updateShopItem(int positionOfShopItem, ShopItem shopItem) {
        shopItemList.set(positionOfShopItem, shopItem);
        notifyItemChanged(positionOfShopItem);
    }

    public void onShopItemMove(int fromAdapterPosition, int toAdapterPosition) {
        if (fromAdapterPosition < toAdapterPosition) {
            for (int i = fromAdapterPosition; i < toAdapterPosition; i++) {
                Collections.swap(shopItemList, i, i + 1);
            }
        } else {
            for (int i = fromAdapterPosition; i > toAdapterPosition; i--) {
                Collections.swap(shopItemList, i, i - 1);
            }
        }
        notifyItemMoved(fromAdapterPosition, toAdapterPosition);
    }

    public void deleteShopItem(int shopItemToDeletePosition) {
        ((MainActivity) context).deleteShopItem(shopItemToDeletePosition);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public List<ShopItem> getShopItemList() {
        return shopItemList;
    }

    @Override
    public int getItemCount() {
        return shopItemList.size();
    }


}
