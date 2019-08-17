package comapps.com.sportsreference2


import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.sportsitemslayout.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import rx.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.text.Normalizer
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.concurrent.thread


private const val DEBUG_TAG = "Gestures"

fun deAccent(str: String): String {
    val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(nfdNormalizedString).replaceAll("")
}

fun Intent.putParcel(key: String = "parcel_key", parcel: Parcelable) {
    val bundle = Bundle()
    bundle.putParcelable(key, parcel)
    this.putExtra("parcel_bundle", bundle)
}

fun <T : Parcelable> Intent.getParcel(key: String = "parcel_key"): T? {
    return this.getBundleExtra("parcel_bundle")?.getParcelable(key)
}


/**
 * Created by me on 4/21/2015.
 */
class SportsItemActivityKotlin : AppCompatActivity(), GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {


    private val TAG = "SIAKOTLIN"


    private var sportsItemListDOCS = mutableListOf<SportsItem>()
    private var sportsItemListDBASE_FILTERED = mutableListOf<SportsItem>()
    private var sportsItemListDBASE = mutableListOf<SportsItem>()

    private lateinit var sportsItemRecyclerView: RecyclerView
    private lateinit var sportsItemAdapter: SportsItemAdapter
    private lateinit var sportsList: List<Any>
    private lateinit var clProgressLayout: ConstraintLayout
    private lateinit var cLayout: ConstraintLayout
    private lateinit var searchText: EditText
    private lateinit var percentProgress: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var progressValueString: String

    private var sport = ""
    private var collegeIndex: String = "1"
    private var currentCollegeIndex: String = "1"

    private var i: Int = 0
    private var j: Int = 0


    private var dbRef: DatabaseReference? = null
    private var docRef: DocumentReference? = null
    private var dBaseAddress: String? = null
    private var sportsArrayPos: Int = 0
    private var extras: Bundle? = null
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null

    private var firestoreListener: ListenerRegistration? = null

    private var toastNumOfCharShown = false

    private lateinit var mDetector: GestureDetectorCompat

    private var reSearch: Boolean = false


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        if (prefs.searchBarBottom) {
            setContentView(R.layout.sportsitemslayoutb)
            sportsItemRecyclerView = findViewById(R.id.sportsItemRecyclerView)
            sportsItemRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL,
                    true)

        } else {
            setContentView(R.layout.sportsitemslayout)
            sportsItemRecyclerView = findViewById(R.id.sportsItemRecyclerView)
            sportsItemRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL,
                    false)
        }



        mDetector = GestureDetectorCompat(this, this)
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this)








        searchText = findViewById(R.id.searchText)
        clProgressLayout = findViewById(R.id.cl_Progress)
        cLayout = findViewById(R.id.constraintlayoutsi)

//        sportsItemRecyclerView.setHasFixedSize(true)



        progressBar = findViewById(R.id.progressSpinner)

        percentProgress = findViewById(R.id.textViewProgressPercentage)

        //    sportsArray = resources.getStringArray(comapps.com.sportsreference2.R.array.sports)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // setup the window


        sportsList = resources.getStringArray(R.array.sports).toList()

        //  ******************* BUNDLE ****************************
        extras = intent.extras
        sport = extras?.getString("sport").toString()
        sportsArrayPos = sportsList.indexOf(sport)
        dBaseAddress = changeSport(sportsArrayPos, "1")
        println("$TAG sport $sport pos $sportsArrayPos")







        docRef = FirebaseFirestore.getInstance().document("searchData/${sportsList[sportsArrayPos]}")



        try {
            sportsItemAdapter = SportsItemAdapter(sportsItemListDOCS)
            sportsItemRecyclerView.adapter = sportsItemAdapter

        } catch (e: Exception) {

        }


        val window = window


        if (resources.configuration.orientation <= 2) {

            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }






        searchText.isEnabled = true
        searchText.isCursorVisible = false

        reSearch = rand(0, 5) >= 5

        when {
            sport.contains("college") -> searchText.hint = getString(R.string.edittext_hint)
            sport.contains("soccer") -> searchText.hint = getString(R.string.edittext_hint3)
            else ->


                if (reSearch) {
                    searchText.hint = getString(R.string.edittext_dt)
                } else {
                    searchText.hint = getString(R.string.edittext_hint2)
                }

        }


        val bar = supportActionBar

        bar?.title = "SportsItem Reference"

        searchText.setOnLongClickListener {
            // TODO Auto-generated method stub

            try {
                firestoreListener!!.remove()
            } catch (e: Exception) {
            }


            searchText.setText("")

            //    sportsItemRecyclerView.invalidate()
            sportsItemListDOCS.clear()
            notifyAdapter()

            sportsItemRecyclerView.visibility = View.GONE




            true
        }



        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)


