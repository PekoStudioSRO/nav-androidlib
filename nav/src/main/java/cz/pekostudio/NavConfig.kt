package cz.pekostudio

import android.content.Context

/**
 * Created by Lukas Urbanek on 12/06/2020.
 */
class NavConfig(context: Context, config: NavConfig.() -> Unit) {

    init {
        config()
    }

    fun navigation(api: NavConfigData.Navigation.() -> Unit) {
        api(NavConfigData.navigation)
    }

    fun ui(api: NavConfigData.UserInterface.() -> Unit) {
        api(NavConfigData.userInterface)
    }
}