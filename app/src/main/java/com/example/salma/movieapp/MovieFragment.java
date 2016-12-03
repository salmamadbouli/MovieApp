package com.example.salma.movieapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    ArrayList<Movie>arrayList_Favs=new ArrayList<>();
    GridView gridView;
    private NameListener mListener;
    void setNameListener(NameListener nameListener) {
        this.mListener = nameListener;
    }
    String API = "f0ba9b3c0bcba6cd1077b914b6eb5e08";
    public MovieFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FetchMovieTask mtask = new FetchMovieTask(mMovieAdapter , view);
        int id = item.getItemId();
        if (id == R.id.action_sort_by_toprated) {
            movies.clear();
            gridView.setAdapter(mMovieAdapter);
            mMovieAdapter.notifyDataSetChanged();
            mtask.execute("https://api.themoviedb.org/3/movie/top_rated?api_key=" + API);
        }

        if (id == R.id.action_sort_by_popular) {
            movies.clear();
            gridView.setAdapter(mMovieAdapter);
            mMovieAdapter.notifyDataSetChanged();
            mtask.execute("https://api.themoviedb.org/3/movie/popular?api_key=" + API);
        }
        if (id == R.id.action_favourite) {
            MovieDatabase movieDatabase= new MovieDatabase(getActivity());
            movies.clear();
            mMovieAdapter.notifyDataSetChanged();
            ArrayList<Movie> test = movieDatabase.getFavsMovies();
            for (int i =0; i<test.size(); i++) {
                movies.add(test.get(i));
            }
            mMovieAdapter = new MovieAdapter(getContext(), movies);
            mMovieAdapter.notifyDataSetChanged();
            gridView.setAdapter(mMovieAdapter);

//            arrayList_Favs=movieDatabase.getFavsMovies();
//            MovieAdapter movieAdapter= new MovieAdapter(getContext(), new ArrayList<Movie>());
//            gridView.setAdapter(movieAdapter);
//            movieAdapter.notifyDataSetChanged();


        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieAdapter = new MovieAdapter(getActivity(),movies);
        // attach the adapter to the gridView

        gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);
        // calling the AsyncTask
        FetchMovieTask movieTask = new FetchMovieTask(mMovieAdapter, rootView);
        movieTask.execute("https://api.themoviedb.org/3/movie/popular?api_key=" + API);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie;
                if(movies.size()==0)
                    movie = arrayList_Favs.get(position);
                else
                    movie = movies.get(position);
                mListener.setSelectedName(movie);
            }
        });
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
            final String ID = "id";
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
                movie.setId(Data.getString("id"));
                movie.setPosterPath(Data.getString("poster_path"));
                movie.setSynopsis(Data.getString("overview"));
                movie.setRating(Data.getString("vote_average"));
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
                URL url = new URL(params[0]);
                // Create the request to moviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
