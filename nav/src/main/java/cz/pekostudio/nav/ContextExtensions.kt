package cz.pekostudio.nav

import android.app.Activity
import android.content.Context
import android.content.Intent
import cz.pekostudio.nav.elements.NavigationElement
import cz.pekostudio.objectsaver.fromJson
import cz.pekostudio.objectsaver.toJson

/**
 * Created by Lukas Urbanek on 26/05/2020.
 */

inline fun <reified T> Context.launchActivity() {
    startActivity(
        Intent(this, T::class.java)
    )
}

inline fun <reified T> Context.launchActivity(intent: (intent: Intent) -> Unit) {
    Intent(this, T::class.java)
        .let {
            intent(it)
            startActivity(it)
        }
}

inline fun <reified T> Activity.launchActivityForResult(requestCode: Int) {
    startActivityForResult(
        Intent(this, T::class.java),
        requestCode
    )
}

inline fun <reified T> Activity.launchActivityForResult(requestCode: Int, intent: (intent: Intent) -> Unit) {
    Intent(this, T::class.java)
        .let {
            intent(it)
            startActivityForResult(it, requestCode)
        }
}

inline fun <reified T, D> Context.launchActivity(data: D? = null) {
    startActivity(
        Intent(this, T::class.java).putExtra("_data", data.toJson())
    )
}

inline fun <reified T, D> Context.launchActivity(data: D? = null, intent: (intent: Intent) -> Unit) {
    Intent(this, T::class.java)
        .putExtra("_data", data.toJson())
        .let {
            intent(it)
            startActivity(it)
        }
}

inline fun <reified T, D> Activity.launchActivityForResult(requestCode: Int, data: D? = null) {
    startActivityForResult(
        Intent(this, T::class.java).putExtra("_data", data.toJson()),
        requestCode
    )
}

inline fun <reified T, D> Activity.launchActivityForResult(requestCode: Int, data: D? = null, intent: (intent: Intent) -> Unit) {
    Intent(this, T::class.java)
        .putExtra("_data", data.toJson())
        .let {
            intent(it)
            startActivityForResult(it, requestCode)
        }
}

inline fun <reified D> Activity.getLaunchData(): D? {
    return intent.getStringExtra("_data")?.fromJson()
}