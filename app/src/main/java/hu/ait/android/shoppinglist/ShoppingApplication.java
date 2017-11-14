package hu.ait.android.shoppinglist;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ShoppingApplication extends Application {

    private Realm realmShopItems;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }

    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realmShopItems = Realm.getInstance(config);
    }

    public void closeRealm() {
        realmShopItems.close();
    }

    public Realm getRealmShopItems() {
        return realmShopItems;
    }
}
