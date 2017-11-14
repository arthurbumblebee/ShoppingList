package hu.ait.android.shoppinglist.data;

import hu.ait.android.shoppinglist.R;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ShopItem extends RealmObject {
    public enum ShopItemCategory {
        FOOD(0, R.drawable.food),
        DRINK(1, R.drawable.drink),
        CLOTHES(2, R.drawable.clothes),
        PERSONAL(3, R.drawable.personal),
        SCHOOL(4, R.drawable.school),;

        private int value;
        private int iconId;

        ShopItemCategory(int value, int iconId) {
            this.value = value;
            this.iconId = iconId;
        }

        public int getValue() {
            return value;
        }

        public int getIconId() {
            return iconId;
        }

        public static ShopItemCategory fromInt(int value) {
            for (ShopItemCategory p : ShopItemCategory.values()) {
                if (p.value == value) {
                    return p;
                }
            }
            return FOOD;
        }
    }

    @PrimaryKey
    private String shopItemID;

    private String shopItemName;
    private String shopItemDescription;
    private String estimatedPrice;
    private int shopItemCategory;
    private boolean isBought;

    public ShopItem() {

    }

    public ShopItem(String shopItemName, String shopItemDescription, String estimatedPrice, int shopItemCategory, boolean isBought) {
        this.shopItemName = shopItemName;
        this.shopItemDescription = shopItemDescription;
        this.estimatedPrice = estimatedPrice;
        this.shopItemCategory = shopItemCategory;
        this.isBought = isBought;
    }

    public String getShopItemName() {
        return shopItemName;
    }

    public void setShopItemName(String shopItemName) {
        this.shopItemName = shopItemName;
    }

    public String getShopItemDescription() {
        return shopItemDescription;
    }

    public void setShopItemDescription(String shopItemDescription) {
        this.shopItemDescription = shopItemDescription;
    }

    public String getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(String estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public ShopItemCategory getShopItemCategory() {
        return ShopItemCategory.fromInt(shopItemCategory);
    }

    public void setShopItemCategory(int shopItemCategory) {
        this.shopItemCategory = shopItemCategory;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }


    public String getShopItemID() {
        return shopItemID;
    }
}
