package com.example.cocktailrecipe.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Base URL for Cocktails API
    private static final String COCKTAILS_BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/search.php";
    // Parameter for the search string
    private static final String QUERY_PARAM = "s";

    public static String getCocktailInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String cocktailJSONString = null;

        try {
            Uri builtURI = Uri.parse(COCKTAILS_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .build();
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream
            InputStream inputStream = urlConnection.getInputStream();

            // Create a buffered reader from that input stream
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a StringBuilder to hold the incoming response
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing) but
                // it does make debugging a *lot* easier if you print out the completed buffer for debugging
                builder.append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty. No point in parsing
                return null;
            }

            cocktailJSONString = builder.toString();
            Log.d(LOG_TAG, cocktailJSONString);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // Close the BufferReader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return cocktailJSONString;
    }
}
