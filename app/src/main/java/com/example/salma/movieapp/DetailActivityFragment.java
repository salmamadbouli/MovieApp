package com.example.salma.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class DetailActivityFragment extends Fragment {
    private TrailersAdapter mTrailersAdapter;
    private ArrayList<Trailers> List = new ArrayList<>();
    private ListView mTrailers;
    private ReviewsAdapter mReviewsAdapter;
    private ArrayList<Reviews> reviews = new ArrayList<>();
    private ListView mReviews;
    Context context;

    String moviewid;
    String movie_title ;
    String movie_description ;
    String movie_poster_key ;
    String movie_releasedate ;
    String movie_rating ;

    MovieDatabase movieDatabase;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();

        TextView name = (TextView) rootView.findViewById(R.id.detail_title);
        ImageView image = (ImageView) rootView.findViewById(R.id.detail_poster);
        TextView description = (TextView) rootView.findViewById(R.id.detail_synopsis);
        TextView releasedate = (TextView) rootView.findViewById(R.id.detail_release);
        TextView rating = (TextView) rootView.findViewById(R.id.detail_rating);
        TextView trailername =(TextView) rootView.findViewById(R.id.trailer_name);
        Button FavBtn=(Button)rootView.findViewById(R.id.btn_fav);

        context=getActivity();
        movieDatabase= new MovieDatabase(context);

       // Movie movie = getActivity().getIntent().getExtras().getParcelable("movie");
        // receive the movie Details from parcelable
        Movie movie= getArguments().getParcelable("movie");
        if (movie != null) {
            moviewid=movie.getId();
            movie_title = movie.getTitle();
            name.setText(movie_title);
             movie_poster_key = movie.getPosterPath();
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" +movie_poster_key).into(image);
            movie_description = movie.getSynopsis();
            description.setText(movie_description);
            movie_releasedate= movie.getReleaseDate();
            releasedate.setText(movie_releasedate);
            movie_rating = movie.getRating();
            rating.setText(movie_rating);
        }

        //Trailers
        mTrailersAdapter = new TrailersAdapter(getActivity(), List);
        FetchTrailersTask trailersTask = new FetchTrailersTask(mTrailersAdapter, rootView);

        // click method for the fav Button
        FavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (movieDatabase.IfExist(moviewid)){
                    Toast.makeText(getContext(),"Exist",Toast.LENGTH_LONG).show();
                    movieDatabase.delete_movie(moviewid);
                }
                else {
                    Toast.makeText(getContext(),"Not exist",Toast.LENGTH_LONG).show();
                    InsertInFavs();
                    Toast.makeText(getContext(),"Added",Toast.LENGTH_LONG).show();

                }
            }
        });

        String API = "f0ba9b3c0bcba6cd1077b914b6eb5e08";
        trailersTask.execute("https://api.themoviedb.org/3/movie/"+ moviewid + "/videos?api_key=" + API);
        mTrailers = (ListView)rootView.findViewById (R.id.trailers);
        mTrailers.setAdapter(mTrailersAdapter);
        mTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailers trailers = List.get(position);
                String movieKey = trailers.getKey();
                String trailername = trailers.getName();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + movieKey));
                startActivity(intent);
            }
        });
        // making Scroll for the trailers list
        mTrailers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;

                }
                view.onTouchEvent(event);
                return true;
            }
        });
        // Reviews
        mReviewsAdapter = new ReviewsAdapter(getActivity(), reviews);
        FetchReviewsTask reviewsTask = new FetchReviewsTask(mReviewsAdapter, rootView);
        reviewsTask.execute("https://api.themoviedb.org/3/movie/"+ moviewid + "/reviews?api_key=" + API);
        mReviews = (ListView)rootView.findViewById((R.id.reviews));
        mReviews.setAdapter(mReviewsAdapter);
        return rootView;
    }
    // Insert into DB
    public void  InsertInFavs() {

        final String id = "id";
        final String poster= "poster";
        final String title= "title";
        final String released_date= "date";
        final String rate= "rate";
        final String synopsis= "synopsis";

        ContentValues contentValues= new ContentValues();

        contentValues.put(id,Integer.parseInt(moviewid));
        contentValues.put(poster,movie_poster_key);
        contentValues.put(title,movie_title);
        contentValues.put(released_date,movie_releasedate);
        contentValues.put(rate,String.valueOf(movie_rating));
        contentValues.put(synopsis,movie_description);

        movieDatabase.insetInDb(contentValues);
    }
    // Trailers Async task
    public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<Trailers>>{
        private final String LOG_TAG = DetailActivityFragment.FetchTrailersTask.class.getSimpleName();
        private TrailersAdapter mTrailersAdapter;
        private View view;
        FetchTrailersTask(TrailersAdapter mTrailersAdapter, View view)
        {
            this.mTrailersAdapter = mTrailersAdapter;
            this.view = view;
        }
        private ArrayList<Trailers> getTrailersFromJson(String TrailersJsonString)
                throws JSONException {
            final String NAME = "name";
            final String KEY = "key";
            JSONObject trailerJson = new JSONObject(TrailersJsonString);
            JSONArray trailersArray = trailerJson.getJSONArray("results");
            int numTrailers = trailersArray.length();
            for (int i = 0; i < numTrailers; i++) {
                // Get the JSON object representing the movie
                JSONObject trailerData = trailersArray.getJSONObject(i);
                Trailers trailer = new Trailers();
                trailer.setName(trailerData.getString("name"));
                trailer.setKey(trailerData.getString("key"));
                List.add(trailer);
            }

            return List;
        }


        @Override
        protected ArrayList<Trailers> doInBackground(String... params) {
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
                return getTrailersFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;        }
        @Override
        protected void onPostExecute(ArrayList<Trailers> trailersList) {
            super.onPostExecute(trailersList);
            mTrailersAdapter.notifyDataSetChanged();

        }
    }

    // Reviews Task
    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Reviews>>{
        private final String LOG_TAG = DetailActivityFragment.FetchReviewsTask.class.getSimpleName();
        private ReviewsAdapter mReviewsAdapter;
        private View view;
        FetchReviewsTask(ReviewsAdapter mReviewsAdapter, View view)
        {
            this.mReviewsAdapter = mReviewsAdapter;
            this.view = view;
        }
        private ArrayList<Reviews> getTrailersFromJson(String ReviewsJsonString)
                throws JSONException {
            final String AUTHOR = "author";
            final String CONTENT = "content";
            JSONObject reviewJson = new JSONObject(ReviewsJsonString);
            JSONArray reviewsArray = reviewJson.getJSONArray("results");
            int numTrailers = reviewsArray.length();
            for (int i = 0; i < numTrailers; i++) {
                // Get the JSON object representing the movie
                JSONObject reviewData = reviewsArray.getJSONObject(i);
                Reviews review = new Reviews();
                review.setAuthor(reviewData.getString("author"));
                review.setContent(reviewData.getString("content"));
                reviews.add(review);
            }

            return reviews;
        }


        @Override
        protected ArrayList<Reviews> doInBackground(String... params) {
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
                return getTrailersFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;        }
        @Override
        protected void onPostExecute(ArrayList<Reviews> reviewsList) {
            super.onPostExecute(reviewsList);
            mReviewsAdapter.notifyDataSetChanged();

        }
    }

}

