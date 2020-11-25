package cz.pekostudio.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import cz.pekostudio.NavConfigData
import cz.pekostudio.nav.R

/**
 * Created by lukasurbanek on 12/1/2017.
 */

class TextDialog internal constructor(
    private val activity: Activity,
    private val title: String,
    private val text: String
) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(NavConfigData.userInterface.dialogDefaultBackgroundResource)
        setContentView(NavConfigData.userInterface.dialogDefaultTextLayout)

        findViewById<TextView>(R.id.title).text = title
        findViewById<TextView>(R.id.text).text = text
        findViewById<TextView>(R.id.ok).setOnClickListener {
            dismiss()
        }
    }
}

public fun Activity.showTextDialog(
    title: String,
    text: String,
    onDismiss: (() -> Unit)? = null
): TextDialog {
    return TextDialog(this, title, text).apply {
        setOnDismissListener { onDismiss?.invoke() }
        show()
    }
}