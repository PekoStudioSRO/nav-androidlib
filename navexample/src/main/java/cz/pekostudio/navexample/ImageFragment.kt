package cz.pekostudio.navexample

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import cz.pekostudio.nav.elements.BaseActivity
import cz.pekostudio.nav.Navigation
import cz.pekostudio.nav.elements.BaseFragment

class ImageFragment : BaseFragment(R.layout.fragment_image) {

    init {
        Navigation.config.fileProvider = "cz.pekostudio.navexample.fileprovider"
    }

    override fun onCreate() {

        findViewById<View>(R.id.select).setOnClickListener {
            pickImage {
                findViewById<ImageView>(R.id.image).setImageBitmap(it)
            }
        }
    }
}
