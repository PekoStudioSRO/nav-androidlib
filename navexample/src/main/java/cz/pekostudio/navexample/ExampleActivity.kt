package cz.pekostudio.navexample

import android.view.View
import androidx.core.view.WindowInsetsCompat
import cz.pekostudio.NavConfig
import cz.pekostudio.api.api
import cz.pekostudio.api.get
import cz.pekostudio.nav.ActivityConfig
import cz.pekostudio.nav.elements.BaseActivity
import cz.pekostudio.navexample.api.Endpoints

class ExampleActivity : BaseActivity(
    R.layout.activity_example,
    ActivityConfig.TRANSPARENT_LIGHT
) {

    private val fragment1Show: View by id(R.id.fragment1_show)
    private val fragment2Show: View by id(R.id.fragment2_show)

    override fun onCreate() {

        val navigator = fragmentNavigatorOf(R.id.content, backNavigation = true)

        navigator.show(ImageFragment::class)

        fragment1Show.setOnClickListener {
            navigator.show(ExampleFragment::class)
        }

        fragment2Show.setOnClickListener {
            navigator.show(ImageFragment::class)
        }

    }

    override fun onInsetsUpdated(insets: WindowInsetsCompat?) {
        insets applyTo findViewById(R.id.parent)
    }

}
