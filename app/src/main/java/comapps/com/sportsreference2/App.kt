package comapps.com.sportsreference2

import android.support.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger


/**
 * Created by me on 3/14/2018.
 */

val prefs: Prefs by lazy {
    App.prefs!!
}


class App : MultiDexApplication() {
    companion object {
        var prefs: Prefs? = null

    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        super.onCreate()
    }



}