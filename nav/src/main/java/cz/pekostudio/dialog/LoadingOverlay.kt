package cz.pekostudio.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager.LayoutParams
import cz.pekostudio.NavConfigData
import cz.pekostudio.nav.R

class LoadingOverlay(context: Context) : Dialog(context, R.style.LoadingDialog) {

    override fun show() {
        window?.run {
            super.show()
            setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setFormat(PixelFormat.TRANSLUCENT)
        }
        setContentView(NavConfigData.userInterface.dialogDefaultLoadingLayout)
        setCancelable(false)
    }
}