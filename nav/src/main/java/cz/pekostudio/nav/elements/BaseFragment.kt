package cz.pekostudio.nav.elements

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import cz.pekostudio.dialog.LoadingOverlay
import cz.pekostudio.nav.fragments.FragmentNavigator
import java.io.File
import kotlin.reflect.KProperty

/**
 * Created by Lukas Urbanek on 8.1.19.
 */
abstract class BaseFragment(private val layout: Int) : Fragment(),
    NavigationElement {

    public lateinit var root: View
    private var mainFragmentNavigator: FragmentNavigator? = null
    public var parentFragmentNavigator: FragmentNavigator? = null

    private val loadingOverlay by lazy { LoadingOverlay(activity!!) }

    public var height: Int = 0

    override val elementContext: Context
        get() = context!!

    private var parentActivity: BaseActivity? = null

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

        onCreate()
        onNavigated()

        (activity as? BaseActivity)?.insets?.let {
            onInsetsUpdated(it)
        } ?: run {
            parentActivity?.insetsUpdateListeners?.add {
                onInsetsUpdated(it)
            }
        }

        return root
    }

    override fun onCreate() {

    }

    open fun onInsetsUpdated(insets: WindowInsetsCompat?) {

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

    override fun pickMultipleImage(onPicked: (bitmaps: ArrayList<Bitmap>) -> Unit) {
        parentActivity?.pickMultipleImage(onPicked)
    }

    override fun pickMultipleImageFile(onPicked: (files: ArrayList<File>) -> Unit) {
        parentActivity?.pickMultipleImageFile(onPicked)
    }

    override fun fragmentNavigatorOf(id: Int, backNavigation: Boolean): FragmentNavigator {
        return FragmentNavigator(childFragmentManager, id).apply {
            if (backNavigation) {
                mainFragmentNavigator = this
            }
        }
    }

    fun <T : View> id(id: Int): FindViewDelegate<T> {
        return FindViewDelegate(id)
    }

    fun onClickView(id: Int, setOnClickListener: () -> Unit) {
        findViewById<View>(id).setOnClickListener {
            setOnClickListener()
        }
    }

    inner class FindViewDelegate<T : View>(val id: Int) {

        private var view: T? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return view ?: findViewById<T>(id).also { view = it }
        }

    }

    override fun showLoading() {
        loadingOverlay.show()
    }

    override fun hideLoading() {
        loadingOverlay.dismiss()
    }

    fun finish() {
        parentFragmentNavigator?.onBackPressed()
    }
}