//****************************************************************************


        if (reSearch) {

            if (sport == "baseball") {


                val debutUrl = "https://www.baseball-reference.com/leagues/MLB/$year-debuts.shtml"


                //    println("$TAG debut Url $debutUrl")


                val mapDebuts = mutableMapOf<String, String>()



                thread {


                    try {
                        //Get Document object after parsing the html from given url.
                        val document = Jsoup.connect(debutUrl).get()

                        //Get links from document object.
                        val links = document.select("a[href]")

                        //Iterate links and print link attributes.


                        for (link in links) {
                            if (link.attr("href").contains("players")) {
                                /*   println("$TAG Link: " + link.attr("href"))
                                   println("$TAG ID: " + link.attr("href").replace("/", "")
                                           .replace(".shtml", "")
                                           .replace("'", "")
                                           .replace(".","")
                                           .replace("`", ""))
                                   println("")
           */
                                mapDebuts[link.text()] = link.attr("href")

                            }


                        }


                        //       println("$TAG baseball $mapDebuts")


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }


                }


                val refForLoad = FirebaseDatabase.getInstance(changeSport(sportsArrayPos, "1")).reference


                mapDebuts.forEach {


                    val id = it.value.replace("/", "")
                            .replace(".shtml", "")
                            .replace("'", "")
                            .replace(".", "")
                            .replace("`", "")


                    val hashMapAdd = HashMap<String, String>()



                    hashMapAdd["name"] = it.key
                    hashMapAdd["link"] = it.value
                    hashMapAdd["sport"] = "baseball"
                    hashMapAdd["type"] = "player"
                    hashMapAdd["firstSeason"] = year.toString()


                    //   println("$TAG id $id $hashMapAdd")


                    //     refForLoad.child(id).setValue(hashMapAdd)

                    refForLoad.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value == null) {


                                //           println("$TAG item $p0")
                                refForLoad.child(id).setValue(hashMapAdd)


                            }


                        }


                    })
                }


            }

            if (sport == "hockey") {


                year++


                val debutUrl = "https://www.hockey-reference.com/leagues/NHL_$year" + "_debut.html"

                //    println("$TAG debut Url $debutUrl")


                val mapDebuts = mutableMapOf<String, String>()



                thread {

                    val document: Document



                    try {
                        //Get Document object after parsing the html from given url.
                        document = Jsoup.connect(debutUrl).get()

                        //Get links from document object.
                        val links = document.select("a[href]")

                        //Iterate links and print link attributes.


                        for (link in links) {
                            if (link.attr("href").contains("players")) {
                                /*   println("$TAG Link: " + link.attr("href"))
                                   println("$TAG ID: " + link.attr("href").replace("/", "")
                                           .replace(".shtml", "")
                                           .replace("'", "")
                                           .replace(".","")
                                           .replace("`", ""))
                                   println("")
           */
                                mapDebuts[link.text()] = link.attr("href")

                            }


                        }

                        //           println("$TAG hockey $mapDebuts")


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }


                }


                val refForLoad = FirebaseDatabase.getInstance(changeSport(sportsArrayPos, "1")).reference


                mapDebuts.forEach {


                    val id = it.value.replace("/", "")
                            .replace(".html", "")
                            .replace("'", "")
                            .replace(".", "")
                            .replace("`", "")
                            .toUpperCase()


                    val hashMapAdd = HashMap<String, String>()



                    hashMapAdd["name"] = it.key
                    hashMapAdd["link"] = it.value
                    hashMapAdd["sport"] = "hockey"
                    hashMapAdd["type"] = "player"
                    hashMapAdd["firstSeason"] = year.toString()


                    //    println("$TAG id $id $hashMapAdd")


                    //     refForLoad.child(id).setValue(hashMapAdd)

                    refForLoad.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value == null) {


                                //   println("$TAG item $p0")
                                refForLoad.child(id).setValue(hashMapAdd)


                            }


                        }


                    })
                }


            }

            if (sport == "basketball") {


                year++


                val debutUrl = "https://www.basketball-reference.com/leagues/NBA_$year" + "_rookies.html"


                //   println("$TAG debut Url $debutUrl")


                val mapDebuts = mutableMapOf<String, String>()



                thread {

                    val document: Document


                    try {
                        //Get Document object after parsing the html from given url.
                        document = Jsoup.connect(debutUrl).get()

                        //Get links from document object.
                        val links = document.select("a[href]")

                        //Iterate links and print link attributes.


                        for (link in links) {
                            if (link.attr("href").contains("players")) {
                                /*   println("$TAG Link: " + link.attr("href"))
                                   println("$TAG ID: " + link.attr("href").replace("/", "")
                                           .replace(".shtml", "")
                                           .replace("'", "")
                                           .replace(".","")
                                           .replace("`", ""))
                                   println("")
           */
                                mapDebuts[link.text()] = link.attr("href")

                            }


                        }

                        //     println("$TAG basketball $mapDebuts")


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }


                }

                val refForLoad = FirebaseDatabase.getInstance(changeSport(sportsArrayPos, "1")).reference


                mapDebuts.forEach {


                    val id = it.value.replace("/players", "")
                            .replace("/", "")
                            .replace(".html", "")
                            .replace("'", "")
                            .replace(".", "")
                            .replace("`", "")
                            .toUpperCase()


                    val hashMapAdd = HashMap<String, String>()



                    hashMapAdd["name"] = it.key
                    hashMapAdd["link"] = it.value
                    hashMapAdd["sport"] = "basketball"
                    hashMapAdd["type"] = "player"
                    hashMapAdd["firstSeason"] = year.toString()


                    //    println("$TAG id $id $hashMapAdd")


                    //     refForLoad.child(id).setValue(hashMapAdd)

                    refForLoad.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value == null) {


                                //    println("$TAG item $p0")
                                refForLoad.child(id).setValue(hashMapAdd)


                            }


                        }


                    })
                }


            }

            if (sport == "football") {


                val debutUrl = "https://www.pro-football-reference.com/"
                println("$TAG debut Url $debutUrl")


                var mapDebuts = mutableMapOf<String, String>()

                var refForLoad = FirebaseDatabase.getInstance(changeSport(sportsArrayPos, "1")).reference



                thread {

                    val document: Document




                    try {
                        //Get Document object after parsing the html from given url.
                        document = Jsoup.connect(debutUrl).get()

                        //Get links from document object.

                        val elements = document.getElementById("players")

                        val playerCat = elements.allElements


                        var index = 0
                        var debuts = false

                        for (cat in playerCat) {


                            if (cat.text().contains("Memoriam")) {
                                debuts = false
                            }


                            if (debuts) {


                                println("$TAG $index ${cat.text()} ${cat.attr("href")}")

                                val hashMapAdd = HashMap<String, String?>()



                                hashMapAdd["name"] = cat.text()
                                hashMapAdd["link"] = cat.attr("href")
                                hashMapAdd["sport"] = "football"
                                hashMapAdd["type"] = "player"
                                hashMapAdd["firstSeason"] = year.toString()


                                val id = cat.attr("href").replace("/", "")
                                        .replace(".htm", "")
                                        .replace("'", "")
                                        .replace(".", "")
                                        .replace("`", "")
                                        .replace("players", "p_")
                                        .toUpperCase()


                                println("$TAG hashmap $id $hashMapAdd")


                                //     refForLoad.child(id).setValue(hashMapAdd)

                                refForLoad.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.value == null) {


                                            println("$TAG item $p0")
                                            refForLoad.child(id).setValue(hashMapAdd)


                                        }


                                    }


                                })


                            }

                            if (cat.text() == "Recent Debuts") {
                                debuts = true
                            }








                            index++


                        }


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }


            }
        }


