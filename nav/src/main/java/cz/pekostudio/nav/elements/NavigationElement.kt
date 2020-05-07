package cz.pekostudio.nav.elements

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import cz.pekostudio.camera.picker.BitmapPictureSelect
import cz.pekostudio.camera.picker.FilePictureSelect
import cz.pekostudio.nav.Navigation
import cz.pekostudio.nav.fragments.FragmentNavigator
import java.io.File
import java.lang.IllegalStateException

/**
 * Created by Lukas Urbanek on 28/04/2020.
 */
interface NavigationElement {

    val elementContext: Context

    fun permissionRequired(permissions: Array<String>, onEnabled: () -> Unit)

    fun permissionRequired(permission: String, onEnabled: () -> Unit)

    fun onCreate()

    fun pickImage(onPicked: (bitmap: Bitmap) -> Unit)

    fun pickImageFile(onPicked: (file: File) -> Unit)

    fun fragmentNavigatorOf(id: Int, backNavigation: Boolean = false): FragmentNavigator

}