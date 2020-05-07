package cz.pekostudio.nav

/**
 * Created by Lukas Urbanek on 07/05/2020.
 */
object Navigation {

    public val config = Config()

    public data class Config(var fileProvider: String? = null)

}