package com.merakiphi.idiotbox.other;

/**
 * Created by anuragmaravi on 28/01/17.
 */

public class Contract {
    public Contract(){}

    public final static String API_KEY = "0744794205a0d39eef72cad8722d4fba";
    //Base Url
    public final static String API_URL = "http://api.themoviedb.org/3/";


    //Query strings
    public final static String API_MOVIE = "movie/";
    public final static String API_TV = "tv";
    public final static String API_CASTING = "person";

    //Image Base Url
    public final static String API_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    //Poster Image Sizes
    public final static String apiImageSizeOriginal = "original";
    public final static String apiImageSizeXXXL = "w780";
    public final static String API_IMAGE_SIZE_XXL = "w500";
    public final static String apiImageSizeXL = "w342";
    public final static String API_IMAGE_SIZE_L = "w185";
    public final static String API_IMAGE_SIZE_M = "w154";
    public final static String API_IMAGE_SIZE_S = "w92";
    
    //Image Url
    public final static String API_IMAGE_URL = API_IMAGE_BASE_URL + API_IMAGE_SIZE_L + "/";


    //Movies
    public final static String API_MOVIE_NOW_PLAYING = "now_playing";
    public final static String API_MOVIE_UPCOMING = "upcoming";
    public final static String API_MOVIE_POPULAR = "popular";
    public final static String API_MOVIE_TOP_RATED = "top_rated";

    //Append to response
    public final static String APPEND = "&append_to_response=";
    public final static String SEPARATOR = ",";

    //Keywords for append to response
    public final static String VIDEOS = "videos";
    public final static String SIMILAR = "similar";
    public final static String CREDITS = "credits";
    public final static String REVIEWS = "reviews";
    public final static String IMAGES = "images";
    public final static String KEYWORDS = "keywords";
    public final static String LISTS = "lists";
    public final static String RELEASE_DATES = "release_dates";
    public final static String TRANSLATIONS = "translations";
    public final static String ALTERNATIVE_TITLES = "alternative_titles";


    //Youtube Url
    public final static String youtubeUrl = "https://www.youtube.com/watch?v=";
    //Youtube Thumbnail
    public final static String YOUTUBE_BASE_THUMBNAIL = "https://img.youtube.com/vi/";

    //Youtube Thumbnail Qualities
    public final static String youtubeQualityThumbnailD = "/default.jpg";
    public final static String YOUTUBE_QUALITY_THUMBNAIL_MQ = "/mqdefault.jpg";
    public final static String youtubeQualityThumbnailSd = "/sddefault.jpg";
    public final static String youtubeQualityThumbnailHq = "/hqdefault.jpg";
    public final static String youtubeQualityThumbnailMax = "/maxresdefault.jpg";


    //Region query
    public final static String REGION = "&region=";
    //Language Query
    public final static String LANGUAGE = "&language=";
    //Adult Content
    public final static String ADULT = "&include_adult=";



    //Omdb Base Url -- append imdb id
    public final static String OMDB_BASE_URL = "http://www.omdbapi.com/?i=";


    //Final Built Urls for movies
    public final static String MOVIE_NOW_PLAYING_REQUEST = API_URL + API_MOVIE + API_MOVIE_NOW_PLAYING + "?api_key=" + API_KEY;
    public final static String MOVIE_UPCOMING_REQUEST = API_URL + API_MOVIE + API_MOVIE_UPCOMING + "?api_key=" + API_KEY;
    public final static String MOVIE_POPULAR_REQUEST = API_URL + API_MOVIE+ API_MOVIE_POPULAR + "?api_key=" + API_KEY;
    public final static String MOVIE_TOP_RATED_REQUEST = API_URL + API_MOVIE + API_MOVIE_TOP_RATED + "?api_key=" + API_KEY;

    //Final builts urls for TvShows
    public final static String TV_POPULAR_REQUEST = API_URL + API_TV + "/popular?api_key=" + API_KEY;
    public final static String TV_ON_THE_AIR_REQUEST = API_URL + API_TV + "/on_the_air?api_key=" + API_KEY;
    public final static String TV_AIRING_TODAY_REQUEST = API_URL + API_TV + "/airing_today?api_key=" + API_KEY;
    public final static String TV_TOP_RATED_REQUEST = API_URL + API_TV + "/top_rated?api_key=" + API_KEY;




}
