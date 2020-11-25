package cz.pekostudio.api

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import com.novoda.merlin.MerlinsBeard
import cz.pekostudio.ApiConfigData
import cz.pekostudio.objectsaver.ObjectSaver
import cz.pekostudio.objectsaver.save
import cz.pekostudio.uiutils.runDelayed
import cz.pekostudio.uiutils.runOnUiThread

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * Created by lukasurbanek on 1/31/2018.
 */

inline fun <reified T : Any> api(
    auth: Boolean = true,
    baseUrl: String = ApiConfigData.api.baseUrl,
    gson: Gson = defaultGson()
): T {
    val httpClient = OkHttpClient.Builder()
        .configTimeouts()
        .addNetworkInterceptor(StethoInterceptor()).apply {
            if (auth) {
                addInterceptor { chain ->
                    val newRequest =
                        chain.request().newBuilder()
                            .addHeader(
                                "Authorization",
                                "Bearer ${ApiConfigData.api.userInstanceData?.accessToken}"
                            )
                            .build()
                    chain.proceed(newRequest)
                }
            }
        }

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addConverterFactories()
        .client(httpClient.build())
        .build()
        .create(T::class.java)
}

fun OkHttpClient.Builder.configTimeouts(): OkHttpClient.Builder {
    ApiConfigData.api.run {
        readTimeoutInSeconds?.let {
            readTimeout(it, TimeUnit.SECONDS)
        }
        callTimeoutInSeconds?.let {
            callTimeout(it, TimeUnit.SECONDS)
        }
        connectTimeoutInSeconds?.let {
            connectTimeout(it, TimeUnit.SECONDS)
        }
        writeTimeoutInSeconds?.let {
            writeTimeout(it, TimeUnit.SECONDS)
        }
    }
    return this
}

inline fun <reified T : Any> api(
    auth: Boolean = true,
    baseUrl: String = ApiConfigData.api.baseUrl,
    gson: Gson = defaultGson(),
    api: T.() -> Unit
) {
    api(api(auth, baseUrl, gson))
}

inline fun <reified T : Any> api(
    authToken: String?,
    baseUrl: String = ApiConfigData.api.baseUrl,
    gson: Gson = defaultGson()
): T {
    val httpClient = OkHttpClient.Builder()
        .configTimeouts()
        .addNetworkInterceptor(StethoInterceptor()).apply {
            if (authToken != null) {
                addInterceptor { chain ->
                    val newRequest =
                        chain.request().newBuilder()
                            .addHeader(
                                "Authorization",
                                "Bearer $authToken"
                            )
                            .build()
                    chain.proceed(newRequest)
                }
            }
        }

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addConverterFactories()
        .client(httpClient.build())
        .build()
        .create(T::class.java)
}

fun Retrofit.Builder.addConverterFactories(): Retrofit.Builder {
    return apply {
        Api.converterFactories.forEach {
            addConverterFactory(it)
        }
    }
}

inline fun <reified T : Any> api(
    authToken: String?,
    baseUrl: String = ApiConfigData.api.baseUrl,
    gson: Gson = defaultGson(),
    api: T.() -> Unit
) {
    api(api(authToken, baseUrl, gson))
}

fun <T> Call<T>.call(result: CallResponse<T>.() -> Unit) {
    thread {
        try {
            execute().apply {
                CallResponse<T>(this, this@call).run {
                    result(this)
                    invalidate()
                }
            }
        } catch (e: Exception) {
            CallResponse(null, this@call).run {
                result(this)
                invalidate()
            }
            e.printStackTrace()
        }
    }
}

fun <T> Call<T>.get(): CallResponse<T>? {
    try {
        execute().apply {
            CallResponse<T>(this, this@get).run {
                return this
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun defaultGson(): Gson = GsonBuilder().setLenient().registerTypeAdapters().create()

//todo responseOnUiThread = true
public class CallResponse<T>(private val response: Response<T>?, private val call: Call<T>) {

    val body: T? = response?.body()
    val code: Int = response?.code() ?: -1
    val errorBody: ResponseBody? = response?.errorBody()

    private var onOk: (T.() -> Unit)? = null
    private var onError: (ErrorResponse<T>.() -> Unit)? = null

    internal fun invalidate() {

        if ((response?.code() ?: 0) >= 200 && (response?.code() ?: 0) <= 299 && response?.body() != null) {
            runOnUiThread {
                onOk?.invoke(response.body()!!)
            }
        //todo refactor
        } else if (
            ApiConfigData.api.tokenRefresher != null
            && response?.code() ?: -1 == ApiConfigData.api.tokenRefresher?.codeUnauthorized
            && call.request().header("Authorization")?.contains("Bearer") != true
        ) {
            ApiConfigData.api.tokenRefresher?.getNewAccessTokenAndSave { ok ->
                if (ok) {
                    runDelayed(500) {
                        call.clone().call {
                            ok {
                                runOnUiThread {
                                    onOk?.invoke(this)
                                }
                            }
                            error {
                                runOnUiThread {
                                    onError?.invoke(this)
                                }
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        ApiConfigData.api.tokenRefresher?.onError()
                    }
                }
            }
        } else {
            ErrorResponse(response).run {
                runOnUiThread {
                    onError?.invoke(this)
                }
                invalidate()
            }
        }
    }

    fun ok(block: T.() -> Unit) {
        onOk = block
    }

    fun error(block: ErrorResponse<T>.() -> Unit) {
        onError = block
    }

}

public class ErrorResponse<T>(val response: Response<T>?) {

    internal fun invalidate() {

    }

    fun noInternet(context: Context): Boolean {
        return !MerlinsBeard.Builder().build(context).isConnected
    }

}

fun GsonBuilder.registerTypeAdapters(): GsonBuilder {
    (Api.serializers + Api.deserializers).forEach {
        registerTypeAdapter(it.key, it.value)
    }
    return this
}

object Api {

    class BasicUserInstance : UserInstanceData() {
        override var accessToken: String? = null
        override var refreshToken: String? = null
    }

    val serializers = HashMap<Class<*>, JsonSerializer<*>>()
    val deserializers = HashMap<Class<*>, JsonDeserializer<*>>()
    val converterFactories = ArrayList<Converter.Factory>()

    inline fun <reified T> registerTypeAdapter(serializer: JsonSerializer<T>, type: Class<*> = T::class.java) {
        serializers[type] = serializer
    }

    inline fun <reified T> registerTypeAdapter(deserializer: JsonDeserializer<T>, type: Class<*> = T::class.java) {
        deserializers[type] = deserializer
    }

    fun addConverterFactory(converterFactory: Converter.Factory) {
        converterFactories.add(converterFactory)
    }

    fun setBearerToken(context: Context, accessToken: String, refreshToken: String? = null) {
        ApiConfigData.api.run {
            userInstanceData = BasicUserInstance().also {
                it.accessToken = accessToken
                it.refreshToken = refreshToken
            }
            userInstanceData.save(context)
        }
    }

    fun loadUserInstance(context: Context) {
        ApiConfigData.api.userInstanceData = ObjectSaver(context).get() ?: BasicUserInstance()
    }

    fun setUserInstance(userInstance: UserInstanceData) {
        ApiConfigData.api.userInstanceData = userInstance
    }

    fun getUserInstance(): UserInstanceData? {
        return ApiConfigData.api.userInstanceData
    }

}