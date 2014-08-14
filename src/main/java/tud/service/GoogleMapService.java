package tud.service;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by sce on 14.08.14.
 */
public interface GoogleMapService {
    @GET("/maps/api/geocode/json")
    LocationData locationData(@Query("address") String adress,@Query("key") String key);



}