//******************************************************************************

        var searchTimer: Long = prefs.timerInt.toLong()
        val auto8 = prefs.checkBoxAuto8



        RxTextView.afterTextChangeEvents(searchText)


                .debounce(searchTimer * 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { tvChangeEvent ->


                    println("$TAG stopped typing")


                    val s = tvChangeEvent.view().text.toString().toLowerCase()



                    if ( s.length >= 3 ) {

                        sportsItemRecyclerView.visibility = View.VISIBLE



                        sportsItemListDOCS.clear()


                        if (sport.contains("college")) {


                            when {
                                s[0] in 'a'..'c' -> collegeIndex = "1"
                                s[0] in 'd'..'h' -> collegeIndex = "2"
                                s[0] in 'i'..'k' -> collegeIndex = "3"
                                s[0] in 'l'..'q' -> collegeIndex = "4"
                                s[0] > 'q' -> collegeIndex = "5"
                            }




                            if (currentCollegeIndex != collegeIndex) {

                                when (sport) {
                                    "college_football" ->
                                        dBaseAddress = changeSport(4, collegeIndex)
                                    "college_basketball" ->
                                        dBaseAddress = changeSport(5, collegeIndex)
                                }

                            }






                            currentCollegeIndex = collegeIndex


                        }






                        this.queryDatabase(deAccent(s.toLowerCase().trim()), dBaseAddress!!)




                        try {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    } else if (!toastNumOfCharShown && s.length > 1) {


                        val toast = Toast.makeText(this, "At least 3 characters", Toast.LENGTH_SHORT)

                        toast.setGravity(Gravity.CENTER, 0, -300)
                        toast.show()


                        toastNumOfCharShown = true


                    }


                }


    }

    /*  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
          (swipe as Swipe).dispatchTouchEvent(event)
          return super.dispatchTouchEvent(event)
      }
  */

    private fun queryDatabase(query: String, address: String) {


        val colPath: String


        val colPathInit = query.replace(" ", "")


        colPath = if (colPathInit.length > 8) {

            colPathInit.substring(0, 8)

        } else {

            colPathInit

        }

        /*try {
            colPath = colPathInit.substring(0, 8)
            println("$TAG SportsItem colPath ----> $colPath")
        } catch (e: Exception) {
            colPath = colPathInit.substring(0, 3) + "_"
            println("$TAG SportsItem colPath ----> $colPath")
        }
*/


        println("$TAG query, colpath, sport, address ----> $query, $colPath, $sport, $address")




        docRef!!.collection(colPath)
                .get()
                .addOnCompleteListener { it ->


                    sportsItemListDOCS.clear()

                    var links: ArrayList<String> = arrayListOf()


                    it.result?.forEach {

                        val firestoreObject = it.toObject(SportsItem::class.java)


                        if ((deAccent(firestoreObject.name).toLowerCase()).contains(query)
                                && (firestoreObject.sport) == sport) {


                            if (!sportsItemListDOCS.contains(firestoreObject) &&
                                    !links.contains(firestoreObject.link)) {

                                sportsItemListDOCS.add(firestoreObject)
                            }
                            links.add(firestoreObject.link)

                        }

                        sportsItemListDOCS.sortedWith(compareBy({ it.firstSeason }, { it.name }))


                    }



                    progressVisibilityToggle(false)



                    notifyAdapter()


                    reSearch = rand(0, 8) == 0

                    var itemID = ""


                    println("$TAG research size fsize----> $reSearch ${sportsItemListDOCS.size}")

                    if (sportsItemListDOCS.size == 0 || reSearch) {


                        docRef!!.collection(colPath).get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        percentProgress.text = "0%"

                                        progressVisibilityToggle(true)


                                        //       sportsItemList.forEach { println("$TAG $it from docs") }


                                        dbRef = FirebaseDatabase.getInstance(address).reference
                                        dbRef!!.addListenerForSingleValueEvent(object : ValueEventListener {

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                println("$TAG databaseError.message")
                                            }

                                            override fun onDataChange(p0: DataSnapshot) {


                                                var count = 0






                                                if (sportsItemListDBASE.isNotEmpty()) {


                                                    sportsItemListDBASE_FILTERED.clear()


                                                    sportsItemListDBASE.forEach {


                                                        if (deAccent(it.name.toLowerCase()).contains(query)) {


                                                            sportsItemListDBASE_FILTERED.add(it)


                                                        }



                                                        count++

                                                        if (count % 4000 == 0) {
                                                            println("$TAG local count is $count")
                                                        }


                                                    }


                                                } else {


                                                    sportsItemListDBASE_FILTERED.clear()
                                                    /* sportsItemListDBASE.clear()*/





                                                    p0.children.forEach {

                                                        val item = it.getValue(SportsItem::class.java)


                                                        sportsItemListDBASE.add(item!!)


                                                        if (item.name.toLowerCase().contains(query)) {


                                                            sportsItemListDBASE_FILTERED.add(item)


                                                        }


                                                        count++

                                                        if (count % 4000 == 0) {
                                                            println("$TAG count is $count")
                                                        }

                                                    }


                                                    /* val gson = Gson()
                                                     val itemType = object : TypeToken<List<SportsItem>>() {}.type
                                                     when (sport) {
                                                         "hockey" -> sportsItemListDBASE = gson.fromJson<List<SportsItem>>(prefs.hockeyItems, itemType).toMutableList()
                                                         "baseball" -> sportsItemListDBASE = gson.fromJson<List<SportsItem>>(prefs.baseballItems, itemType).toMutableList()
                                                         "football" -> sportsItemListDBASE = gson.fromJson<List<SportsItem>>(prefs.footballItems, itemType).toMutableList()
                                                         "basketball" -> sportsItemListDBASE = gson.fromJson<List<SportsItem>>(prefs.basketballItems, itemType).toMutableList()
                                                         "soccer" -> sportsItemListDBASE = gson.fromJson<List<SportsItem>>(prefs.soccerItems, itemType).toMutableList()
                                                         "college_basketball" -> sportsItemListDBASE = gson.fromJson<List<SportsItem>>(prefs.cbbItems, itemType).toMutableList()
                                                         "college_football" -> sportsItemListDBASE = gson.fromJson<List<SportsItem>>(prefs.cfbItems, itemType).toMutableList()
                                                     }


         */


/*

                                            val gs = Gson()
                                            val sportsItemListJsonString = gs.toJson(sportsItemListDBASE)

                                            when (sport) {
                                                "hockey" -> prefs.hockeyItems = sportsItemListJsonString
                                                "baseball" -> prefs.baseballItems = sportsItemListJsonString
                                                "football" -> prefs.footballItems = sportsItemListJsonString
                                                "basketball" -> prefs.basketballItems = sportsItemListJsonString
                                                "soccer" -> prefs.soccerItems = sportsItemListJsonString
                                                "college_basketball" -> prefs.cbbItems = sportsItemListJsonString
                                                "college_football" -> prefs.cfbItems = sportsItemListJsonString


                                            }

*/


                                                }





                                                sportsItemListDBASE_FILTERED.sortedWith(compareBy({ it.firstSeason }, { it.name }))







                                                println("$TAG docs,dbase ${sportsItemListDOCS.size},${sportsItemListDBASE_FILTERED.size}")








                                                if (sportsItemListDOCS.size < sportsItemListDBASE_FILTERED.size) {


                                                    runOnUiThread {
                                                        progressVisibilityToggle(true)
                                                    }





                                                    i = 0
                                                    j = sportsItemListDOCS.size

                                                    sportsItemListDBASE_FILTERED.forEach {


                                                        var position = it.position
                                                        var firstSeason = it.firstSeason
                                                        var lastSeason = it.lastSeason
                                                        var schoolOrTeam = it.schoolOrTeam
                                                        var schoolLink = it.schoolLink

                                                        println("$TAG position $position")
                                                        println("$TAG firstSeason $firstSeason")
                                                        println("$TAG lastSeason $lastSeason")
                                                        println("$TAG schoolOrTeam $schoolOrTeam")
                                                        println("$TAG schoolLink $schoolLink")


                                                        var linkToScrape = ""






                                                        thread {


                                                            //************************************************scrape *******************************


                                                            if (!it.link.contains("www")) {

                                                                linkToScrape = when {
                                                                    sport == "baseball" ->
                                                                        "https://www.baseball-reference.com" + it.link
                                                                    sport == "football" ->
                                                                        "https://www.pro-football-reference.com" + it.link
                                                                    sport == "basketball" ->
                                                                        "https://www.basketball-reference.com" + it.link
                                                                    sport == "hockey" ->
                                                                        "https://www.hockey-reference.com" + it.link
                                                                    sport == "college_basketball" ->
                                                                        "https://www.sports-reference.com" + it.link
                                                                    sport == "college_football" ->
                                                                        "https://www.sports-reference.com" + it.link
                                                                    sport.contains("soccer") ->
                                                                        "https://www.fbref.com" + it.link
                                                                    else -> ""
                                                                }

                                                            }






                                                            try {


                                                                Jsoup.connect(linkToScrape)
                                                                        .get().run {
                                                                            //   println("$TAG $title")

                                                                            select("div.media-item").forEachIndexed { index, element ->
                                                                                val titleAnchor = element.select("h3 a")
                                                                                val title = titleAnchor.text()
                                                                                val url = titleAnchor.attr("href")
                                                                                //3. Dumping Search Index, Title and URL on the stdout.
                                                                                println("$index. $title ($url) $element")
                                                                            }






                                                                            if (position.isEmpty()) {

                                                                                select("p:contains(Position:)")
                                                                                        .forEachIndexed { index, element ->

                                                                                            position = element.text().replace("Position:", "")
                                                                                            position = position.trim()


                                                                                        }
                                                                            }



                                                                            if (firstSeason.isEmpty()) {

                                                                                select("p:matches(Debut:)").forEachIndexed { index, element ->

                                                                                    val debutSeason = element.text().replace("Debut: ", "")
                                                                                    val debutSeasonSplit = debutSeason.split(",")

                                                                                    val debutForDocSplit = debutSeasonSplit[1].split(" ")

                                                                                    firstSeason = debutForDocSplit[1].trim()

                                                                                    println("$TAG firstSeason $firstSeason")


                                                                                }
                                                                            }

                                                                            if (lastSeason.isEmpty()) {

                                                                                select("p:matches(Last Game:)")
                                                                                        .forEachIndexed { index, element ->

                                                                                            println("$TAG lastSeason element ${element.text()}")

                                                                                            val serviceTime = element.text()
                                                                                            val serviceTimeSplit = serviceTime.split(",", "(")
                                                                                            try {
                                                                                                lastSeason = serviceTimeSplit[1].trim()
                                                                                            } catch (e: Exception) {
                                                                                            }

                                                                                            println("$TAG lastSeason $lastSeason")


                                                                                        }
                                                                            }

                                                                            if (schoolOrTeam.isEmpty()) {

                                                                                select("p:matches(College)").forEachIndexed { index, element ->


                                                                                    val schoolOrTeamData = element.select("a").toString()
                                                                                    val schoolOrTeamDataSplit = schoolOrTeamData.split("<")


                                                                                    schoolOrTeamDataSplit.forEachIndexed { index, s ->
                                                                                        println("$TAG schoolsplit $s $index")

                                                                                        if (s.contains("/schools/")
                                                                                                || s.contains("colleges")) {
                                                                                            val tempSplit = s.split(">")
                                                                                            schoolOrTeam = tempSplit[1]
                                                                                        }


                                                                                    }

                                                                                    if (!schoolLink.contains("/schools/")) {
                                                                                        schoolOrTeamDataSplit.forEachIndexed { index, s ->
                                                                                            println("$TAG schoolsplit $s $index")

                                                                                            try {
                                                                                                if (s.contains("href") && s.contains("reference.com/c")) {
                                                                                                    val tempLinkSplit = s.split("\"")
                                                                                                    schoolLink = tempLinkSplit[1]
                                                                                                    println("$TAG schoolsplit link $schoolLink")
                                                                                                }


                                                                                            } catch (e: Exception) {

                                                                                                println("$TAG no link")
                                                                                                schoolLink = ""


                                                                                            }

                                                                                        }
                                                                                    }
                                                                                }
                                                                            }


                                                                        }
                                                            } catch (e: Exception) {

                                                                println("$TAG jSoup failed $linkToScrape")

                                                            }


                                                            val hashMap = HashMap<String, Any?>()

                                                            println("$TAG sportsItem-$j preHash $it")


                                                            hashMap["firstSeason"] = firstSeason
                                                            hashMap["lastSeason"] = lastSeason
                                                            hashMap["schoolOrTeam"] = schoolOrTeam
                                                            hashMap["schoolLink"] = schoolLink
                                                            hashMap["position"] = position

                                                            hashMap["sport"] = it.sport
                                                            hashMap["name"] = it.name
                                                            hashMap["link"] = it.link
                                                            hashMap["type"] = it.type

                                                            var key = it.link.replace(".", "")
                                                            key = key.replace("/", "")
                                                            key = key.replace("shtml", "")
                                                            key = key.replace("html", "")
                                                            key = key.replace("htm", "")
                                                            key = key.replace("managers", "m_")
                                                            key = key.replace("players", "p_")
                                                            key = key.replace("teams", "t_")


                                                            var itemUpdate = dbRef!!.child(key)
                                                            itemUpdate.setValue(hashMap)


                                                            runOnUiThread {
                                                                progressVisibilityToggle(true)
                                                            }



                                                            docRef!!
                                                                    .collection(colPath)
                                                                    .document(key)
                                                                    .set(hashMap as Map<String, Any?>, SetOptions.merge())
                                                                    .addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {

                                                                            //                   println("$TAG sportsItem-$i postHash $hashMap added/updated")

                                                                            j++


                                                                            val percentComplete =
                                                                                    ((j.toDouble() / (sportsItemListDBASE_FILTERED.size).toDouble()
                                                                                            * 100))
                                                                            var percentCompleteInt = percentComplete.toInt()
                                                                            println("$TAG progress% $j $percentCompleteInt%")


                                                                            val remainder = percentCompleteInt % 5

                                                                            progressValueString = "$percentCompleteInt%"


                                                                            //                      println("$TAG progressString $j $progressValueString")


                                                                            runOnUiThread {
                                                                                if (remainder == 0) {


                                                                                    progressValueString = percentCompleteInt.toString() + "%"
                                                                                    percentProgress.text = progressValueString
                                                                                    progressBar.progress = percentCompleteInt


                                                                                }

                                                                                if (percentCompleteInt > 90) {


                                                                                    percentProgress.text = "0%"
                                                                                    progressBar.progress = 0
                                                                                    j = 0

                                                                                    progressVisibilityToggle(false)
                                                                                    searchText.text == null

                                                                                }
                                                                            }


                                                                        } else {
                                                                            println("$TAG $hashMap add/update failed")
                                                                        }


                                                                    }


                                                        }
                                                        //*************scrape finished *********************


                                                    }


                                                    //***********************************doAsync finished***********************************


                                                } else {

                                                    progressVisibilityToggle(false)

                                                }










                                                if (sportsItemListDBASE_FILTERED.isEmpty()) {


                                                    val searchfinal = query.replace(" ".toRegex(), "+")
                                                    val httpSearch = when (sport) {
                                                        "baseball" -> "https://www.baseball-reference.com/search/search.fcgi?hint=&search=$searchfinal&pid=&idx="
                                                        "football" -> "https://www.pro-football-reference.com/search/search.fcgi?hint=$searchfinal&search=$searchfinal&pid="
                                                        "basketball" -> "https://www.basketball-reference.com/search/search.fcgi?hint=$searchfinal&search=$searchfinal&pid="
                                                        "hockey" -> "https://www.hockey-reference.com/search/search.fcgi?hint=$searchfinal&search=$searchfinal&pid="
                                                        "college_basketball" -> "https://www.sports-reference.com/cbb/search/search.fcgi?hint=$searchfinal&search=$searchfinal&pid="
                                                        "college_football" -> "https://www.sports-reference.com/cfb/search/search.fcgi?hint=$searchfinal&search=$searchfinal&pid="
                                                        "soccer" -> "https://fbref.com/search/search.fcgi?hint=&search=$searchfinal&pid=&idx="
                                                        else -> ""
                                                    }

                                                    searchText.text = null


                                                    val intent = Intent(this@SportsItemActivityKotlin, WebView::class.java)
                                                    intent.putExtra("linkSearchText", httpSearch)
                                                    intent.putExtra("sentFrom", "ACTIVITY")
                                                    startActivity(intent)


                                                } else {


                                                    firestoreListener = docRef!!.collection(colPath)
                                                            .addSnapshotListener(EventListener { documentSnapshots, e ->
                                                                if (e != null) {
                                                                    Log.e(TAG, "Listen failed!", e)
                                                                    return@EventListener
                                                                }


                                                                sportsItemListDOCS.clear()



                                                                documentSnapshots?.forEach {

                                                                    if ((deAccent(it.toObject(SportsItem::class.java).name).toLowerCase()).contains(query)
                                                                            && (it.toObject(SportsItem::class.java).sport) == sport) {
                                                                        sportsItemListDOCS.add(it.toObject(SportsItem::class.java))

                                                                    }

                                                                }


                                                                notifyAdapter()


                                                            })


                                                }


                                            }
                                        })


                                    }


                                }

                    }


                }


    }

    private fun changeSport(sportArrayMember: Int, index: String): String {

        when (sportArrayMember) {
            0 -> {
                try {
                    window.setBackgroundDrawableResource(R.drawable.wallpaper_bb)
                } catch (e: Exception) {
                }
                dBaseAddress = "https://sportsreference2-bb.firebaseio.com/"
                sport = "baseball"

            }
            1 -> {
                try {
                    window.setBackgroundDrawableResource(R.drawable.wallpaper_fb)
                } catch (e: Exception) {
                }
                dBaseAddress = "https://sportsreference2-fb.firebaseio.com/"
                sport = "football"
            }
            2 -> {
                try {
                    window.setBackgroundDrawableResource(R.drawable.wallpaper_h)
                } catch (e: Exception) {
                }
                dBaseAddress = "https://sportsreference2-h.firebaseio.com/"
                sport = "hockey"
            }
            3 -> {
                try {
                    window.setBackgroundDrawableResource(R.drawable.wallpaper_bkb)
                } catch (e: Exception) {
                }
                dBaseAddress = "https://sportsreference2-bkb.firebaseio.com/"
                sport = "basketball"
            }
            4 -> {
                try {
                    window.setBackgroundDrawableResource(R.drawable.wallpaper_cfb)
                } catch (e: Exception) {
                }
                dBaseAddress = "https://sportsreference2-cfb$index.firebaseio.com/"
                sport = "college_football"
            }
            5 -> {
                try {
                    window.setBackgroundDrawableResource(R.drawable.wallpaper_cbb)
                } catch (e: Exception) {
                }
                dBaseAddress = "https://sportsreference2-cbb$index.firebaseio.com/"
                sport = "college_basketball"
            }
            6 -> {
                try {
                    window.setBackgroundDrawableResource(R.drawable.soccerbg)
                } catch (e: Exception) {
                }
                dBaseAddress = "https://sportsreference2-soccer.firebaseio.com/"
                sport = "soccer"
            }


        }



        sportsItemListDBASE.clear()


        return dBaseAddress!!


    }

    private fun notifyAdapter() {
        this.runOnUiThread {
            sportsItemRecyclerView.adapter = sportsItemAdapter
            sportsItemAdapter.notifyDataSetChanged()

        }
    }

    private fun progressVisibilityToggle(barVisibility: Boolean) {


        if (barVisibility) {
            cl_Progress.visibility = View.VISIBLE
        } else {
            cl_Progress.visibility = View.GONE
        }

       /* if (percentVisibility) {
            percentProgress.visibility = View.VISIBLE
        } else {
            percentProgress.visibility = View.GONE
        }


        this.runOnUiThread {

            if (percentProgress.text == "0%" || percentProgress.text == "") {
                cl_Progress.visibility = android.view.View.GONE
            } else {
                cl_Progress.visibility = android.view.View.VISIBLE
            }

        }*/
    }


    private fun rand(s: Int, e: Int) = Random().nextInt(e + 1 - s) + s


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(event: MotionEvent): Boolean {
        Log.d(DEBUG_TAG, "onDown: $event")
        return true
    }

    override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
    ): Boolean {
        Log.d(DEBUG_TAG, "onFling: $event1 $event2")
        return true
    }

    override fun onLongPress(event: MotionEvent) {
        Log.d(DEBUG_TAG, "onLongPress: $event")
    }

    override fun onScroll(
            event1: MotionEvent,
            event2: MotionEvent,
            distanceX: Float,
            distanceY: Float
    ): Boolean {
        Log.d(DEBUG_TAG, "onScroll: $event1 $event2")
        return true
    }

    override fun onShowPress(event: MotionEvent) {
        Log.d(DEBUG_TAG, "onShowPress: $event")
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        Log.d(DEBUG_TAG, "onSingleTapUp: $event")
        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        Log.d(DEBUG_TAG, "onDoubleTap: $event")
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: $event")

        if (Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            val intent = Intent(this.applicationContext, MainActivityKotlin::class.java)
            ContextCompat.startActivity(this.applicationContext, intent, null)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
        } else {
            finish()
        }


        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: $event")
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()

        cl_Progress.visibility = View.GONE
        searchText.setText("")
        sportsItemListDOCS.clear()
        notifyAdapter()


    }


    /*  override fun onBackPressed() {

        *//*  if (Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            val intent = Intent(this.applicationContext, MainActivityKotlin::class.java)
            ContextCompat.startActivity(this.applicationContext, intent, null)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
        } else {
            finish()
        }

*//*

        finish()



    }

*/


}





