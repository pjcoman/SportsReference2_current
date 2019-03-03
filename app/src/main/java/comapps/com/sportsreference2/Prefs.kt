package comapps.com.sportsreference2

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by me on 3/14/2018.
 */
class Prefs(context: Context) {
    private val PREFS_FILENAME = "comapps.com.sportsreference2.prefs"
    private val SPORTS_ITEM_HISTORY = "sih"


    private val HOCKEYITEMS = "hockey_items"
    private val BASEBALLITEMS = "baseball_items"
    private val FOOTBALLITEMS = "football_items"
    private val BASKETBALLITEMS = "basketball_items"
    private val SOCCERITEMS = "soccer_items"
    private val COLLEGE_BASKETBALLITEMS = "cbb_items"
    private val COLLEGE_FOOTBALLITEMS = "cfb_items"

    private val MLBFAVSPIN = "mlb_spin"
    private val NHLFAVSPIN = "nhl_spin"
    private val NBAFAVSPIN = "nba_spin"
    private val NFLFAVSPIN = "nfl_spin"
    private val TIMER_INT = "timer_integer"
    private val SHOW_INSTRUCTIONS = "show_instructions"
    private val ON_BOTTOM = "on_bottom"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var sportsItemHistory: String
        get() = prefs.getString(SPORTS_ITEM_HISTORY, "[{\"name\":\"search history\"}]")
        set(value) = prefs.edit().putString(SPORTS_ITEM_HISTORY, value).apply()


    var hockeyItems: String
        get() = prefs.getString(HOCKEYITEMS, "")
        set(value) = prefs.edit().putString(HOCKEYITEMS, value).apply()
    var baseballItems: String
        get() = prefs.getString(BASEBALLITEMS, "")
        set(value) = prefs.edit().putString(BASEBALLITEMS, value).apply()
    var footballItems: String
        get() = prefs.getString(FOOTBALLITEMS, "")
        set(value) = prefs.edit().putString(FOOTBALLITEMS, value).apply()
    var basketballItems: String
        get() = prefs.getString(BASKETBALLITEMS, "")
        set(value) = prefs.edit().putString(BASKETBALLITEMS, value).apply()
    var soccerItems: String
        get() = prefs.getString(SOCCERITEMS, "")
        set(value) = prefs.edit().putString(SOCCERITEMS, value).apply()
    var cbbItems: String
        get() = prefs.getString(COLLEGE_BASKETBALLITEMS, "")
        set(value) = prefs.edit().putString(COLLEGE_BASKETBALLITEMS, value).apply()
    var cfbItems: String
        get() = prefs.getString(COLLEGE_FOOTBALLITEMS, "")
        set(value) = prefs.edit().putString(COLLEGE_FOOTBALLITEMS, value).apply()


    var mlbfav: Int
        get() = prefs.getInt(MLBFAVSPIN, 0)
        set(value) = prefs.edit().putInt(MLBFAVSPIN, value).apply()

    var nhlfav: Int
        get() = prefs.getInt(NHLFAVSPIN, 0)
        set(value) = prefs.edit().putInt(NHLFAVSPIN, value).apply()

    var nbafav: Int
        get() = prefs.getInt(NBAFAVSPIN, 0)
        set(value) = prefs.edit().putInt(NBAFAVSPIN, value).apply()

    var nflfav: Int
        get() = prefs.getInt(NFLFAVSPIN, 0)
        set(value) = prefs.edit().putInt(NFLFAVSPIN, value).apply()

    var timerInt: Int
        get() = prefs.getInt(TIMER_INT, 4)
        set(value) = prefs.edit().putInt(TIMER_INT, value).apply()

    var showInstructions: Boolean
        get() = prefs.getBoolean(SHOW_INSTRUCTIONS, true)
        set(value) = prefs.edit().putBoolean(SHOW_INSTRUCTIONS, value).apply()

    var searchBarBottom: Boolean
        get() = prefs.getBoolean(ON_BOTTOM, true)
        set(value) = prefs.edit().putBoolean(ON_BOTTOM, value).apply()
}