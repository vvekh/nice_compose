package com.example.diplomast.Retrofit;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Note;
import com.example.diplomast.DTO.Point;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.DTO.Timeline;
import com.example.diplomast.DTO.Work;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Call<List<Point>> getSpecialistPoints(@Path("id") int id);
    @Headers("Accept: application/json")
    @PUT("Specialist/{id}/Update")
    Call<Void> updateSpecialist(@Path("id") int id, @Body Specialist updatedSpecialist);
    @Headers("Accept: application/json")
    @POST("Specialist/Registration")
    Call<Void> postNewSpecialist(@Body Specialist newSpecialist);
    @Headers("Accept: application/json")
    @GET("Points")
    Call<List<Point>> getAllPoints();
    @Headers("Accept: application/json")
    @GET("Specialists/ByPoints")
    Call<List<Specialist>> getSpecialistsByCriteria(@Query("criteriaIds") List<Integer> criteriaIds);
    @Headers("Accept: application/json")
    @GET("Client/{id}/Favorites")
    Call<List<Specialist>> getFavoriteSpecialists(@Path("id") int id);
    @Headers("Accept: application/json")
    @GET("Specialists/1")
    Call<List<Specialist>> getAllSpecialists();
    @Headers("Accept: application/json")
    @POST("Client/{id}/Favorites/Add/{specialistId}")
    Call<ResponseBody> addFavoriteSpecialist(@Path("id") int id, @Path("specialistId") int specialistId);
    @Headers("Accept: application/json")
    @DELETE("Client/{id}/Favorites/Remove/{specialistId}")
    Call<ResponseBody> removeFavoriteSpecialist(@Path("id") int id, @Path("specialistId") int specialistId);
    @Headers("Accept: application/json")
    @GET("Notes")
    Call<List<Note>> getNotes();
    @Headers("Accept: application/json")
    @POST("Note/Insert")
    Call<String> insertNote(@Body Note note);
    @Headers("Accept: application/json")
    @POST("Specialist/{id}/AddPoint")
    Call<Void> addCriteriaToSpecialist(@Path("id") int id, @Body List<Integer> criteriaIds);
    @Headers("Accept: application/json")
    @HTTP(method = "DELETE", path = "Specialist/{id}/DeletePoint", hasBody = true)
    Call<Void> deleteCriteriaFromSpecialist(@Path("id") int id, @Body List<Integer> criteriaIds);
    @Headers("Accept: application/json")
    @POST("Client/{id}/Work/Add/{specialistId}")
    Call<Void> addWork(@Path("id") int id, @Path("specialistId") int specialistId);
    @Headers("Accept: application/json")
    @PUT("Specialist/{id}/Work/{clientId}/StartWorkSp")
    Call<Void> startWorkSp(@Path("id") int id, @Path("clientId") int clientId);
    @Headers("Accept: application/json")
    @PUT("Client/{id}/Work/{specialistId}/StartWorkCl")
    Call<Void> startWorkCl(@Path("id") int id, @Path("specialistId") int specialistId);
    @Headers("Accept: application/json")
    @PUT("Specialist/{id}/Work/{clientId}/EndWorkSp")
    Call<Void> endWorkSp(@Path("id") int id, @Path("clientId") int clientId);
    @Headers("Accept: application/json")
    @PUT("Client/{id}/Work/{specialistId}/EndWorkCl")
    Call<Void> endWorkCl(@Path("id") int id, @Path("specialistId") int specialistId);
    @Headers("Accept: application/json")
    @GET("Client/{id}/Work")
    Call<List<Work>> getWorksByClientId(@Path("id") int clientId);
    @Headers("Accept: application/json")
    @GET("Specialist/{id}/Work")
    Call<List<Work>> getWorksBySpecialistId(@Path("id") int specialistId);
    @Headers("Accept: application/json")
    @GET("Clients/All")
    Call<List<Client>> getAllClients();
    @Headers("Accept: application/json")
    @PUT("Specialist/{id}/Update2")
    Call<Void> update2Specialist(@Path("id") int id, @Body Specialist specialist);
    @Headers("Accept: application/json")
    @PUT("Work/{Clid}/{Spid}")
    Call<Work> workGet(@Path("Clid") int Clid, @Path("Spid") int Spid);




    @Headers("Accept: application/json")
    @Multipart
    @POST("Upload/Pdf1")
    Call<String> uploadPdf1(
            @Part MultipartBody.Part pdfFile,
            @Part("specialistId") int specialistId
    );
    @Headers("Accept: application/json")
    @Multipart
    @POST("Upload/Pdf2")
    Call<String> uploadPdf2(
            @Part MultipartBody.Part pdfFile,
            @Part("specialistId") int specialistId
    );
    @Headers("Accept: application/json")
    @Multipart
    @POST("Upload/Pdf3")
    Call<String> uploadPdf3(
            @Part MultipartBody.Part pdfFile,
            @Part("specialistId") int specialistId
    );
}
