package com.merakiphi.idiotbox.model;

/**
 * Created by anuragmaravi on 28/01/17.
 */

public class Movie {
    public Movie(){}

    private String posterPath,
            overview,
            releaseDate,
            id,
            title,
            language,
            backdropPath,
            popularity,
            voteCount,
            voteAverage,
    similarId,
    similarPosterPath,
    genreId,
    genreName;

    //YouTube Data
    private String videoKey,
    videoName,
    videoType;

    //Casting
    private String castingName,
    castingId,
    castingProfilePath,
    castingCharacter;
    int castImagePosition;

    private boolean adult;

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getSimilarId() {
        return similarId;
    }

    public void setSimilarId(String similarId) {
        this.similarId = similarId;
    }

    public String getSimilarPosterPath() {
        return similarPosterPath;
    }

    public void setSimilarPosterPath(String similarPosterPath) {
        this.similarPosterPath = similarPosterPath;
    }

    //Youtube Video Data
    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    //Casting

    public String getCastingName() {
        return castingName;
    }

    public void setCastingName(String castingName) {
        this.castingName = castingName;
    }

    public String getCastingId() {
        return castingId;
    }

    public void setCastingId(String castingId) {
        this.castingId = castingId;
    }

    public String getCastingProfilePath() {
        return castingProfilePath;
    }

    public void setCastingProfilePath(String castingProfilePath) {
        this.castingProfilePath = castingProfilePath;
    }

    public String getCastingCharacter() {
        return castingCharacter;
    }

    public void setCastingCharacter(String castingCharacter) {
        this.castingCharacter = castingCharacter;
    }

    public int getCastImagePosition() {
        return castImagePosition;
    }

    public void setCastImagePosition(int castImagePosition) {
        this.castImagePosition = castImagePosition;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
