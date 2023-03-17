package com.sky.SkyFleetDriver.fregment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.sky.skyfleettech.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class EstimateMapsFragment : Fragment() {
    private lateinit var map: GoogleMap
    private var userId: Int = -1
    private var isBookingConfirmed = false
    private var deviceId: String = ""
    var orgin : String= "Chennai 1A Chennai Tamil Nadu India 600082"
    var destina : String= "Chengalpattu, Tamil Nadu, India"
    lateinit var waypts:ArrayList<String>



    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(22.852056, 78.541122), 4f
            )
        )
        Single.fromCallable {
            getPollyLineOnMap(orgin, destina)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orgin = this.requireArguments().getString("pickup").toString()
        destina = this.requireArguments().getString("dropoff").toString()
        waypts = this.requireArguments().getStringArrayList("waypts") as ArrayList<String>//todo  contine


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


    }

    private fun getPollyLineOnMap(origin: String, dest: String) {
        // Getting URL to the Google Directions API
        val url = getDirectionsUrl(origin, dest)
//        Log.e( "getPerror",url )
//        binding.progressBar.visibility = View.VISIBLE
        val path: MutableList<List<LatLng>> = ArrayList()

        val directionsRequest = object :
            StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    // Get routes
                    val routes = jsonResponse.getJSONArray("routes")
                    val legs = routes.getJSONObject(0).getJSONArray("legs")

                    for (i in 0 until  legs.length() ){
                        val steps = legs.getJSONObject(i).getJSONArray("steps")
                        for (j in 0 until steps.length()) {
                            val points =
                                steps.getJSONObject(j).getJSONObject("polyline").getString("points")
                            path.add(PolyUtil.decode(points))

                        }


                        val lastIndex = path[path.lastIndex].lastIndex
                        val intermediate = path[path.lastIndex][lastIndex]

                        val markerIcon =
                            getMarkerBitmapFromVectorRecourse(
                                requireContext(),
                                R.drawable.c_point_icon
                            )
                        map.addMarker(
                            MarkerOptions().position(intermediate).title("Origin").icon(markerIcon)
                        )
//                        val random = Random()
//
//                        val color = Color.argb(
//                            255,
//                            random.nextInt(256),
//                            random.nextInt(256),
//                            random.nextInt(256)
//                        )


//                        path.clear()


                    }

                    for (k in 0 until path.size) {
                        map.addPolyline(PolylineOptions().addAll(path[k]).color(Color.BLUE))
                    }

//                    for (k in 0 until path.size) {
//                        val random = Random()
//
//                        val color = Color.argb(
//                            255,
//                            random.nextInt(256),
//                            random.nextInt(256),
//                            random.nextInt(256)
//                        )
//
//                        map.addPolyline(PolylineOptions().addAll(path[k]).color(color))
//                    }
                    /**
                    val steps = legs.getJSONObject(0).getJSONArray("steps")
                    for (i in 0 until steps.length()) {
                    val points =
                    steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                    path.add(PolyUtil.decode(points))
                    }

                    for (i in 0 until path.size) {
                    map.addPolyline(PolylineOptions().addAll(path[i]).color(Color.BLUE))
                    }

                     */
//                    binding.progressBar.visibility = View.GONE
                    val origin1 = path[0][0]
                    val lastIndex = path[path.lastIndex].lastIndex
                    val destination = path[path.lastIndex][lastIndex]
                    var markerIcon =
                        getMarkerBitmapFromVectorRecourse(
                            requireContext(),
                            R.drawable.b_point_icon
                        )
                    map.addMarker(
                        MarkerOptions().position(origin1).title("Origin").icon(markerIcon)
                    )
                    markerIcon =
                        getMarkerBitmapFromVectorRecourse(
                            requireContext(),
                            R.drawable.a_point_icon
                        )
                    map.addMarker(
                        MarkerOptions().position(destination).title("Destination")
                            .icon(markerIcon)
                    )
                    val bounds = LatLngBounds.Builder()
                        .include(origin1)
                        .include(destination)
                        .build()
                    val padding = 140
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
//                    binding.progressBar.visibility = View.GONE

                } catch (e: Exception) {
                    e.printStackTrace()
//                    binding.progressBar.visibility = View.GONE
                }
            }, Response.ErrorListener { error ->
                error.printStackTrace()
//                binding.progressBar.visibility = View.GONE
//                Log.e( "TAGgetPollyLineOnMap","unabletoloadmap" )
//                requireContext().toast("Unable to load route on map")
            }) {}
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(directionsRequest)

    }

    private fun getDirectionsUrl(origin: String, dest: String): String {
        var wayPts="waypoints="
        for(i in waypts){
            val addres=i.trim().replace(" ", "+") + "|"
            wayPts += addres
        }

//val waypts="55VM+4GG, Red Hills, Padianallur, Tamil Nadu 600052"
//val waypts2="Manali, Chennai, Tamil Nadu, India"
        // Origin of route
        val str_origin = "origin=" + origin.trim().replace(" ", "+")

        //waypoints
//        val way_pts = "waypoints=" + waypts.trim().replace(" ", "+")
//        val way_pts2 = "|" + waypts2.trim().replace(" ", "+")
//        val way_pts = "waypoints=" + origin.trim().replace(" ", "+")
//        &waypoints=37.746560%2C-122.408328
//        &waypoints=optimize:true|Barossa+Valley,SA|Clare,SA|Connawarra,SA|McLaren+Vale,SA

        // Destination of route
        val str_dest = "destination=" + dest.trim().replace(" ", "+")

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"

        // Building the parameters to the web service
//        val parameters = "$str_origin&$str_dest&$way_pts$way_pts2&$sensor&$mode"
        val parameters = "$str_origin&$str_dest&$wayPts&$sensor&$mode"
        //todo problem may come due to | this line in waypts

        // Output format
        val output = "json"

        //Key
        val key = "&key=${getString(R.string.google_maps_api_key)}"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters$key"
    }





    private fun getMarkerBitmapFromVectorRecourse(
        context: Context,
        imageResId: Int,
    ): BitmapDescriptor {
        val drawable = requireContext().resources.getDrawable(imageResId)
        val bitmap: Bitmap
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}
//TODO use this for direction with restriceted key
/**
 * private void drawMap(double s_lat,double s_lng,double e_lat,double e_lng) {
GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
Log.e("map", "++");
List<LatLng> waypoints = Arrays.asList(

new LatLng(22.626390800000003, 88.4313014), new LatLng(22.619708499999998, 88.4369083)
);
GoogleDirection.withServerKey("AIz... your google api key")
.from(new LatLng(s_lat, s_lng))
.and(waypoints)
.to(new LatLng(e_lat, e_lng))
.transportMode(TransportMode.DRIVING)
.execute(new DirectionCallback() {
@Override
public void onDirectionSuccess(Direction direction, String rawBody) {
if (direction.isOK()) {
mMap.setMinZoomPreference(8f);
com.akexorcist.googledirection.model.Route route = direction.getRouteList().get(0);
int legCount = route.getLegList().size();
for (int index = 0; index < legCount; index++) {
Log.e("map", "++++" + index);
Leg leg = route.getLegList().get(index);
// mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()));

if (index == 0) {
Log.e("position","0" + leg.getStartLocation().getCoordination());
//   mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title("User"));
mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()).icon(BitmapDescriptorFactory
.fromResource(R.drawable.start_pointer)));
} else if (index == legCount - 1) {
//   mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title("User"));
mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).icon(BitmapDescriptorFactory
.fromResource(R.drawable.stop_pointer)));
} else {
mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).icon(BitmapDescriptorFactory
.fromResource(R.drawable.user_point)));
}
List<Step> stepList = leg.getStepList();
ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(MainActivity.this, stepList, 5, Color.RED, 3, Color.BLUE);
for (PolylineOptions polylineOption : polylineOptionList) {
mMap.addPolyline(polylineOption);
}
}
setCameraWithCoordinationBounds(route); // animateCamera

}
}

@Override
public void onDirectionFailure(Throwable t) {

Log.e("error", t.getLocalizedMessage() + t.getMessage() + "");
// Do something
}
});
}

private void setCameraWithCoordinationBounds(com.akexorcist.googledirection.model.Route route) {
LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
LatLngBounds bounds = new LatLngBounds(southwest, northeast);
mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
}
 */

