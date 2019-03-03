package comapps.com.sportsreference2

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.itemlayoutrecycler_constraint.view.*
import java.util.*


/**
 * Created by me on 2/26/2018.
 */
class SportsItemAdapter(private val listSportsItems: MutableList<SportsItem>) :
        RecyclerView.Adapter<SportsItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout
                .itemlayoutrecycler_constraint,
                parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(listSportsItems[position])



        holder.itemView.setOnClickListener {


            val intent = Intent(it.context, WebView::class.java)
            intent.putParcel(parcel = listSportsItems[position])
            intent.putExtra("sentFrom", "ADAPTER")
            startActivity(it.context, intent, null)
        }



        var schoolLink = listSportsItems[position].schoolLink


        if ( !schoolLink.isNullOrEmpty() ) {

            holder.itemView.setOnLongClickListener {


                val intent = Intent(it.context, WebView::class.java)
                intent.putParcel(parcel = listSportsItems[position])
                intent.putExtra("sentFrom", "ADAPTERLONGCLICK")
                startActivity(it.context, intent, null)
                true


            }


        }




    }


    override fun getItemCount() = listSportsItems.size


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        fun bindForecast(sportsItem: SportsItem) {
            with(sportsItem) {

                //   println("$TAG SIA adapter sportsItem: $this")

                itemView.textViewName.text = deAccent(sportsItem.name)
                val schoolOrTeamText = this.schoolOrTeam.replace("amp;", "").trim()

                //***********************************************************************************************

                val year: Int = Calendar.getInstance().get(Calendar.YEAR)
                var careerSpan = ""

     ///           println("$TAG SIA adapter sportsItem: $this")


                if (this.firstSeason == "") {


                    itemView.textViewSeasons.visibility = View.GONE


                } else {

                    itemView.textViewSeasons.visibility = View.VISIBLE

                    if (this.firstSeason == this.lastSeason) {
                        itemView.textViewSeasons.text = this.lastSeason
                    } else {
                        itemView.textViewSeasons.text = "${this.firstSeason}-${this.lastSeason}"

                    }

                    if ( sport == "basketball" ) {

                        itemView.textViewSeasons.text = "debut NBA ${this.firstSeason}"

                    }


                }


                //****************************************************************************************


                val positionText = (this.position).replace("Position:|Positions:", "").trim()

                itemView.textViewPosition.text = positionText

                itemView.textViewPosition.visibility = View.VISIBLE
                if (itemView.textViewPosition.text == "") {
                    itemView.textViewPosition.visibility = View.GONE
                }

                //*************************************************************************************************




                if ( schoolOrTeamText == "" || sport == "baseball") {

          //          println("$TAG sport schoolOrTeam $sport $schoolOrTeam")
                    itemView.textViewSchoolOrTeam.visibility = View.GONE

                } else {

                    if (this.schoolLink.contains("https")) {


                        val longClickString = schoolOrTeamText + "\n" + "long click for college stats"


                        itemView.textViewSchoolOrTeam.text = longClickString.replace(
                                "|",
                                ""
                        )


                        itemView.textViewSchoolOrTeam.visibility = View.VISIBLE

                    } else {

                        if (this.sport.contains("college")) {



                            val schoolTextForCollege = schoolOrTeamText.split(':')

                            try {
                                if (schoolTextForCollege.size > 1) {
                                    itemView.textViewSchoolOrTeam.text = schoolTextForCollege[1]
                                }
                            } catch (e: Exception) {
                            }

                            try {
                                if (schoolTextForCollege.size > 2) {
                                    itemView.textViewSchoolOrTeam.text = schoolTextForCollege[2]
                                }
                            } catch (e: Exception) {
                            }


                        } else {

                            itemView.textViewSchoolOrTeam.text = schoolOrTeamText.replace("|", "\n")
                                    .replace("Schools:|School:", "").trim()
                            itemView.textViewSchoolOrTeam.visibility = View.VISIBLE


                        }

                    }
                }

                //********************************************************************************

                if (this.type != "player") {
                    itemView.textViewType.text = this.type
                    itemView.textViewType.visibility = View.VISIBLE
                    itemView.textViewPosition.visibility = View.GONE


                    if (this.sport.contains("college") && this.type == "coach" && schoolOrTeamText != "") {
                        itemView.textViewSchoolOrTeam.text = "last coached " + schoolOrTeamText
                        itemView.textViewSchoolOrTeam.visibility = View.VISIBLE
                        itemView.textViewType.visibility = View.GONE
                    } else {
                        itemView.textViewSchoolOrTeam.visibility = View.GONE
                    }


                } else {
                    itemView.textViewType.visibility = View.GONE

                    if (this.sport.contains("college") && schoolOrTeamText != "") {
                        itemView.textViewSchoolOrTeam.text = schoolOrTeamText
                        itemView.textViewSchoolOrTeam.visibility = View.VISIBLE


                    }
                }


            }
        }


    }


}


