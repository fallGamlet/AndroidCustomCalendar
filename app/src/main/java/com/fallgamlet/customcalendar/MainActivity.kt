package com.fallgamlet.customcalendar

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity(),
    NavigationFragment.Listener
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(false)
            setHomeButtonEnabled(true)
        }

        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        if (fragment == null) showFragment(NavigationFragment(), false)

        supportFragmentManager.addOnBackStackChangedListener {
            val hasBackStack = supportFragmentManager.backStackEntryCount > 0
            supportActionBar?.setDisplayHomeAsUpEnabled(hasBackStack)
        }
    }

    override fun onNavigationActionActivated(action: String) {
        showFragment(getFragmentForAction(action))
    }

    private fun getFragmentForAction(action: String): Fragment {
        return when (action) {
            NavigationIds.MONTH_VIEW_EXAMPLE_1 -> MonthViewExample1Fragment()
            NavigationIds.MONTH_VIEW_EXAMPLE_2 -> MonthViewExample2Fragment()
            NavigationIds.CALENDAR_VIEW_PAGER_1 -> CalendarViewPagerExample1Fragment()
            NavigationIds.CALENDAR_VIEW_PAGER_2 -> CalendarViewPagerExample2Fragment()
            else -> throw IllegalArgumentException("wrong action ID")
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .also {
                if (addToBackStack) it.addToBackStack(fragment.javaClass.simpleName)
            }
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
