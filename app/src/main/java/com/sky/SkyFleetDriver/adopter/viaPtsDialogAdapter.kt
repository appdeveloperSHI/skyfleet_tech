package com.sky.SkyFleetDriver.adopter


import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sky.SkyFleetDriver.model.WayptX
import com.sky.skyfleettech.R
import java.io.IOException

class viaPtsDialogAdapter(private val mList: List<WayptX>, private val context: Context) : RecyclerView.Adapter<viaPtsDialogAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_textview, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.Txt.text =  "Point " + (position + 1).toString() + "   :" + mList[position].address


        holder.Txt.setOnClickListener{
            getLocationFromAddress(  mList[position].address)
        }


    }
    fun getLocationFromAddress(coordinates: String?) {
        val coder = Geocoder(context)
        val address: List<Address>
        try {
            address = coder.getFromLocationName(coordinates, 5)
            /**       if (address == null) {
             * return false;
             * }
             *
             */
            val location = address[0]
            //            Log.e("TAGlocation", String.valueOf(location));
            location.latitude
            location.longitude
            //            String url = "http://maps.google.com/maps?daddr="+address;
            val url =
                "http://maps.google.com/maps?daddr=" + location.latitude + "," + location.longitude
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)

//            String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + mTitle + ")";
            /**
             * String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location.getLatitude(), location.getLongitude());
             * Log.e("taglatlong",  location.getLatitude() + " // " + location.getLongitude() );
             * Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
             * startActivity(intent);
             */
//                return true;
        } catch (e: IOException) {
            e.printStackTrace()
        }

//        return true;
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val Txt: TextView = itemView.findViewById(R.id.item_text)
    }
}
