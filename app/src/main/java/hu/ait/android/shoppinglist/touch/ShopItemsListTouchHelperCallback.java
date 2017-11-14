package hu.ait.android.shoppinglist.touch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import hu.ait.android.shoppinglist.adapter.ShopItemRecyclerAdapter;

public class ShopItemsListTouchHelperCallback extends ItemTouchHelper.Callback {
    private ShopItemRecyclerAdapter shopItemRecyclerAdapter;

    public ShopItemsListTouchHelperCallback(ShopItemRecyclerAdapter shopItemRecyclerAdapter) {
        this.shopItemRecyclerAdapter = shopItemRecyclerAdapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        shopItemRecyclerAdapter.onShopItemMove(
                viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        shopItemRecyclerAdapter.deleteShopItem(viewHolder.getAdapterPosition());
    }
}
