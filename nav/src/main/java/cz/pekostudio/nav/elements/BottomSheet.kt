package cz.pekostudio.nav.elements

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cz.pekostudio.nav.R
import cz.pekostudio.nav.fragments.FragmentNavigator
import java.io.File

abstract class BottomSheet(@LayoutRes val layout: Int) : BottomSheetDialogFragment(), NavigationElement {

    private var parentActivity: BaseActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DefaultBottomSheetThemeN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false).apply {
            parentActivity = activity as? BaseActivity
            onCreateView(this)
        }
    }

    abstract fun onCreateView(view: View)

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog?.window?.decorView?.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    override val elementContext: Context
        get() = context!!

    override fun permissionRequired(permissions: Array<String>, onEnabled: () -> Unit) {
        parentActivity?.permissionRequired(permissions, onEnabled)
    }

    override fun permissionRequired(permission: String, onEnabled: () -> Unit) {
        permissionRequired(arrayOf(permission), onEnabled)
    }

    override fun pickImage(onPicked: (bitmap: Bitmap) -> Unit) {
        parentActivity?.pickImage(onPicked)
    }

    override fun pickImageFile(onPicked: (file: File) -> Unit) {
        parentActivity?.pickImageFile(onPicked)
    }

    override fun pickMultipleImage(onPicked: (bitmaps: ArrayList<Bitmap>) -> Unit) {
        parentActivity?.pickMultipleImage(onPicked)
    }

    override fun pickMultipleImageFile(onPicked: (files: ArrayList<File>) -> Unit) {
        parentActivity?.pickMultipleImageFile(onPicked)
    }

    override fun fragmentNavigatorOf(id: Int, backNavigation: Boolean): FragmentNavigator {
        throw IllegalStateException("fragmentNavigatorOf not available in BottomSheet")
    }

    override fun showLoading() {
        throw IllegalStateException("showLoading not available in BottomSheet")
    }

    override fun hideLoading() {
        throw IllegalStateException("hideLoading not available in BottomSheet")
    }

}