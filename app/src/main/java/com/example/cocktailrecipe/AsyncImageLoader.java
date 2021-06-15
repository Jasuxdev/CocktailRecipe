package com.example.cocktailrecipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.InputStream;
import java.net.URL;

/// Load bitmap from an image url
public class AsyncImageLoader extends AsyncTaskLoader<Bitmap> {
    private String mUrl;

    public AsyncImageLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Nullable
    @Override
    public Bitmap loadInBackground() {
        try {
            URL url = new URL(mUrl);
            InputStream stream = url.openConnection().getInputStream();
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            return bmp;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
