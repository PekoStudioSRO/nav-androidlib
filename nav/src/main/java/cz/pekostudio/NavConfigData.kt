package cz.pekostudio

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import cz.pekostudio.nav.R

/**
 * Created by Lukas Urbanek on 07/05/2020.
 */
object NavConfigData {

    public val navigation = Navigation()
    public data class Navigation(
        var fileProvider: String? = null
    )

    public val userInterface = UserInterface()
    public data class UserInterface(
        var dialogDefaultBackgroundResource: Int = R.drawable.bg_dialog,
        var dialogDefaultTextLayout: Int = R.layout.dialog_text,
        var dialogDefaultLoadingLayout: Int = R.layout.dialog_loading
    )

}