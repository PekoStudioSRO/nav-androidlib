package cz.pekostudio.nav.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import cz.pekostudio.nav.elements.BaseActivity
import java.util.*

/**
 * Created by Lukas Urbanek on 2019-10-24.
 */
object PermissionsHelper {

    fun checkPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION) break
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    fun createPermissionRequestId(element: BaseActivity): Int {
        var id: Int
        do {
            id = Random().nextInt(1024)
        } while (element.permissionRequests.containsKey(id))
        return id
    }

}
