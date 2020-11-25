package cz.pekostudio.nav.elements

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import cz.pekostudio.nav.fragments.FragmentNavigator
import java.io.File

/**
 * Created by Lukas Urbanek on 28/04/2020.
 */
interface NavigationElement {

    val elementContext: Context

    fun permissionRequired(permissions: Array<String>, onEnabled: () -> Unit)

    fun permissionRequired(permission: String, onEnabled: () -> Unit)

    fun onCreate()

    fun pickImage(onPicked: (bitmap: Bitmap) -> Unit)

    fun pickMultipleImageFile(onPicked: (files: ArrayList<File>) -> Unit)

    fun pickMultipleImage(onPicked: (bitmaps: ArrayList<Bitmap>) -> Unit)

    fun pickImageFile(onPicked: (file: File) -> Unit)

    fun fragmentNavigatorOf(id: Int, backNavigation: Boolean = false): FragmentNavigator

    fun showLoading()

    fun hideLoading()

}