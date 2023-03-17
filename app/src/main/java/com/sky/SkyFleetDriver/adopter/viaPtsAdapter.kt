package com.sky.SkyFleetDriver.adopter


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sky.skyfleettech.R
import com.sky.SkyFleetDriver.model.Job
import com.sky.SkyFleetDriver.model.WayptX

class viaPtsAdapter(private val mList: List<WayptX>, private val context: Context
                    , private val job : Job, private val home:Boolean ) : RecyclerView.Adapter<viaPtsAdapter.ViewHolder>(){


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_via_points, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.Txt.text = mList[position].address
        Log.e("12345678912345",mList.get(position).address.toString())
        holder.viaLable.text = "Point " + (position + 1).toString() + ":"
//       holder.completed.setImageDrawable()= mList.get(position).completed

//       Picasso.get().load(mList[position]).resize(150,150).into(holder.itemImage)


        if (mList.get(position).completed=="yes"){


            holder.Txt.setTextColor(Color.parseColor("#3DBA42")) //"#3DBA42"
            holder.viaLable.setTextColor(Color.parseColor("#3DBA42"))
            holder.completed.setImageResource(R.drawable.img)
            Log.e("parthiban",mList.get(position).completed.toString());

        }

        if (mList.get(position).completed=="no"){

            holder.completed.setImageResource(R.drawable.img_1)
            Log.e("parthibansss",mList.get(position).completed.toString());

        }


//        holder.viapts.setOnClickListener {
//            if(home) {
//
//                val intent = Intent(context, OrderPendingDetailsActivity::class.java)
//                intent.putExtra("MyClass", job)
//                startActivity(context, intent, null)
//            }
 //       }




//     fun checkActivity(job: Job){
//
//         if (this.equals(job)){
//
//                val intent = Intent(context, OrderPendingDetailsActivity::class.java)
//                intent.putExtra("MyClass", job)
//                startActivity(context, intent, null)
//
//            }else{
//
//
//            }
//        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

//        val viapts: LinearLayout = itemView.findViewById(R.id.viapts_adapter)
        val Txt: TextView = itemView.findViewById(R.id.via_address)
        val viaLable: TextView = itemView.findViewById(R.id.via_lable)
        val completed:ImageView=itemView.findViewById(R.id.completed)

    }



}


