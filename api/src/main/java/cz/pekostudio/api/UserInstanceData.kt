package cz.pekostudio.api

/**
 * Created by Lukas Urbanek on 11/05/2020.
 */
abstract class UserInstanceData {
    abstract var accessToken: String?
    abstract var refreshToken: String?
}