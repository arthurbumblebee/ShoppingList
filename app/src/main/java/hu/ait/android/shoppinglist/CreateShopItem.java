package hu.ait.android.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.UUID;

import hu.ait.android.shoppinglist.data.ShopItem;
import io.realm.Realm;

public class CreateShopItem extends AppCompatActivity {
    public static final String KEY_SHOP_ITEM = "KEY_SHOP_ITEM";
    private Spinner spinnerShopItemCategory;
    private EditText etShopItemPrice;
    private EditText etShopItemName;
    private EditText etShopItemDesc;
    private CheckBox cbBought;
    private ShopItem shopItemToEdit = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop_item);

        setupActivityUI();

        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            initEdit();
        } else {
            initCreate();
        }
    }

    private void initCreate() {
        getRealm().beginTransaction();
        shopItemToEdit = getRealm().createObject(ShopItem.class, UUID.randomUUID().toString());
        shopItemToEdit.setBought(false);
        getRealm().commitTransaction();
    }

    private void initEdit() {
        String shopItemID = getIntent().getStringExtra(MainActivity.KEY_EDIT);
        shopItemToEdit = getRealm().where(ShopItem.class)
                .equalTo("shopItemID", shopItemID)
                .findFirst();

        assert shopItemToEdit != null;
        etShopItemName.setText(shopItemToEdit.getShopItemName());
        etShopItemDesc.setText(shopItemToEdit.getShopItemDescription());
        etShopItemPrice.setText(shopItemToEdit.getEstimatedPrice());
        cbBought.setChecked(shopItemToEdit.isBought());
        spinnerShopItemCategory.setSelection(shopItemToEdit.getShopItemCategory().getValue());
    }

    private void setupActivityUI() {
        spinnerShopItemCategory = findViewById(R.id.spinnerItemCategory);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShopItemCategory.setAdapter(adapter);

        etShopItemName = findViewById(R.id.etItemName);
        etShopItemDesc = findViewById(R.id.etItemDesc);
        etShopItemPrice = findViewById(R.id.etItemPrice);
        cbBought = findViewById(R.id.cbBought);


        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveShopItem();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void saveShopItem() {
        try {
            if (!TextUtils.isEmpty(etShopItemName.getText()) && !TextUtils.isEmpty(etShopItemPrice.getText())) {
                Intent intentResult = new Intent();

                getRealm().beginTransaction();
                shopItemToEdit.setShopItemName(etShopItemName.getText().toString());
                shopItemToEdit.setShopItemDescription(etShopItemDesc.getText().toString());
                shopItemToEdit.setShopItemCategory(spinnerShopItemCategory.getSelectedItemPosition());
                shopItemToEdit.setEstimatedPrice(etShopItemPrice.getText().toString());
                shopItemToEdit.setBought(cbBought.isChecked());
                getRealm().commitTransaction();

                intentResult.putExtra(KEY_SHOP_ITEM, shopItemToEdit.getShopItemID());
                setResult(RESULT_OK, intentResult);
                finish();
            } else {
                if (TextUtils.isEmpty(etShopItemName.getText())) {
                    etShopItemName.setError(getString(R.string.et_name_error));

                } else
                    etShopItemPrice.setError(getString(R.string.et_price_error));
            }
        } catch (NumberFormatException nf) {
            etShopItemName.setError(getString(R.string.wrong_content));
            nf.printStackTrace();
        }
    }

    public Realm getRealm() {
        return ((ShoppingApplication) getApplication()).getRealmShopItems();
    }

}
