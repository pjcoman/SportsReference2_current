package comapps.com.sportsreference2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WorldFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WorldFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorldFragment : Fragment() {



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
        val v = inflater.inflate(R.layout.fragment_world, container, false)

        val button1World = v.findViewById<View>(R.id.button1World)

        button1World.setOnClickListener {
            val intentplayersearch = Intent()
            intentplayersearch.setClass(activity!!.applicationContext, SportsItemActivityKotlin::class.java)
            intentplayersearch.putExtra("sport", "soccer")
            startActivity(intentplayersearch)
            //        getActivity().finish();
            try {
                activity!!.overridePendingTransition(R.anim.pushinfromright, R.anim.pushouttoleft)
                //    activity!!.finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }


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
    interface OnFragmentInteractionListener

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NorthAmericaFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): WorldFragment {
            val fragment = WorldFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args



            return fragment
        }


    }
}// Required empty public constructor
