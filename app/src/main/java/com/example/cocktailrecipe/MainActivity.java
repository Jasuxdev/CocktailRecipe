package com.example.cocktailrecipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ItemAdapter mAdapter;
    private EditText mCocktailInput;
    private TextView mErrorMessage;

    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private ArrayList<CocktailModel> mCocktailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCocktailInput = (EditText) findViewById(R.id.cocktailInput);
        mErrorMessage = (TextView) findViewById(R.id.main_error_message);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.dd_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if(savedInstanceState != null){
            getSupportLoaderManager().initLoader(1, null,this);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

    }

    public void searchCocktail(View view) {
        // Get the search string from the input field
        String queryString = mCocktailInput.getText().toString();

        // Hide keyboard when user taps the button
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        // Manage the network state and the empty search field case
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(1, queryBundle, this);

            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            if (queryString.length() == 0) {
                mErrorMessage.setText(R.string.no_search_term);
            } else {
                mErrorMessage.setText(R.string.no_network);
            }
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";

        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new CocktailLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("drinks");

            String title = null;
            String glass = null;
            StringBuilder ingredients = new StringBuilder();
            String imageLargeThumb = null;
            String imageSmallThumb = null;
            String description = null;
            String idDrink = null;

            mCocktailList = new ArrayList<CocktailModel>();

            for (int i=0; i < itemsArray.length(); i++) {
                // Get the current item information
                JSONObject cocktail = itemsArray.getJSONObject(i);

                Log.d(LOG_TAG, cocktail.toString());

                // Try to get the author and title from the current item, catch if
                // either field is empty and move on
                try {
                    title = cocktail.getString("strDrink");
                    glass = cocktail.getString("strGlass");
                    idDrink = cocktail.getString("idDrink");
                    imageSmallThumb = cocktail.getString("strDrinkThumb");
                    description = cocktail.getString("strInstructions");

                    ingredients.setLength(0);
                    for(int k = 1; k < 16; k++) {
                        String ingredient = cocktail.getString("strIngredient" + k);
                        String measure = cocktail.getString("strMeasure" + k).trim();

                        if (ingredient != null && ingredient != "" && ingredient != "null") {

                            ingredients.append(ingredient);

                            if (measure != null && measure != "" && measure != "null") {
                                ingredients.append(" (" + measure + ")");
                            }
                            if (k < 14) {
                                ingredients.append("\n");
                            }
                        }
                    }

                    CocktailModel cocktailModel = new CocktailModel();
                    cocktailModel.putTitle(title);
                    cocktailModel.putGlass(glass);
                    cocktailModel.putSmallImage(imageSmallThumb);
                    cocktailModel.putDescription(description);
                    cocktailModel.putIngredients(ingredients.toString());

                    Log.d(LOG_TAG, cocktailModel.getTitle());

                    mCocktailList.add(cocktailModel);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mErrorMessage.setVisibility(View.INVISIBLE);
            ItemAdapter mAdapter = new ItemAdapter(mCocktailList, getSupportLoaderManager(), this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results
            mErrorMessage.setText(R.string.no_results);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            /*
             * Restart recycler view all over
             */
            case R.id.action_refresh:
                if (mRecyclerView.getVisibility() == View.VISIBLE ) {
                    mAdapter = new ItemAdapter(mCocktailList, getSupportLoaderManager(), this);
                    mRecyclerView.setAdapter(mAdapter);

                    return true;
                }
                if (mErrorMessage.getVisibility() == View.VISIBLE) {
                    mErrorMessage.setVisibility(View.INVISIBLE);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  Detects item clicked in the recycler view
     * @param element
     * @param itemView
     */
    @Override
    public void onListItemClick(CocktailModel element, View itemView) {
        Intent intent = new Intent(itemView.getContext(), ModelDetailPage.class);
        Bundle extra = new Bundle();
        extra.putSerializable(ItemAdapter.EXTRA_NAME, element);
        intent.putExtras(extra);
        itemView.getContext().startActivity(intent);
    }
}