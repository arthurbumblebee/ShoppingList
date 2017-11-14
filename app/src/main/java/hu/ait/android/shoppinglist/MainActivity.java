package hu.ait.android.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.ait.android.shoppinglist.adapter.ShopItemRecyclerAdapter;
import hu.ait.android.shoppinglist.data.ShopItem;
import hu.ait.android.shoppinglist.touch.ShopItemsListTouchHelperCallback;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_NEW_SHOP_ITEM = 1001;
    public static final int REQUEST_EDIT_SHOP_ITEM = 102;
    public static final String KEY_EDIT = "KEY_EDIT";
    public static final String KEY_FIRST = "KEY_FIRST";
    public static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";
    public static final String TOTAL_OF_PRICES = "TOTAL_OF_PRICES";
    public static final String TOTAL_BOUGHT = "TOTAL_BOUGHT";
    public static final String TOTAL_NOT_BOUGHT = "TOTAL_NOT_BOUGHT";
    private ShopItemRecyclerAdapter shopItemRecyclerAdapter;
    private CoordinatorLayout layoutContent;
    private int positionOfShopItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolBar();

        ((ShoppingApplication) getApplication()).openRealm();

        RealmResults<ShopItem> allShopItems = getRealm().where(ShopItem.class).findAll();
        ShopItem shopItemsArray[] = new ShopItem[allShopItems.size()];
        List<ShopItem> shopItemsResult = new ArrayList<>(Arrays.asList(allShopItems.toArray(shopItemsArray)));

        shopItemRecyclerAdapter = new ShopItemRecyclerAdapter(shopItemsResult, this);
        RecyclerView recyclerViewShopItems = findViewById(R.id.recyclerViewShopItems);
        recyclerViewShopItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewShopItems.setAdapter(shopItemRecyclerAdapter);

        ShopItemsListTouchHelperCallback shopItemsListTouchHelperCallback = new ShopItemsListTouchHelperCallback(
                shopItemRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                shopItemsListTouchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewShopItems);

        layoutContent = findViewById(R.id.layoutContent);

        FloatingActionButton floatingActionButton = findViewById(R.id.btnAdd);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateShopItemActivity();
            }
        });

        if (isFirstStart()) {
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(findViewById(R.id.btnAdd))
                    .setPrimaryText(R.string.a_new_shop_item)
                    .setSecondaryText(R.string.create_new_item)
                    .show();
        }

    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(R.drawable.cart_icon);
        }
    }

    private boolean isFirstStart() {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(KEY_FIRST, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        saveFirstStartFlag();
    }

    private void saveFirstStartFlag() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(KEY_FIRST, false);
        edit.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public Realm getRealm() {
        return ((ShoppingApplication) getApplication()).getRealmShopItems();
    }


    private void showCreateShopItemActivity() {
        Intent intentStart = new Intent(MainActivity.this,
                CreateShopItem.class);
        startActivityForResult(intentStart, REQUEST_NEW_SHOP_ITEM);
    }

    public void openEditShopItemActivity(String shopItemID, int adapterPosition) {
        Intent intentStart = new Intent(MainActivity.this,
                CreateShopItem.class);
        positionOfShopItem = adapterPosition;

        intentStart.putExtra(KEY_EDIT, shopItemID);
        startActivityForResult(intentStart, REQUEST_EDIT_SHOP_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String shopItemID = data.getStringExtra(
                        CreateShopItem.KEY_SHOP_ITEM);

                ShopItem shopItem = getRealm().where(ShopItem.class)
                        .equalTo("shopItemID", shopItemID)
                        .findFirst();

                if (requestCode == REQUEST_NEW_SHOP_ITEM) {
                    shopItemRecyclerAdapter.addShopItem(shopItem);
                    showSnackBarMessage(getString(R.string.you_added_an_item));

                } else if (requestCode == REQUEST_EDIT_SHOP_ITEM) {
                    shopItemRecyclerAdapter.updateShopItem(positionOfShopItem, shopItem);
                    showSnackBarMessage(getString(R.string.item_edited));
                }
                break;
            case RESULT_CANCELED:
                showSnackBarMessage(getString(R.string.add_cancelled));
                break;
        }
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                deleteAll();
                return true;
            case R.id.action_add:
                showCreateShopItemActivity();
                return true;
            case R.id.action_summary:
                showBudget();
                return true;
        }
        return false;
    }

    private void showBudget() {
        Intent intentResult = new Intent();
        intentResult.setClass(MainActivity.this, SummaryActivity.class);

        int numberOfItems = Integer.valueOf(computeTotalNumberOfItems());
        int totalOfPrices = Integer.valueOf(computeTotalPrice());
        int totalBought = Integer.valueOf(computePriceOfBought());
        int totalNotBought = totalOfPrices - totalBought;

        intentResult.putExtra(NUMBER_OF_ITEMS, numberOfItems);
        intentResult.putExtra(TOTAL_OF_PRICES, totalOfPrices);
        intentResult.putExtra(TOTAL_BOUGHT, totalBought);
        intentResult.putExtra(TOTAL_NOT_BOUGHT, totalNotBought);

        startActivity(intentResult);
    }

    private String computeTotalNumberOfItems() {
        int totalNumberOfItems = shopItemRecyclerAdapter.getItemCount();
        return Integer.toString(totalNumberOfItems);
    }

    private String computeTotalPrice() {
        int totalPrice = 0;

        List<ShopItem> shopItemList = shopItemRecyclerAdapter.getShopItemList();

        for (int i = 0; i < shopItemRecyclerAdapter.getItemCount(); i++) {
            ShopItem shopItem = shopItemList.get(i);
            String estimatedPrice = shopItem.getEstimatedPrice();
            totalPrice += Integer.parseInt((estimatedPrice));
        }

        return Integer.toString(totalPrice);
    }

    private String computePriceOfBought() {
        int totalBought = 0;

        List<ShopItem> shopItemList = shopItemRecyclerAdapter.getShopItemList();

        for (int i = 0; i < shopItemRecyclerAdapter.getItemCount(); i++) {
            ShopItem shopItem = shopItemList.get(i);
            if (shopItem.isBought()) {
                String estimatedPrice = shopItem.getEstimatedPrice();
                totalBought += Integer.parseInt((estimatedPrice));
            }
        }

        return Integer.toString(totalBought);
    }

    public void deleteShopItem(final int shopItemToDeletePosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.attention);
        builder.setTitle(R.string.confirm_delete_item);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<ShopItem> shopItemList = shopItemRecyclerAdapter.getShopItemList();
                final ShopItem shopItem = shopItemList.get(shopItemToDeletePosition);

                getRealm().beginTransaction();
                shopItem.deleteFromRealm();
                shopItemList.remove(shopItemToDeletePosition);
                shopItemRecyclerAdapter.notifyItemRemoved(shopItemToDeletePosition);
                getRealm().commitTransaction();

                showSnackBarMessage(getString(R.string.deleted_an_item));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                shopItemRecyclerAdapter.notifyItemChanged(shopItemToDeletePosition);
                showSnackBarMessage(getString(R.string.deletion_cancelled));
            }
        });
        builder.show();
    }

    private void deleteAll() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.attention);
        builder.setTitle(R.string.confirm_delete_all);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getRealm().beginTransaction();

                int sizeOfShopItemsList = shopItemRecyclerAdapter.getItemCount();
                List<ShopItem> shopItemList = shopItemRecyclerAdapter.getShopItemList();
                if (sizeOfShopItemsList > 0) {
                    getRealm().deleteAll();
                    shopItemList.clear();
                    shopItemRecyclerAdapter.notifyDataSetChanged();
                }
                getRealm().commitTransaction();
                showSnackBarMessage(getString(R.string.deleted_all_items));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                showSnackBarMessage(getString(R.string.deletion_cancelled));
            }
        });

        builder.show();
    }

    public void toggleIsBoughtOfShopItem(ShopItem shopItem) {
        getRealm().beginTransaction();
        shopItem.setBought(!shopItem.isBought());
        getRealm().commitTransaction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ShoppingApplication) getApplication()).closeRealm();
    }

}
