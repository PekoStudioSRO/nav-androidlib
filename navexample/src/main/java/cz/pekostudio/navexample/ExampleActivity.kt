package cz.pekostudio.navexample

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import cz.pekostudio.nav.elements.BaseActivity
import cz.pekostudio.nav.Navigation
import cz.pekostudio.nav.fragments.FragmentNavigator

class ExampleActivity : BaseActivity(R.layout.activity_example) {

    override fun onCreate() {

        val navigator = fragmentNavigatorOf(R.id.content, backNavigation = true)

        findViewById<View>(R.id.fragment1_show).setOnClickListener {
            navigator.show(ExampleFragment::class.java)
        }

        findViewById<View>(R.id.fragment2_show).setOnClickListener {
            navigator.show(ImageFragment::class.java)
        }
    }
}
