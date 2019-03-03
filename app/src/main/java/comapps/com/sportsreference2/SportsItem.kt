package comapps.com.sportsreference2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by me on 2/8/2017.
 */





@Parcelize
data class SportsItem(val name: String = "",
                      val link: String = "",
                      val type: String = "",
                      val sport: String  = "",
                      var firstSeason: String = "",
                      var lastSeason: String = "",
                      var position: String = "",
                      var schoolOrTeam: String = "",
                      var schoolLink: String = "") : Parcelable


