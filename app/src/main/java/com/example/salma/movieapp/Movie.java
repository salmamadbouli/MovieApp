package com.example.salma.movieapp;

import android.os.Parcel;
import android.os.Parcelable;



public class Movie implements Parcelable{
    private String id;
    private String title;
    private String posterPath;
    private String synopsis;
    private String rating;
    private String releaseDate;
public Movie() {}
    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {return  id;}
    public String getTitle() {
        return title;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public String getSynopsis() {
        return synopsis;
    }
    public String getRating() {
        return rating;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setId(String id) {this.id = id;}
    public void setTitle(String title) {
        this.title = title;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(synopsis);
        dest.writeString(rating);
        dest.writeString(releaseDate);
    }
}

