package comapps.com.sportsreference2

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.Normalizer
import java.util.regex.Pattern


/*fun deAccent(str: String): String {
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
}*/


class MainActivityKotlin : AppCompatActivity() {

    private val TAG = "MAKOTLIN"

    private var viewPager: ViewPager? = null
    private var itemsHistory = ArrayList<SportsItem>()
    private val type = object : TypeToken<java.util.ArrayList<SportsItem>>() {}.type
    private var spinnerHistoryAdapter: HistorySpinnerAdapter? = null
    private var spinnerCountFilterAdapter: ItemSpinnerAdapter? = null
    private var spinnerMLBAdapter: ItemSpinnerAdapter? = null
    private var spinnerNFLAdapter: ItemSpinnerAdapter? = null
    private var spinnerNHLAdapter: ItemSpinnerAdapter? = null
    private var spinnerNBAAdapter: ItemSpinnerAdapter? = null


    private var spinnerMLB: Spinner? = null
    private var spinnerNFL: Spinner? = null
    private var spinnerNHL: Spinner? = null
    private var spinnerNBA: Spinner? = null
    private var spinnerFilterCount: Spinner? = null
    private var spinnerHistory: Spinner? = null

    private var userIsInteracting: Boolean = false

    private var loaded = false

    private var adapter: PagerAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.MyTheme)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabbedlayoutmain2)

        val actionBar = supportActionBar

        actionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.layout_rounded_corners_transparent))



        spinnerHistory = findViewById(R.id.spinnerHistory)

        viewPager = findViewById(R.id.simpleViewPager)
        val tabLayout = findViewById<TabLayout>(R.id.simpleTabLayout)


        val firstTab = tabLayout.newTab()
        firstTab.text = null // set the Text for the first Tab
        firstTab.setIcon(R.drawable.na_icon) // set an icon for the
        tabLayout.addTab(firstTab) // add  the tab at in the TabLayout

        val secondTab = tabLayout.newTab()
        secondTab.text = null // set the Text for the second Tab
        secondTab.setIcon(R.drawable.world_icon) // set an icon for the
        tabLayout.addTab(secondTab) // add  the tab  in the TabLayout


        for (i in 0 until tabLayout.tabCount) {

            val imageView = ImageView(applicationContext)
            if (i == 0) {
                imageView.setImageResource(R.drawable.na_icon)

            } else {
                imageView.setImageResource(R.drawable.world_icon)
            }



            tabLayout.getTabAt(i)!!.customView = imageView
        }



        adapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager!!.adapter = adapter

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                Log.e(TAG, "Tab selected ----> " + tab.position)

                //THIS!!

                viewPager!!.currentItem = tab.position


            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


        loadHistory()



    }

    //****************************************MENU ***************************************************

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.


        menuInflater.inflate(R.menu.menu_main, menu)





        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.searchBarLayout) {

            prefs.searchBarBottom = !prefs.searchBarBottom

        } else {


            val popDialogBuilder = AlertDialog.Builder(this)
            val mView = layoutInflater.inflate(R.layout.customdialogsettings, null)
            //    popDialogBuilder.setIcon(R.mipmap.ic_launcher);
            //    popDialogBuilder.setTitle("Characters for Filter");

            popDialogBuilder.setView(mView)
            val alertDialog = popDialogBuilder.create()

            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            alertDialog.show()

            spinnerMLB = mView.findViewById<View>(R.id.spinnerMLB) as Spinner
            spinnerNFL = mView.findViewById<View>(R.id.spinnerNFL) as Spinner
            spinnerNHL = mView.findViewById<View>(R.id.spinnerNHL) as Spinner
            spinnerNBA = mView.findViewById<View>(R.id.spinnerNBA) as Spinner

            spinnerMLBAdapter = ItemSpinnerAdapter(applicationContext, resources.getStringArray(R.array.mlbteams))
            spinnerMLBAdapter!!.notifyDataSetInvalidated()
            spinnerMLBAdapter!!.notifyDataSetChanged()

            spinnerNFLAdapter = ItemSpinnerAdapter(applicationContext, resources.getStringArray(R.array.nflteams))
            spinnerNFLAdapter!!.notifyDataSetInvalidated()
            spinnerNFLAdapter!!.notifyDataSetChanged()

            spinnerNHLAdapter = ItemSpinnerAdapter(applicationContext, resources.getStringArray(R.array.nhlteams))
            spinnerNHLAdapter!!.notifyDataSetInvalidated()
            spinnerNHLAdapter!!.notifyDataSetChanged()

            spinnerNBAAdapter = ItemSpinnerAdapter(applicationContext, resources.getStringArray(R.array.nbateams))
            spinnerNBAAdapter!!.notifyDataSetInvalidated()
            spinnerNBAAdapter!!.notifyDataSetChanged()


            spinnerMLB!!.adapter = spinnerMLBAdapter
            spinnerNFL!!.adapter = spinnerNFLAdapter
            spinnerNHL!!.adapter = spinnerNHLAdapter
            spinnerNBA!!.adapter = spinnerNBAAdapter



            spinnerMLB!!.setSelection(prefs.mlbfav)
            spinnerNFL!!.setSelection(prefs.nflfav)
            spinnerNHL!!.setSelection(prefs.nhlfav)
            spinnerNBA!!.setSelection(prefs.nbafav)



            spinnerMLB!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                    if (i != 0) {
                        prefs.mlbfav = i
                    }


                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }

            spinnerNFL!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                    if (i != 0) {

                        //    prefs!!.edit().putString("NFLFAV", spinnerNfl!!.selectedItem.toString()).apply()
                        prefs.nflfav = i

                    }
                }


                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }

            spinnerNHL!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                    if (i != 0) {


                        //    prefs!!.edit().putString("NHLFAV", spinnerNhl!!.selectedItem.toString()).apply()
                        prefs.nhlfav = i

                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }

            spinnerNBA!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                    if (i != 0) {


                        //    prefs!!.edit().putString("NBAFAV", spinnerNba!!.selectedItem.toString()).apply()
                        prefs.nbafav = i
                    }

                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }


            val clearHistory = mView.findViewById<Button>(R.id.buttonClearHistory)


            if (itemsHistory.size != 0) {

                clearHistory.visibility = View.VISIBLE
                clearHistory.setText(R.string.ch)

            } else {
                clearHistory.visibility = View.GONE
                spinnerHistory?.visibility = View.GONE
            }


            clearHistory.setOnClickListener {

                prefs.sportsItemHistory = "[{\"name\":\"search history\"}]"
                itemsHistory.clear()
                spinnerHistoryAdapter!!.notifyDataSetChanged()

                val toast = Toast.makeText(this@MainActivityKotlin,
                        resources.getText(R.string.chtoast),
                        Toast.LENGTH_SHORT)

                toast.setGravity(Gravity.TOP, 0, 400)
                centerText(toast.view)
                toast.show()


            }










            spinnerFilterCount = mView.findViewById(R.id.spinnerFilterChar)

            spinnerCountFilterAdapter = ItemSpinnerAdapter(applicationContext, resources.getStringArray(R.array.integers))
            spinnerCountFilterAdapter!!.notifyDataSetInvalidated()
            spinnerCountFilterAdapter!!.notifyDataSetChanged()

            spinnerFilterCount!!.adapter = spinnerCountFilterAdapter
            spinnerFilterCount!!.setSelection(prefs.timerInt - 1)


            spinnerFilterCount!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {


                    if (i >= 0) {

                        prefs.timerInt = i + 1
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }


        }



        return super.onOptionsItemSelected(item)
    }






    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val item = menu!!.findItem(R.id.searchBarLayout)

        if ( prefs.searchBarBottom ) {
            item.title = resources.getString(R.string.sbt)
        } else {
            item.title = resources.getString(R.string.sbb)
        }


        return super.onPrepareOptionsMenu(menu)
    }

    //****************************************MENU ***************************************************


    private fun centerText(view: View) {
        if (view is TextView) {
            view.gravity = Gravity.CENTER
        } else if (view is ViewGroup) {
            val n = view.childCount
            for (i in 0 until n) {
                centerText(view.getChildAt(i))
            }
        }
    }

    private fun loadHistory() {



        println("$TAG itemsHistory size ${itemsHistory.size}")
        println("$TAG sportsItemHistory ---> ${prefs.sportsItemHistory}")

        try {





            itemsHistory = Gson().fromJson<java.util.ArrayList<SportsItem>>(prefs.sportsItemHistory, type)

            /* if (!(prefs.sih).contains(resources.getString(R.string.sh))) {

                      val dummySportsItem = SportsItem(name = resources.getString(R.string.sh), link = "",
                              type = "", position = "", sport = "", DB_ID = "", firstSeason = "",
                              lastSeason = "", schoolLink = "", schoolOrTeam = "", SCRAPED = false)

                     itemsHistory?.add(0, dummySportsItem)
                      val jsonHistoryList = Gson().toJson(itemsHistory)
                      prefs.sih = jsonHistoryList


                  }


          */


            /*if ( itemsHistory.size > 1 ) {
                itemsHistory.removeAt(0)
            }*/


            spinnerHistoryAdapter = HistorySpinnerAdapter(applicationContext, itemsHistory)
            spinnerHistoryAdapter!!.notifyDataSetInvalidated()
            spinnerHistoryAdapter!!.notifyDataSetChanged()


            spinnerHistory!!.adapter = spinnerHistoryAdapter

            //      if ( userIsInteracting ) {spinnerHistory!!.isSelected = true} else {spinnerHistory!!.isSelected = false}
            spinnerHistory!!.setSelection(0, true)


            spinnerHistory!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                    println("$TAG onItemSelectedListener $userIsInteracting $position")
                    //    dimBackground()

                    val intent: Intent

                    if (userIsInteracting) {
                        intent = Intent(application.applicationContext, WebView::class.java)
                        intent.putParcel(parcel = itemsHistory[position])
                        intent.putExtra("sentFrom", "HISTORY")
                        userIsInteracting = false
                        startActivity(intent)


                    }
                }


                override fun onNothingSelected(parent: AdapterView<*>) {


                }

            }
        } catch (e: Exception) {


            /* itemsHistory.add(0, SportsItem(name = "search history"))
            // spinnerHistory?.visibility = View.GONE

             spinnerHistoryAdapter = HistorySpinnerAdapter(applicationContext, itemsHistory)
             spinnerHistoryAdapter!!.notifyDataSetInvalidated()
             spinnerHistoryAdapter!!.notifyDataSetChanged()


             spinnerHistory!!.adapter = spinnerHistoryAdapter*/
        }
    }


    inner class HistorySpinnerAdapter(internal val context: Context, private val itemsHistory:
    ArrayList<SportsItem>?) : BaseAdapter() {

        override fun getCount(): Int {
            return itemsHistory?.size ?: 0
        }

        override fun getItem(i: Int): SportsItem? {
            return itemsHistory?.get(i)
        }

        override fun getItemId(i: Int): Long {
            return 0
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view
            if (view == null) {
                val inflater = context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.itemlayouthistory2, viewGroup, false)

            }


            val itemFromHistory = itemsHistory!![i]

            val txtName = view!!.findViewById<TextView>(R.id.textViewNameHistory)
            /*  val txtType = view.findViewById<TextView>(R.id.textViewTypeHistory)*/
            val imgView = view.findViewById<ImageView>(R.id.imageViewIconHistory)

            println("$TAG txtName ${txtName.text} $i")




            txtName.text = itemFromHistory.name



            /* if (itemFromHistory.type == "player" || itemFromHistory.type == "team"
                     || itemFromHistory.type == "school" || itemFromHistory.type == "") {
                 txtType.visibility = View.GONE
             } else {
                 txtType.visibility = View.VISIBLE
                 txtType.text = "as ${itemFromHistory.type}"
             }*/

            val options = BitmapFactory.Options()
            options.outHeight = 40
            options.outWidth = 40
            val bmfb = BitmapFactory.decodeResource(resources, R.drawable.football_icon_history, options)
            val bmbb = BitmapFactory.decodeResource(resources, R.drawable.baseball_icon_history, options)
            val bmh = BitmapFactory.decodeResource(resources, R.drawable.hockey_icon_history, options)
            val bmbkb = BitmapFactory.decodeResource(resources, R.drawable.basketball_icon_history, options)
            val bmcfb = BitmapFactory.decodeResource(resources, R.drawable.collegefootball_icon_history, options)
            val bmcbb = BitmapFactory.decodeResource(resources, R.drawable.collegebasketball_icon_history, options)
            val bms = BitmapFactory.decodeResource(resources, R.drawable.fbreficonhistory, options)
            val bmsr = BitmapFactory.decodeResource(resources, R.drawable.sr_icon_notitle, options)



            if (itemFromHistory.sport == "football") {
                imgView.setImageBitmap(bmfb)
            } else if (itemFromHistory.sport == "baseball") {
                imgView.setImageBitmap(bmbb)
            } else if (itemFromHistory.sport == "soccer") {
                imgView.setImageBitmap(bms)
            } else if (itemFromHistory.sport == "basketball") {
                imgView.setImageBitmap(bmbkb)
            } else if (itemFromHistory.sport == "hockey") {
                imgView.setImageBitmap(bmh)
            } else if (itemFromHistory.sport == "college_football") {
                imgView.setImageBitmap(bmcfb)
            } else if (itemFromHistory.sport == "college_basketball")    {
                imgView.setImageBitmap(bmcbb)
            } else {
                imgView.setImageBitmap(bmsr)
            }

            return view
        }

    }


    inner class ItemSpinnerAdapter(internal val context: Context, private val items:
    Array<String>) : BaseAdapter() {

        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(i: Int): String? {
            return items[i]
        }

        override fun getItemId(i: Int): Long {
            return 0
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view: View? = view
            if (view == null) {
                val inflater = context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.customspinneritem, viewGroup, false)
            }


            val item = items[i]

            val txtItem = view!!.findViewById<TextView>(R.id.text1)

            txtItem.text = item






            return view
        }

    }

    public override fun onResume() {
        super.onResume()






        if (!loaded) {
            loaded = true
        } else {
            itemsHistory.clear()
            loadHistory()
            spinnerHistory?.visibility = View.VISIBLE

            userIsInteracting = false

            println("$TAG ON RESUME")

        }



    }





    override fun onUserInteraction() {
        super.onUserInteraction()
        userIsInteracting = true
    }










}




