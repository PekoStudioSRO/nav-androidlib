package cz.pekostudio.navexample

import android.util.Log
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import cz.pekostudio.nav.elements.BaseFragment

class ExampleFragment : BaseFragment(R.layout.fragment_example) {

    private val text: TextView by id(R.id.text)

    override fun onCreate() {
        parentFragmentNavigator?.onBackPressed()
    }

    override fun onInsetsUpdated(insets: WindowInsetsCompat?) {
        text.text = insets?.systemWindowInsetBottom.toString()
    }
}
