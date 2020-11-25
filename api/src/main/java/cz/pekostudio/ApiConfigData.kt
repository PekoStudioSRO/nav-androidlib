package cz.pekostudio

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import cz.pekostudio.api.TokenRefresher
import cz.pekostudio.api.UserInstanceData

/**
 * Created by Lukas Urbanek on 07/05/2020.
 */
object ApiConfigData {

    public val api = Api()
    public data class Api(
        var userInstanceData: UserInstanceData? = null,
        var baseUrl: String = "",
        var autoSaveUserInstance: Boolean = true,
        var tokenRefresher: TokenRefresher? = null,
        var readTimeoutInSeconds: Long? = null,
        var writeTimeoutInSeconds: Long? = null,
        var callTimeoutInSeconds: Long? = null,
        var connectTimeoutInSeconds: Long? = null
    ) {

        inline fun <reified T> registerTypeAdapter(serializer: JsonSerializer<T>, type: Class<*> = T::class.java) {
            cz.pekostudio.api.Api.registerTypeAdapter(serializer, type)
        }

        inline fun <reified T> registerTypeAdapter(deserializer: JsonDeserializer<T>, type: Class<*> = T::class.java) {
            cz.pekostudio.api.Api.registerTypeAdapter(deserializer, type)
        }

    }

}