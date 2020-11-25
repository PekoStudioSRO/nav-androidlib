package cz.pekostudio.nav.elements

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cz.pekostudio.objectsaver.toJson

/**
 * Created by Lukas Urbanek on 26/05/2020.
 */

inline fun <reified T> NavigationElement.launchActivity() {
    elementContext.startActivity(
        Intent(elementContext, T::class.java)
    )
}

inline fun <reified T> NavigationElement.launchActivity(intent: Intent.() -> Unit) {
    Intent(elementContext, T::class.java).run {
        intent(this)
        elementContext.startActivity(this)
    }
}

inline fun <reified T, D> NavigationElement.launchActivity(data: D? = null) {
    elementContext.startActivity(
        Intent(elementContext, T::class.java).putExtra("_data", data.toJson())
    )
}

inline fun <reified T, D> NavigationElement.launchActivity(data: D? = null, intent: Intent.() -> Unit) {
    Intent(elementContext, T::class.java).putExtra("_data", data.toJson()).run {
        intent(this)
        elementContext.startActivity(this)
    }
}