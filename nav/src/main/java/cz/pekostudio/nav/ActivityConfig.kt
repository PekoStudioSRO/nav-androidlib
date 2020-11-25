package cz.pekostudio.nav

/**
 * Created by Lukas Urbanek on 14/05/2020.
 */
data class ActivityConfig(
    internal val transparentStatusBar: Boolean = false,
    internal val transparentNavigationBar: Boolean = false,
    internal val lightStatusBar: Boolean = false,
    internal val lightNavigationBar: Boolean = false,
    internal val api30Insets: Boolean = false
) {

    companion object {

        val DEFAULT = ActivityConfig()
        val BY_STYLE = null
        val TRANSPARENT_LIGHT = ActivityConfig(
            transparentStatusBar = true,
            transparentNavigationBar = true,
            lightStatusBar = true,
            lightNavigationBar = true
        )
        val TRANSPARENT_DARK = ActivityConfig(
            transparentStatusBar = true,
            transparentNavigationBar = true,
            lightStatusBar = false,
            lightNavigationBar = false
        )

    }

}