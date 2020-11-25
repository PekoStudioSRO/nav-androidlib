package cz.pekostudio.navexample

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import cz.pekostudio.NavConfigData
import cz.pekostudio.nav.elements.BaseFragment

class ImageFragment : BaseFragment(R.layout.fragment_image) {

    init {
        NavConfigData.navigation.fileProvider = "cz.pekostudio.navexample.fileprovider"
    }

    override fun onCreate() {

        findViewById<View>(R.id.select).setOnClickListener {
            pickImageFile {
                findViewById<ImageView>(R.id.image).setImageURI(it.toUri())
            }
        }
    }
}
