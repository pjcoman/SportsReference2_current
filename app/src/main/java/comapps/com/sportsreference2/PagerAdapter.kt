package comapps.com.sportsreference2


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by me on 3/21/2017.
 */

internal class PagerAdapter(fm: FragmentManager, private val mNumOfTabs: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> NorthAmericaFragment()
            1 -> WorldFragment()

            else ->
                return NorthAmericaFragment()
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }


}
