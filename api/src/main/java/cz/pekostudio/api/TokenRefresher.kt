package cz.pekostudio.api

/**
 * Created by Lukas Urbanek on 03/07/2020.
 */
abstract class TokenRefresher(val codeUnauthorized: Int) {

    abstract fun getNewAccessTokenAndSave(response: (ok: Boolean) -> Unit)

    abstract fun onError()

}