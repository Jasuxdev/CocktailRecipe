package com.example.cocktailrecipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private static final String LOG_TAG = ItemAdapter.class.getSimpleName();
    private ArrayList<CocktailModel> cocktailModelList;
    private LoaderManager mLoaderManager;
    public final static String EXTRA_NAME = "me.jas.itemsrecyclerview.EXTRA.NAME";

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(CocktailModel model, View itemView); //added parameter of type view
    }

    ItemAdapter(ArrayList<CocktailModel> cocktailModels, LoaderManager loaderManager, ListItemClickListener listener) {
        cocktailModelList = cocktailModels;
        mLoaderManager = loaderManager;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.model_list_item, parent, false);
        return new ItemViewHolder(itemView, this, mLoaderManager);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(cocktailModelList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return cocktailModelList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, LoaderManager.LoaderCallbacks<Bitmap>  {
        final TextView titleTextView;
        final ImageView mImageView;
        final ItemAdapter mAdapter;
        final LoaderManager mLoaderManager;

        public ItemViewHolder(View itemView, ItemAdapter adapter, LoaderManager loaderManager) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleText);
            mImageView = itemView.findViewById(R.id.cocktail_image_small);
            mAdapter = adapter;
            mLoaderManager = loaderManager;

            itemView.setOnClickListener(this);
        }

        void bind(CocktailModel model, int position) {
            titleTextView.setText(model.getTitle());
            Bundle bundle = new Bundle();
            bundle.putString("url", model.getSmallImage());
            mLoaderManager.restartLoader(100+position, bundle, this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            CocktailModel element = mAdapter.cocktailModelList.get(clickedPosition);
            mOnClickListener.onListItemClick(element, view);
        }

        @NonNull
        @Override
        public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
            String url = args.getString("url");
            return new AsyncImageLoader(itemView.getContext(), url);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap data) {
            mImageView.setImageBitmap(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Bitmap> loader) {

        }
    }
}
