package cz.pekostudio.nav.elements

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cz.pekostudio.NavConfigData
import cz.pekostudio.camera.picker.AbstractPictureSelect
import cz.pekostudio.camera.picker.BitmapPictureSelect
import cz.pekostudio.camera.picker.FilePictureSelect
import cz.pekostudio.dialog.LoadingOverlay
import cz.pekostudio.nav.ActivityConfig
import cz.pekostudio.nav.fragments.FragmentNavigator
import cz.pekostudio.nav.permissions.PermissionsHelper
import cz.pekostudio.objectsaver.fromJson
import cz.pekostudio.objectsaver.toJson
import cz.pekostudio.uiutils.isApi
import cz.pekostudio.uiutils.window.setLightNavgiationBar
import cz.pekostudio.uiutils.window.setLightStatusBar
import java.io.File
import kotlin.reflect.KProperty

/**
 * Created by Lukas Urbanek on 28/04/2020.
 */
abstract class BaseActivity(
    private val layout: Int,
    private val config: ActivityConfig? = ActivityConfig.BY_STYLE
) : AppCompatActivity(), NavigationElement {

    internal val insetsUpdateListeners: ArrayList<(insets: WindowInsetsCompat?) -> Unit> = ArrayList()
    internal var permissionRequests: HashMap<Int, () -> Unit> = HashMap()
    var insets: WindowInsetsCompat? = null

    companion object {
        internal var pictureSelect: AbstractPictureSelect<*>? = null
    }

    private var mainFragmentNavigator: FragmentNavigator? = null

    private val loadingOverlay by lazy { LoadingOverlay(this) }

    override val elementContext: Context
        get() = this

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layout)
        requestInsets()
        onCreate()

        config?.let { config ->
            if (config.transparentStatusBar && config.transparentNavigationBar) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
            if (config.transparentStatusBar) {
                window.statusBarColor = if (!isApi(23) && config.lightStatusBar) Color.BLACK else Color.TRANSPARENT
            }
            if (config.transparentNavigationBar) {
                window.navigationBarColor = if (!isApi(26) && config.lightNavigationBar) Color.BLACK else Color.TRANSPARENT
            }

            setLightStatusBar(config.lightStatusBar)
            setLightNavgiationBar(config.lightNavigationBar)
        }
    }

    override fun onCreate() {

    }

    //todo refactor
    open fun requestInsets() {
        val content = findViewById<View>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && config?.api30Insets == true) {
            window.setDecorFitsSystemWindows(false)
            content.setWindowInsetsAnimationCallback(object : WindowInsetsAnimation.Callback(
                DISPATCH_MODE_STOP
            ) {
                override fun onProgress(
                    insets: WindowInsets,
                    p1: MutableList<WindowInsetsAnimation>
                ): WindowInsets {
                    onImeInsetsUpdated(insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars()).bottom)
                    return insets
                }
            })
            content.setOnApplyWindowInsetsListener { _, windowInsets ->
                val wInsets = windowInsets.getInsets(WindowInsets.Type.systemBars())
                val nInsets = WindowInsetsCompat.Builder()
                    .setSystemWindowInsets(Insets.toCompatInsets(wInsets)).build()
                onInsetsUpdated(nInsets)
                insetsUpdateListeners.forEach {
                    it.invoke(nInsets)
                }
                windowInsets
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(content) { _, insets ->
                this.insets = insets
                onInsetsUpdated(insets)
                insetsUpdateListeners.forEach {
                    it.invoke(insets)
                }
                insets.consumeSystemWindowInsets()
            }
        }
    }

    open fun onInsetsUpdated(insets: WindowInsetsCompat?) {

    }

    open fun onImeInsetsUpdated(bottomInsets: Int) {

    }

    override fun pickImage(onPicked: (bitmap: Bitmap) -> Unit) {
        pictureSelect = BitmapPictureSelect(this,
            NavConfigData.navigation.fileProvider ?: throw IllegalStateException("Není registrován file provider")
        ).also {
            it.pickImage(function = onPicked)
        }
    }

    override fun pickImageFile(onPicked: (file: File) -> Unit) {
        pictureSelect = FilePictureSelect(this,
            NavConfigData.navigation.fileProvider ?: throw IllegalStateException("Není registrován file provider")
        ).also {
            it.pickImage(function = onPicked)
        }
    }

    override fun pickMultipleImage(onPicked: (bitmaps: ArrayList<Bitmap>) -> Unit) {
        pictureSelect = BitmapPictureSelect(this,
            NavConfigData.navigation.fileProvider ?: throw IllegalStateException("Není registrován file provider")
        ).also {
            it.pickMultipleImages(function = onPicked)
        }
    }

    override fun pickMultipleImageFile(onPicked: (files: ArrayList<File>) -> Unit) {
        pictureSelect = FilePictureSelect(this,
            NavConfigData.navigation.fileProvider ?: throw IllegalStateException("Není registrován file provider")
        ).also {
            it.pickMultipleImages(function = onPicked)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        pictureSelect?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequests[requestCode]?.let {
            if (checkPermissionsResults(grantResults)) {
                it.invoke()
                permissionRequests.remove(requestCode)
            }
        }
    }

    public override fun permissionRequired(permissions: Array<String>, onEnabled: () -> Unit) {
        if (!PermissionsHelper.checkPermissions(
                this,
                *permissions
            )
        ) {
            val id =
                PermissionsHelper.createPermissionRequestId(
                    this
                )
            permissionRequests[id] = onEnabled
            ActivityCompat.requestPermissions(this, permissions, id)
        } else {
            onEnabled()
        }
    }

    public override fun permissionRequired(permission: String, onEnabled: () -> Unit) {
        permissionRequired(arrayOf(permission), onEnabled)
    }

    private fun checkPermissionsResults(results: IntArray): Boolean {
        if (results.isEmpty()) return false
        for (result in results) {
            if (result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pictureSelect?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (mainFragmentNavigator?.onBackPressed() != false) {
            super.onBackPressed()
        }
    }

    override fun fragmentNavigatorOf(id: Int, backNavigation: Boolean): FragmentNavigator {
        return FragmentNavigator(supportFragmentManager, id).apply {
            if (backNavigation) {
                mainFragmentNavigator = this
            }
        }
    }

    infix fun WindowInsetsCompat?.applyTo(view: View) {
        view.updatePadding(
            top = this?.systemWindowInsetTop ?: 0,
            left = this?.systemWindowInsetLeft ?: 0,
            right = this?.systemWindowInsetRight ?: 0,
            bottom = this?.systemWindowInsetBottom ?: 0
        )
    }

    fun <T : View> id(id: Int): FindViewDelegate<T> {
        return FindViewDelegate(id)
    }

    inner class FindViewDelegate<T : View>(val id: Int) {

        private var view: T? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return view ?: findViewById<T>(id).also { view = it }
        }
    }

    fun onClickView(id: Int, setOnClickListener: () -> Unit) {
        findViewById<View>(id).setOnClickListener {
            setOnClickListener()
        }
    }

    override fun showLoading() {
        loadingOverlay.show()
    }

    override fun hideLoading() {
        loadingOverlay.dismiss()
    }

    private inline fun <reified T> Bundle.putObject(data: T?) {
        putString(T::class.java.name, data.toJson())
    }

    private inline fun <reified T> Bundle.getObject(): T? {
        return getString(T::class.java.name)?.fromJson()
    }

}
