package com.example.salma.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements NameListener{
    static boolean  mIsTwoPane = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MovieFragment mf = new MovieFragment();
        mf.setNameListener(this);
        if (getSupportFragmentManager().findFragmentById(R.id.Frame1) != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Frame1, mf).commit();
        }
        else{
        getSupportFragmentManager().beginTransaction().add(R.id.Frame1, mf).commit();
    }
        if (null != findViewById(R.id.Frame2)) {
            mIsTwoPane = true;
        }
    }

    @Override
    public void setSelectedName(Movie mov) {
        // Case One Pane
        //Start Details Activity
        if (!mIsTwoPane) {

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie", mov);
            startActivity(intent);
        } else {
            //Case Two-PAne
            DetailActivityFragment mDetailsFragment= new DetailActivityFragment();
            Bundle extras= new Bundle();
            extras.putParcelable("movie", mov);
            mDetailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.Frame2,mDetailsFragment,"").commit();
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
