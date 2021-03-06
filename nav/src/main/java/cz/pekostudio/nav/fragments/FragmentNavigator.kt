package cz.pekostudio.nav.fragments

import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import cz.pekostudio.nav.R
import cz.pekostudio.nav.elements.BaseFragment
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Lukas Urbanek on 07/05/2020.
 */
class FragmentNavigator(
    private val fragmentManager: FragmentManager,
    private val container: Int
) {

    private var actualFragment: BaseFragment? = null
    private val history = Stack<KClass<out BaseFragment>>()

    fun show(fragmentClass: KClass<out BaseFragment>, back: Boolean = false): BaseFragment? {
        addHistoryEntry(fragmentClass, back)

        fragmentClass.instanceOfClass()?.let { fragment ->
            fragmentManager.beginTransaction().run {
                setReorderingAllowed(true)
                setCustomAnimations(R.anim.fragment_enter, 0)

                val fragmentToShow = fragmentManager.findFragmentByTag(fragmentClass.simpleName) as BaseFragment?

                fragmentManager.fragments.forEach { hide(it) }

                fragmentToShow?.let {
                    show(fragmentToShow)
                    commit()
                    fragment.onNavigated()
                } ?: run {
                    add(container, fragment, fragmentClass.simpleName)
                    commit()
                }
            }
            fragment.parentFragmentNavigator = this
            actualFragment = fragment
            return fragment
        }

        return null
    }

    private fun addHistoryEntry(fragmentClass: KClass<out BaseFragment>,  back: Boolean) {
        if (!back && actualFragment != null) {
            ArrayList<KClass<out BaseFragment>>().run {
                history.forEach {
                    if (it.simpleName == fragmentClass.simpleName) add(it)
                }
                history.removeAll(this)
            }
            actualFragment?.let {
                history.push(it::class)
            }
        }
    }

    private fun KClass<out BaseFragment>.instanceOfClass(): BaseFragment? {
        return try {
            java.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun onBackPressed(): Boolean {
        if (actualFragment?.onBackPressed() != true) return false
        if (history.isEmpty()) return true
        show(history.pop(), true)
        return false
    }

}