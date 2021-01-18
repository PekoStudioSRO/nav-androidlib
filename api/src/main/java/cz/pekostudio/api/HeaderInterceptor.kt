package cz.pekostudio.api

/**
 * Created by Lukas Urbanek on 1/18/21.
 */
interface HeaderInterceptor {
    fun onCreateHeaders(): Map<String, String>
}