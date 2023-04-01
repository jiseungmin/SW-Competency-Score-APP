package com.example.swcertificatio

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SWservice {
    @GET("/api/post/updateDB")
    fun getUser(): Call<List<SWdataDto>>
}