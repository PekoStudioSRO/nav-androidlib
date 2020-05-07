package cz.pekostudio.nav.elements

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cz.pekostudio.camera.picker.AbstractPictureSelect
import cz.pekostudio.camera.picker.BitmapPictureSelect
import cz.pekostudio.camera.picker.FilePictureSelect
import cz.pekostudio.nav.Navigation
import cz.pekostudio.nav.fragments.FragmentNavigator
import cz.pekostudio.nav.permissions.PermissionsHelper
import java.io.File
import java.lang.IllegalStateException
import kotlin.collections.HashMap

/**
 * Created by Lukas Urbanek on 28/04/2020.
 */
abstract class BaseActivity(private val layout: Int) : AppCompatActivity(),
    NavigationElement {

    internal var permissionRequests: HashMap<Int, () -> Unit> = HashMap()
    private var insets: WindowInsetsCompat? = null

    private var pictureSelect: AbstractPictureSelect<*>? = null

    private var mainFragmentNavigator: FragmentNavigator? = null

    override val elementContext: Context
        get() = this

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        onCreate()
        requestInsets()
    }

    override fun onCreate() {

    }

    fun requestInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { _, insets ->
            this.insets = insets
            onInsetsUpdated(insets)
            insets.consumeSystemWindowInsets()
        }
    }

    open fun onInsetsUpdated(insets: WindowInsetsCompat?) {

    }

    override fun pickImage(onPicked: (bitmap: Bitmap) -> Unit) {
        permissionRequired(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            pictureSelect = BitmapPictureSelect(this,
                Navigation.config.fileProvider ?: throw IllegalStateException("Není registrován file provider")
            ).also {
                it.pickImage(function = onPicked)
            }
        }
    }

    override fun pickImageFile(onPicked: (file: File) -> Unit) {
        permissionRequired(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            pictureSelect = FilePictureSelect(this,
                Navigation.config.fileProvider ?: throw IllegalStateException("Není registrován file provider")
            ).also {
                it.pickImage(function = onPicked)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

}