//{
//    "error_message" : "This IP, site or mobile application is not authorized to use this API key. Request received from IP address 2402:3a80:132a:d2c6:0:e:1d4f:b501, with empty referer",
//    "routes" : [],
//    "status" : "REQUEST_DENIED"
//}



/**   8-04-22
 *     private fun getPollyLineOnMap(origin: String, dest: String) {
// Getting URL to the Google Directions API
val url = getDirectionsUrl(origin, dest)
Log.e( "getPerror",url )
//        binding.progressBar.visibility = View.VISIBLE
val path: MutableList<List<LatLng>> = ArrayList()

val directionsRequest = object :
StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
try {
val jsonResponse = JSONObject(response)
// Get routes
val routes = jsonResponse.getJSONArray("routes")
val legs = routes.getJSONObject(0).getJSONArray("legs")
val steps = legs.getJSONObject(0).getJSONArray("steps")
for (i in 0 until steps.length()) {
val points =
steps.getJSONObject(i).getJSONObject("polyline").getString("points")
path.add(PolyUtil.decode(points))
}

for (i in 0 until path.size) {
map.addPolyline(PolylineOptions().addAll(path[i]).color(Color.BLUE))
}
//                    binding.progressBar.visibility = View.GONE
val origin1 = path[0][0]
val lastIndex = path[path.lastIndex].lastIndex
val destination = path[path.lastIndex][lastIndex]
var markerIcon =
getMarkerBitmapFromVectorRecourse(
requireContext(),
R.drawable.b_point_icon
)
map.addMarker(
MarkerOptions().position(origin1).title("Origin").icon(markerIcon)
)
markerIcon =
getMarkerBitmapFromVectorRecourse(
requireContext(),
R.drawable.a_point_icon
)
map.addMarker(
MarkerOptions().position(destination).title("Destination")
.icon(markerIcon)
)
val bounds = LatLngBounds.Builder()
.include(origin1)
.include(destination)
.build()
val padding = 140
map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
//                    binding.progressBar.visibility = View.GONE

} catch (e: Exception) {
e.printStackTrace()
//                    binding.progressBar.visibility = View.GONE
}
}, Response.ErrorListener { error ->
error.printStackTrace()
//                binding.progressBar.visibility = View.GONE
Log.e( "TAGgetPollyLineOnMap","unabletoloadmap" )
//                requireContext().toast("Unable to load route on map")
}) {}
val requestQueue = Volley.newRequestQueue(requireContext())
requestQueue.add(directionsRequest)

}

 */