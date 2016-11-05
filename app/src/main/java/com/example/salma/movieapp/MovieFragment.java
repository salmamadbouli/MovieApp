package com.example.salma.movieapp;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {
    private MovieAdapter mMovieAdapter;
    private View view;
    private ArrayList<Movie> movies = new ArrayList<>();
    public MovieFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieAdapter = new MovieAdapter(getActivity(),movies);
        Log.d("OnCreateView","onCreateView Done");
        // attach the adapter to the gridView
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);
        // calling the AsyncTask
        FetchMovieTask movieTask = new FetchMovieTask(mMovieAdapter, rootView);
        movieTask.execute();
        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private MovieAdapter mMovieAdapter;
        private View view;
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        FetchMovieTask(MovieAdapter movieadapter, View view)
        {
            this.mMovieAdapter = movieadapter;
            this.view = view;
        }
        /**
         * Converts JSON from MDB into movie objects
         */
        private ArrayList<Movie> getMovieDataFromJson(String moviesJsonString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "result";
            final String TITLE = "title";
            final String POSTER_PATH = "poster_path";
            final String PLOT = "overview";
            final String RATING = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray("results");
            int numMovies = moviesArray.length();
            for (int i = 0; i < numMovies; i++) {
                // Get the JSON object representing the movie
                JSONObject Data = moviesArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setTitle(Data.getString("title"));
                movie.setPosterPath(Data.getString("poster_path"));
                movie.setSynopsis(Data.getString("overview"));
                movie.setRating(Data.getDouble("vote_average"));
                movie.setReleaseDate(Data.getString("release_date"));
                movies.add(movie);
            }
            return movies;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            try {
                URL url = new URL("");
                // Create the request to moviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.d("Debug ", "Connection Done");
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                Log.d("Debug ", "Buffer Done");

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                Log.d("Debug ", "BufferLoop Done");

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                Log.d("YAG",moviesJsonStr.toString());
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            mMovieAdapter.notifyDataSetChanged();

        }
    }
}
