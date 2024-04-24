package com.example.diplomast.Retrofit;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Note;
import com.example.diplomast.DTO.PointDTO;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.DTO.Timeline;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIinterface {
    @Headers("Accept: application/json")
    @GET("Client/{login}&{password}")
    Call<Client> getClientByLoginAndPassword(@Path("login") String login, @Path("password") String password);
    @Headers("Accept: application/json")
    @GET("Specialist/{login}&{password}")
    Call<Specialist> getSpecialistByLoginAndPassword(@Path("login") String login, @Path("password") String password);
    @Headers("Accept: application/json")
    @GET("Timelines")
    Call<List<Timeline>> getAllTimelines();
    @Headers("Accept: application/json")
    @PUT("Client/{id}/Update")
    Call<Void> updateClient(@Path("id") int id, @Body Client updatedClient);
    @Headers("Accept: application/json")
    @POST("Client/Registration")
    Call<Void> postNewClient(@Body Client newClient);
    @Headers("Accept: application/json")
    @GET("Specialist/{id}/Points")
    Call<List<PointDTO>> getSpecialistPoints(@Path("id") int id);
    @Headers("Accept: application/json")
    @PUT("Specialist/{id}/Update")
    Call<Void> updateSpecialist(@Path("id") int id, @Body Specialist updatedSpecialist);

    @Headers("Accept: application/json")
    @POST("Specialist/Registration")
    Call<Void> postNewSpecialist(@Body Specialist newSpecialist);
}
