package com.example.distributingdata;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class ModelDetailPage extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bitmap> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_detail_page);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        TextView title = findViewById(R.id.cocktail_title_text);
        TextView glass = findViewById(R.id.cocktail_glass);
        TextView description = findViewById(R.id.cocktail_description);
        TextView ingredients = findViewById(R.id.cocktail_ingredients);

        if (extras != null) {

            CocktailModel item = (CocktailModel) extras.getSerializable(ItemAdapter.EXTRA_NAME);

            title.setText(item.getTitle());
            glass.setText(item.getGlass());
            description.setText(item.getDescription());
            ingredients.setText(item.getIngredients());

            Bundle bundle = new Bundle();
            bundle.putString("url", item.getSmallImage());
            getSupportLoaderManager().initLoader(0, bundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
        String url = args.getString("url");
        return new AsyncImageLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap data) {
        ImageView imgView = findViewById(R.id.cocktail_image);
        imgView.setImageBitmap(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Bitmap> loader) {

    }
}
