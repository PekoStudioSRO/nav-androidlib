package cz.pekostudio.navexample.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Created by Lukas Urbanek on 14.2.19.
 */
interface Endpoints : BaseEndpoints {

    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<List<Unit>>

}

interface BaseEndpoints {

}
