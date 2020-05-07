package cz.pekostudio.nav.elements

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import cz.pekostudio.camera.picker.BitmapPictureSelect
import cz.pekostudio.camera.picker.FilePictureSelect
import cz.pekostudio.nav.Navigation
import cz.pekostudio.nav.fragments.FragmentNavigator
import java.io.File
import java.lang.IllegalStateException

/**
 * Created by Lukas Urbanek on 8.1.19.
 */
abstract class BaseFragment(private val layout: Int) : Fragment(),
    NavigationElement {

    private var requestOnNavigatedWhenViewBinds: Boolean = false
    public var isViewsBinds: Boolean = false
    public lateinit var root: View

    public var height: Int = 0

    override val elementContext: Context
        get() = context!!

    private var parentActivity: BaseActivity? = null
        private set

    val stringId: String
        get() = javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(layout, container, false)
        root.viewTreeObserver.addOnGlobalLayoutListener { height = root.measuredHeight }
        parentActivity = activity as BaseActivity

        (parentActivity as BaseActivity).requestInsets()

        isViewsBinds = true
        if (requestOnNavigatedWhenViewBinds) {
            onNavigated()
            requestOnNavigatedWhenViewBinds = false
        }
        onCreate()

        return root
    }

    override fun onCreate() {

    }

    fun <T : View> findViewById(@IdRes id: Int): T {
        return root.findViewById(id)
    }

    open fun onNavigated() { }

    fun showError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    public override fun permissionRequired(permissions: Array<String>, onEnabled: () -> Unit) {
        (parentActivity as BaseActivity).permissionRequired(permissions, onEnabled)
    }

    public override fun permissionRequired(permission: String, onEnabled: () -> Unit) {
        permissionRequired(arrayOf(permission), onEnabled)
    }

    open fun onBackPressed(): Boolean {
        return true
    }

    override fun pickImage(onPicked: (bitmap: Bitmap) -> Unit) {
        parentActivity?.pickImage(onPicked)
    }

    override fun pickImageFile(onPicked: (file: File) -> Unit) {
        parentActivity?.pickImageFile(onPicked)
    }

    override fun fragmentNavigatorOf(id: Int, backNavigation: Boolean): FragmentNavigator {
        TODO("Not yet implemented")
    }
}
