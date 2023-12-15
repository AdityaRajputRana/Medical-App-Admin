package com.example.medicalappadmin.rest.api;

import com.example.medicalappadmin.Models.retrofit.UploadAudioResponse;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiService {

    @Multipart
    @POST("api/page/additional/upload")
    Call<UploadAudioResponse> uploadFile(
            @Header("x-access-token") String token,
            @Part MultipartBody.Part file,
            @Part("data") String data
    );
}