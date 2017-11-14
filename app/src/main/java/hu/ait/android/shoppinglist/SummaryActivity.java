package hu.ait.android.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(R.drawable.cart_icon);
        }

        TextView totalNumberOfItems = findViewById(R.id.tvTotalNumberOfItems);
        TextView totalEstimatedPrice = findViewById(R.id.tvTotalEstimatedPrice);
        TextView tvTotalBought = findViewById(R.id.tvTotalBought);
        TextView tvTotalNotBought = findViewById(R.id.tvTotalNotBought);

        if (getIntent().hasExtra(MainActivity.NUMBER_OF_ITEMS) &&
                getIntent().hasExtra(MainActivity.TOTAL_OF_PRICES)) {
            int numberOfItems = getIntent().getIntExtra(MainActivity.NUMBER_OF_ITEMS, 0);
            int totalPrice = getIntent().getIntExtra(MainActivity.TOTAL_OF_PRICES, 0);
            int totalBought = getIntent().getIntExtra(MainActivity.TOTAL_BOUGHT, 0);
            int totalNotBought = getIntent().getIntExtra(MainActivity.TOTAL_NOT_BOUGHT, 0);

            totalNumberOfItems.setText(getString(R.string.total_number_of_items, Integer.toString(numberOfItems)));
            totalEstimatedPrice.setText(getString(R.string.total_price, Integer.toString(totalPrice)));
            tvTotalBought.setText(getString(R.string.total_bought, Integer.toString(totalBought)));
            tvTotalNotBought.setText(getString(R.string.total_not_bought, Integer.toString(totalNotBought)));

        } else {
            totalNumberOfItems.setText(getString(R.string.total_number_of_items, getString(R.string.default_value)));
            totalEstimatedPrice.setText(getString(R.string.total_price, getString(R.string.default_value)));
            tvTotalBought.setText(getString(R.string.total_bought, getString(R.string.default_value)));
            tvTotalNotBought.setText(getString(R.string.total_not_bought, getString(R.string.default_value)));
        }
    }
}
