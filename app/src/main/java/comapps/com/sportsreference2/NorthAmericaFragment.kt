package comapps.com.sportsreference2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NorthAmericaFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NorthAmericaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NorthAmericaFragment : Fragment() {


    private var checkBoxInstructions: CheckBox? = null
    private var textViewI: TextView? = null


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val mParam1 = arguments!!.getString(ARG_PARAM1)
            val mParam2 = arguments!!.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_north_america, container, false)

        val mlbbutton = v.findViewById<View>(R.id.button1)
        val nflbutton = v.findViewById<View>(R.id.button2)
        val nhlbutton = v.findViewById<View>(R.id.button3)
        val nbabutton = v.findViewById<View>(R.id.button4)
        val cfbbutton = v.findViewById<View>(R.id.button5)
        val cbbbutton = v.findViewById<View>(R.id.button6)
        //   View testbutton = v.findViewById(R.id.button7);


        textViewI = v.findViewById(R.id.textViewI)
        checkBoxInstructions = v.findViewById(R.id.checkBoxInstructions)

        val mlbTeamLinksStringArray = resources.getStringArray(R.array.mlbteamlinks)
        val nflTeamLinksStringArray = resources.getStringArray(R.array.nflteamlinks)
        val nhlTeamLinksStringArray = resources.getStringArray(R.array.nhlteamlinks)
        val nbaTeamLinksStringArray = resources.getStringArray(R.array.nbateamlinks)

        Log.d(TAG, "prefs showInstructions " + prefs.showInstructions)

        checkBoxInstructions!!.isChecked = prefs.showInstructions


        checkBoxInstructions!!.setOnCheckedChangeListener { buttonView, isChecked ->
            prefs.showInstructions = buttonView.isChecked


            Log.d(TAG, "checkbox checked " + checkBoxInstructions!!.isChecked)
        }


        mlbbutton.setOnClickListener {
            val intentbaseballsearch = Intent()
            intentbaseballsearch.setClass(activity!!.applicationContext, SportsItemActivityKotlin::class.java)
            intentbaseballsearch.putExtra("sport", "baseball")
            startActivity(intentbaseballsearch)
            //        getActivity().finish();
            try {
                activity!!.overridePendingTransition(R.anim.pushinfromleft, R.anim.pushouttoright)
                //    activity!!.finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        mlbbutton.setOnLongClickListener { view ->
            Log.i(TAG, "LONG CLICK")


            if (prefs.mlbfav == 0) {
                val toast = Toast.makeText(activity,
                        "Set favorite for long\n click in settings.",
                        Toast.LENGTH_SHORT)

                toast.setGravity(Gravity.TOP, 0, 400)
                toast.show()
            } else {

                val linkToPass = mlbTeamLinksStringArray[prefs.mlbfav - 1]


                val intent = Intent(view.context, WebView::class.java)
                intent.putExtra("link", linkToPass)
                intent.putExtra("sentFrom", "FAVORITE")
                startActivity(intent)

            }



            false
        }

        nflbutton.setOnClickListener {
            val intentfootballsearch = Intent()
            intentfootballsearch.setClass(activity!!.applicationContext, SportsItemActivityKotlin::class.java)
            intentfootballsearch.putExtra("sport", "football")
            startActivity(intentfootballsearch)
            //    getActivity().finish();
            try {
                activity!!.overridePendingTransition(R.anim.pushinfromleft, R.anim.pushouttoright)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        nflbutton.setOnLongClickListener { view ->
            Log.i(TAG, "LONG CLICK")


            if (prefs.nflfav == 0) {
                val toast = Toast.makeText(activity,
                        "Set favorite for long\n click in settings.",
                        Toast.LENGTH_SHORT)

                toast.setGravity(Gravity.TOP, 0, 400)
                toast.show()
            } else {

                val linkToPass = nflTeamLinksStringArray[prefs.nflfav - 1]


                val intent = Intent(view.context, WebView::class.java)
                intent.putExtra("link", linkToPass)
                intent.putExtra("sentFrom", "FAVORITE")
                startActivity(intent)

            }


            false
        }


        nhlbutton.setOnClickListener {
            val intenthockeysearch = Intent()
            intenthockeysearch.setClass(activity!!.applicationContext, SportsItemActivityKotlin::class.java)
            intenthockeysearch.putExtra("sport", "hockey")
            startActivity(intenthockeysearch)
            //       getActivity().finish();
            try {
                activity!!.overridePendingTransition(R.anim.pushinfromleft, R.anim.pushouttoright)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        nhlbutton.setOnLongClickListener { view ->
            Log.i(TAG, "LONG CLICK")

            if (prefs.nhlfav == 0) {
                val toast = Toast.makeText(activity,
                        "Set favorite for long\n click in settings.",
                        Toast.LENGTH_SHORT)

                toast.setGravity(Gravity.TOP, 0, 400)
                toast.show()
            } else {

                val linkToPass = nhlTeamLinksStringArray[prefs.nhlfav - 1]


                val intent = Intent(view.context, WebView::class.java)
                intent.putExtra("link", linkToPass)
                intent.putExtra("sentFrom", "FAVORITE")
                startActivity(intent)

            }

            false
        }


        nbabutton.setOnClickListener {
            val intentbasketballsearch = Intent()
            intentbasketballsearch.setClass(activity!!.applicationContext, SportsItemActivityKotlin::class.java)
            intentbasketballsearch.putExtra("sport", "basketball")
            startActivity(intentbasketballsearch)
            //       getActivity().finish();
            try {
                activity!!.overridePendingTransition(R.anim.pushinfromleft, R.anim.pushouttoright)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        nbabutton.setOnLongClickListener { view ->
            Log.i(TAG, "LONG CLICK")

            if (prefs.nbafav == 0) {
                val toast = Toast.makeText(activity,
                        "Set favorite for long\n click in settings.",
                        Toast.LENGTH_SHORT)

                toast.setGravity(Gravity.TOP, 0, 400)
                toast.show()
            } else {

                val linkToPass = nbaTeamLinksStringArray[prefs.nbafav - 1]


                val intent = Intent(view.context, WebView::class.java)
                intent.putExtra("link", linkToPass)
                intent.putExtra("sentFrom", "FAVORITE")
                startActivity(intent)

            }


            false
        }


        cfbbutton.setOnClickListener {
            val intentcollegefootballsearch = Intent()
            intentcollegefootballsearch.setClass(activity!!.applicationContext, SportsItemActivityKotlin::class.java)
            intentcollegefootballsearch.putExtra("sport", "college_football")
            startActivity(intentcollegefootballsearch)
            //      getActivity().finish();
            try {
                activity!!.overridePendingTransition(R.anim.pushinfromleft, R.anim.pushouttoright)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        cbbbutton.setOnClickListener {
            val intentcollegebasketballsearch = Intent()
            intentcollegebasketballsearch.setClass(activity!!.applicationContext, SportsItemActivityKotlin::class.java)
            intentcollegebasketballsearch.putExtra("sport", "college_basketball")
            startActivity(intentcollegebasketballsearch)
            //         getActivity().finish();
            try {
                activity!!.overridePendingTransition(R.anim.pushinfromleft, R.anim.pushouttoright)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /* testbutton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intenttestsearch = new Intent();
                        intenttestsearch.setClass(getActivity().getApplicationContext(), SportsItemActivityKotlin.class);
                        intenttestsearch.putExtra("sport", "baseball");
                        startActivity(intenttestsearch);
                        //         getActivity().finish();
                        try {
                            getActivity().overridePendingTransition(R.anim.pushinfromright, R.anim.pushouttoleft);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                }

        );
*/
        val Number = Random()
        val rNumber = Number.nextInt(10)



        if (rNumber < 9 && prefs.showInstructions) {
            textViewI!!.visibility = View.GONE
            checkBoxInstructions!!.visibility = View.GONE


        }



        return v


    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        private const val TAG = "NORTHAMERICA"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NorthAmericaFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): NorthAmericaFragment {
            val fragment = NorthAmericaFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args



            return fragment
        }


    }
}// Required empty public constructor
