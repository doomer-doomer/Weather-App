package com.current.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.app.assist.AssistContent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.current.weatherforecast.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.util.*


private lateinit var context: Context
var intent1: Intent? = null
private lateinit var locationManager: LocationManager
var gpsStatus = false

var current_temp=""
var current_speed=""
var current_pressure=""

var daynigth=""
var isday=""

var c1=""
var c2=""
var c3=""
var c4=""
var c5=""
var c6=""
var c7=""
var c8=""
var c9=""
var c10=""
var c11=""
var c12=""
var c13=""
var c14=""
var c15=""
var c16=""
var c17=""
var c18=""
var c19=""
var c20=""
var c21=""
var c22=""
var c23=""
var c24=""

public var lat:String = ""
public var long:String = ""
public var loc= ""
public var region_var = ""
public var country_var = ""
public var localtime = ""
public var celcius = ""
public var farenheit = ""
public var continent = ""
public var windspeedz=""
public var winddegree=""
public var winddir=""
public var pressure=""
public var precipitate=""
public var humid=""
public var cloudz=""
public var feelz=""
public var uvray=""
public var gustinfo=""
var moonrise=""
var sunrise=""


var hourlytemp:ArrayList<String>?=null
var hourlytime:ArrayList<String>?=null
var hourly:ArrayList<JSONObject>?=null

var windspeedzmph=""
var gustspeedmph=""

var progressBar:ProgressBar ?=null
private var handler: Handler? = null
private var myRunnable: Runnable? = null

private lateinit var mainBinding: ActivityMainBinding
private lateinit var mFusedLocationClient: FusedLocationProviderClient
private val permissionId = 2


private lateinit var recyclerView: RecyclerView
private lateinit var newarraylist:ArrayList<ItemHolder>
lateinit var imageId: Array<Int>
lateinit var weatherdata:Array<String>
lateinit var weatherdataf:Array<String>
lateinit var weatherdata1:Array<String>
lateinit var weatherdata2:Array<String>


private lateinit var forcastrecyclerView: RecyclerView
private lateinit var newarraylist2:ArrayList<DayforcastComponents>
lateinit var imageId2: ArrayList<Int>
lateinit var timedata:ArrayList<String>
lateinit var tempdata:Array<String>
lateinit var tempdataf:Array<String>
lateinit var conddata:Array<String>
lateinit var spddata:Array<String>
lateinit var spddatamile:Array<String>
lateinit var dirdata:Array<String>
lateinit var pressuredata:Array<String>
lateinit var pressuredatain:Array<String>

@SuppressLint("StaticFieldLeak")
lateinit var swipeRefreshLayout: SwipeRefreshLayout

var condition_director = 0

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //setContentView(R.layout.activity_main)
        var location = savedInstanceState?.getString("mykey")

        context = applicationContext
        checkGpsStatus()
        getLocation()
        //progressBar = findViewById(R.id.load)
        swipeRefreshLayout = findViewById(R.id.mainbg)
        progressBar?.getIndeterminateDrawable()?.setColorFilter(Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.MULTIPLY);

        forcastrecyclerView = findViewById(R.id.forcastrecycle)
        recyclerView = findViewById(R.id.mydata)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        if(!isOnline(context)){
            //progressBar?.setVisibility(View.VISIBLE);
            subloc.text = location
            Handler().postDelayed(Runnable {
               Toast.makeText(applicationContext,"Pull up to Refresh",Toast.LENGTH_SHORT).show()
            }, 7500)

        }

        settings.setOnClickListener{
            val intent = Intent(this,com.current.weatherforecast.SettingsActivity::class.java)
            startActivity(intent)

        }

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("mykey", celcius)
    }

    private fun set(){
        val intent = Intent(this,com.current.weatherforecast.SettingsActivity::class.java)
        startActivity(intent)
    }


    private fun checkGpsStatus() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun gpsStatus(view: View) {
        intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1);
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    @SuppressLint("MissingPermission", "SetTextI18n", "NotifyDataSetChanged")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                        val set_temperature = prefs.getString("temp","-1")
                        val set_speed = prefs.getString("spd","-1")
                        val set_pressure = prefs.getString("pres","-1")

                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        mainBinding.apply {
                            var lat= location.latitude
                            var long = location.longitude
                            loc = list[0].subLocality as String
                            //var lower = list[0].subLocality as String
                            //Log.i(TAG,lower.lowercase())
                            //forcastvolly(loc.lowercase())
                            forcastvolly(lat,long)
                            if (set_temperature != null && set_speed != null && set_pressure != null) {
                                current_temp = set_temperature
                                current_speed = set_speed
                                current_pressure = set_pressure
                                Log.i(TAG, current_pressure)
                            }

                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    public fun forcastvolly(lat:Double,long:Double){
        val queue2 = Volley.newRequestQueue(this)
        val url2 = "https://api.weatherapi.com/v1/forecast.json?key=62facabc43d84ee2857140159220812&q=${lat},${long}"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url2, null,
            { response ->
                try {
                    midload.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade))
                    animationView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade))
                    midload.visibility = View.INVISIBLE
                    animationView.visibility = View.INVISIBLE

                    val jsonObject: JSONObject = response
                    val locationz = jsonObject.getJSONObject("location")
                    region_var = locationz.getString("region")
                    country_var = locationz.getString("country")
                    localtime = locationz.getString("localtime")
                    continent = locationz.getString("tz_id")


                    val current = jsonObject.getJSONObject("current")
                    celcius = current.getString("temp_c")
                    farenheit = current.getString("temp_f")
                    windspeedz = current.getString("wind_kph")
                    winddegree = current.getString("wind_degree")
                    winddir = current.getString("wind_dir")
                    pressure = current.getString("pressure_mb")
                    precipitate = current.getString("precip_mm")
                    humid = current.getString("humidity")
                    uvray = current.getString("uv")
                    cloudz = current.getString("cloud")
                    feelz = current.getString("feelslike_c")
                    gustinfo = current.getString("gust_kph")
                    var windmile = current.getString("wind_mph")
                    var pressin = current.getString("pressure_in")
                    var gustmile = current.getString("gust_mph")
                    var vismile = current.getString("vis_miles")
                    //region.text = "$region_var"
                    //country.text = "$country_var"
                    var regionandcountry = "$region_var, $country_var"
                    country.text = regionandcountry
                    tzid.text = continent
                    wind_dirr.text = "Last Updated : $localtime"
                    //temperaturec.text = "$celcius °C"
                    val conversions: String? = intent.getStringExtra("Common")
                    val temp_data:String? = intent.getStringExtra("Temp")
                    val wind_data:String? = intent.getStringExtra("Speed")
                    val press_data:String? = intent.getStringExtra("Press")

                    //windspd.text = "Wind speed \n$windspeedz km/hr"
                    //winddeg.text = "Wind degree \n$winddegree"
                    //direc.text = "Wind direction \n$winddir"
                    //pressure_mg.text = "Wind pressure \n$pressure"
                    //precipitaion.text = "Precipitaion \n$precipitate"
                    //humidity.text = "Humidity \n$humid%"
                    ///uv.text = "UV Rays \n$uvray"
                    //cloud_per.text = "Cloud Density \n$cloudz %"
                    //feelc.text = "Feels like \n$feelz °C"
                    //gust.text = "Gust Speed \n$gustinfo km/hr"
                    subloc.text = loc


                    val condition = current.getJSONObject("condition")

                    //code.text = condition.getString("icon")
                    type.text = condition.getString("text")
                    val typez = condition.getString("text")

                    if(typez.equals("Sunny")){
                        subimg.setBackgroundResource(R.drawable.sunnyicon)
                        subimg.background.alpha = 100
                    }else if(typez.equals("Cloudy") || typez.equals("Partly cloudy") || typez.equals("Overcast")){
                        subimg.setBackgroundResource(R.drawable.cloudyicon)
                        subimg.background.alpha = 100
                    }else if(typez.equals("Mist")){
                        subimg.setBackgroundResource(R.drawable.misticon)
                        subimg.background.alpha = 100
                    }else{
                        subimg.setBackgroundResource(R.drawable.rainicon)
                        subimg.background.alpha = 100
                    }

                    if (condition.getString("icon").contains("night")) {
                        mainbg.setBackgroundResource(R.drawable.day)
                    } else if(condition.getString("icon").contains("day")){
                        mainbg.setBackgroundResource(R.drawable.daystart)
                    }else{
                        mainbg.setBackgroundResource(R.drawable.daystart)
                    }
                    var forecast = jsonObject.getJSONObject("forecast")
                    val forecastday = forecast.getJSONArray("forecastday")
                    var astro:JSONObject?=null
                    var hour:JSONArray?=null
                    for (i in 0 until forecastday.length()) {
                        forecast = forecastday.getJSONObject(i)
                        astro = forecast.getJSONObject("astro")
                        break
                    }


                    var firsttempc :JSONObject?=null
                    var secondtempc :JSONObject?=null
                    var thirdtempc :JSONObject?=null
                    var fourthtempc :JSONObject?=null
                    var fifthtempc :JSONObject?=null
                    var sixthtempc :JSONObject?=null
                    var seventhtempc :JSONObject?=null
                    var eighthtempc :JSONObject?=null
                    var ninthtempc :JSONObject?=null
                    var tenthtempc :JSONObject?=null
                    var eleventhtempc :JSONObject?=null
                    var twelevethtempc :JSONObject?=null
                    var thirteenthtempc :JSONObject?=null
                    var fourteentempc :JSONObject?=null
                    var fifteentempc :JSONObject?=null
                    var sixteentempc :JSONObject?=null
                    var seventeentempc :JSONObject?=null
                    var eightteentempc :JSONObject?=null
                    var nightenntempc :JSONObject?=null
                    var twentytempc :JSONObject?=null
                    var twentyonetempc :JSONObject?=null
                    var twentytwotempc :JSONObject?=null
                    var twentythreetempc :JSONObject?=null
                    var twentyfourtempc :JSONObject?=null


                    hour = forecast.getJSONArray("hour")
                    for (j in 0 until 1) {
                        firsttempc = hour.getJSONObject(0)
                        secondtempc = hour.getJSONObject(1)
                        thirdtempc = hour.getJSONObject(2)
                        fourthtempc = hour.getJSONObject(3)
                        fifthtempc = hour.getJSONObject(4)
                        sixthtempc = hour.getJSONObject(5)
                        seventhtempc = hour.getJSONObject(6)
                        eighthtempc = hour.getJSONObject(7)
                        ninthtempc = hour.getJSONObject(8)
                        tenthtempc = hour.getJSONObject(9)
                        eleventhtempc = hour.getJSONObject(10)
                        twelevethtempc = hour.getJSONObject(11)
                        thirteenthtempc = hour.getJSONObject(12)
                        fourteentempc = hour.getJSONObject(13)
                        fifteentempc = hour.getJSONObject(14)
                        sixteentempc = hour.getJSONObject(15)
                        seventeentempc = hour.getJSONObject(16)
                        eightteentempc = hour.getJSONObject(17)
                        nightenntempc = hour.getJSONObject(18)
                        twentytempc = hour.getJSONObject(19)
                        twentyonetempc = hour.getJSONObject(20)
                        twentytwotempc = hour.getJSONObject(21)
                        twentythreetempc = hour.getJSONObject(22)
                        twentyfourtempc= hour.getJSONObject(23)

                        val condition1 = fifthtempc.getJSONObject("condition")
                        c1 = condition1.getString("text")
                        val condition2 = secondtempc.getJSONObject("condition")
                        c2 = condition2.getString("text")
                        val condition3 = thirdtempc.getJSONObject("condition")
                        c3 = condition3.getString("text")
                        val condition4 = fourthtempc.getJSONObject("condition")
                        c4 = condition4.getString("text")
                        val condition5 = fifthtempc.getJSONObject("condition")
                        c5 = condition5.getString("text")
                        val condition6 = sixthtempc.getJSONObject("condition")
                        c6 = condition6.getString("text")
                        val condition7 = seventhtempc.getJSONObject("condition")
                        c7 = condition7.getString("text")
                        val condition8 = eighthtempc.getJSONObject("condition")
                        c8 = condition8.getString("text")
                        val condition9 = ninthtempc.getJSONObject("condition")
                        c9 = condition9.getString("text")
                        val condition10 = tenthtempc.getJSONObject("condition")
                        c10 = condition10.getString("text")
                        val condition11= eleventhtempc.getJSONObject("condition")
                        c11 = condition11.getString("text")
                        val condition12 = twelevethtempc.getJSONObject("condition")
                        c12 = condition12.getString("text")
                        val condition13 = thirteenthtempc.getJSONObject("condition")
                        c13 = condition13.getString("text")
                        val condition14 = fourteentempc.getJSONObject("condition")
                        c14 = condition14.getString("text")
                        val condition15 = fifteentempc.getJSONObject("condition")
                        c15 = condition15.getString("text")
                        val condition16 = sixteentempc.getJSONObject("condition")
                        c16 = condition16.getString("text")
                        val condition17 = seventeentempc.getJSONObject("condition")
                        c17 = condition17.getString("text")
                        val condition18 =eightteentempc.getJSONObject("condition")
                        c18 = condition18.getString("text")
                        val condition19 = nightenntempc.getJSONObject("condition")
                        c19 = condition19.getString("text")
                        val condition20 = twentytempc.getJSONObject("condition")
                        c20 = condition20.getString("text")
                        val condition21 = twentyonetempc.getJSONObject("condition")
                        c21 = condition21.getString("text")
                        val condition22 = twentytwotempc.getJSONObject("condition")
                        c22 = condition22.getString("text")
                        val condition23 = twentythreetempc.getJSONObject("condition")
                        c23 = condition23.getString("text")
                        val condition24 = twentyfourtempc.getJSONObject("condition")
                        c24 = condition24.getString("text")

                        spddata = arrayOf(
                            firsttempc.getString("wind_kph") + "km/hr",
                            secondtempc.getString("wind_kph")+ "km/hr",
                            thirdtempc.getString("wind_kph")+ "km/hr",
                            fourthtempc.getString("wind_kph")+ "km/hr",
                            fifthtempc.getString("wind_kph")+ "km/hr",
                            sixthtempc.getString("wind_kph")+ "km/hr",
                            seventhtempc.getString("wind_kph")+ "km/hr",
                            eighthtempc.getString("wind_kph")+ "km/hr",
                            ninthtempc.getString("wind_kph")+ "km/hr",
                            tenthtempc.getString("wind_kph")+ "km/hr",
                            eleventhtempc.getString("wind_kph")+ "km/hr",
                            twelevethtempc.getString("wind_kph")+ "km/hr",
                            thirteenthtempc.getString("wind_kph")+ "km/hr",
                            fourteentempc.getString("wind_kph")+ "km/hr",
                            fifteentempc.getString("wind_kph")+ "km/hr",
                            sixteentempc.getString("wind_kph")+ "km/hr",
                            seventeentempc.getString("wind_kph")+ "km/hr",
                            eightteentempc.getString("wind_kph")+ "km/hr",
                            nightenntempc.getString("wind_kph")+ "km/hr",
                            twentytempc.getString("wind_kph")+ "km/hr",
                            twentyonetempc.getString("wind_kph")+ "km/hr",
                            twentytwotempc.getString("wind_kph")+ "km/hr",
                            twentythreetempc.getString("wind_kph")+ "km/hr",
                            twentyfourtempc.getString("wind_kph")+ "km/hr"
                        )

                        spddatamile = arrayOf(
                            firsttempc.getString("wind_mph") + "m/hr",
                            secondtempc.getString("wind_mph")+ "m/hr",
                            thirdtempc.getString("wind_mph")+ "m/hr",
                            fourthtempc.getString("wind_mph")+ "m/hr",
                            fifthtempc.getString("wind_mph")+ "m/hr",
                            sixthtempc.getString("wind_mph")+ "m/hr",
                            seventhtempc.getString("wind_mph")+ "m/hr",
                            eighthtempc.getString("wind_mph")+ "m/hr",
                            ninthtempc.getString("wind_mph")+ "m/hr",
                            tenthtempc.getString("wind_mph")+ "m/hr",
                            eleventhtempc.getString("wind_mph")+ "m/hr",
                            twelevethtempc.getString("wind_mph")+ "m/hr",
                            thirteenthtempc.getString("wind_mph")+ "m/hr",
                            fourteentempc.getString("wind_mph")+ "m/hr",
                            fifteentempc.getString("wind_mph")+ "m/hr",
                            sixteentempc.getString("wind_mph")+ "m/hr",
                            seventeentempc.getString("wind_mph")+ "m/hr",
                            eightteentempc.getString("wind_mph")+ "m/hr",
                            nightenntempc.getString("wind_mph")+ "m/hr",
                            twentytempc.getString("wind_mph")+ "m/hr",
                            twentyonetempc.getString("wind_mph")+ "m/hr",
                            twentytwotempc.getString("wind_mph")+ "m/hr",
                            twentythreetempc.getString("wind_mph")+ "m/hr",
                            twentyfourtempc.getString("wind_kph")+ "m/hr"
                        )

                        timedata = arrayListOf(
                            firsttempc.getString("time"),
                            secondtempc.getString("time"),
                            thirdtempc.getString("time"),
                            fourthtempc.getString("time"),
                            fifthtempc.getString("time"),
                            sixthtempc.getString("time"),
                            seventhtempc.getString("time"),
                            eighthtempc.getString("time"),
                            ninthtempc.getString("time"),
                            tenthtempc.getString("time"),
                            eleventhtempc.getString("time"),
                            twelevethtempc.getString("time"),
                            thirteenthtempc.getString("time"),
                            fourteentempc.getString("time"),
                            fifteentempc.getString("time"),
                            sixteentempc.getString("time"),
                            seventeentempc.getString("time"),
                            eightteentempc.getString("time"),
                            nightenntempc.getString("time"),
                            twentytempc.getString("time"),
                            twentyonetempc.getString("time"),
                            twentytwotempc.getString("time"),
                            twentythreetempc.getString("time"),
                            twentyfourtempc.getString("time")
                        )

                        dirdata = arrayOf(
                            firsttempc.getString("wind_dir"),
                            secondtempc.getString("wind_dir"),
                            thirdtempc.getString("wind_dir"),
                            fourthtempc.getString("wind_dir"),
                            fifthtempc.getString("wind_dir"),
                            sixthtempc.getString("wind_dir"),
                            seventhtempc.getString("wind_dir"),
                            eighthtempc.getString("wind_dir"),
                            ninthtempc.getString("wind_dir"),
                            tenthtempc.getString("wind_dir"),
                            eleventhtempc.getString("wind_dir"),
                            twelevethtempc.getString("wind_dir"),
                            thirteenthtempc.getString("wind_dir"),
                            fourteentempc.getString("wind_dir"),
                            fifteentempc.getString("wind_dir"),
                            sixteentempc.getString("wind_dir"),
                            seventeentempc.getString("wind_dir"),
                            eightteentempc.getString("wind_dir"),
                            nightenntempc.getString("wind_dir"),
                            twentytempc.getString("wind_dir"),
                            twentyonetempc.getString("wind_dir"),
                            twentytwotempc.getString("wind_dir"),
                            twentythreetempc.getString("wind_dir"),
                            twentyfourtempc.getString("wind_dir")
                        )

                        pressuredata = arrayOf(
                            firsttempc.getString("pressure_mb") + "mb",
                            secondtempc.getString("pressure_mb")+ "mb",
                            thirdtempc.getString("pressure_mb")+ "mb",
                            fourthtempc.getString("pressure_mb")+ "mb",
                            fifthtempc.getString("pressure_mb")+ "mb",
                            sixthtempc.getString("pressure_mb")+ "mb",
                            seventhtempc.getString("pressure_mb")+ "mb",
                            eighthtempc.getString("pressure_mb")+ "mb",
                            ninthtempc.getString("pressure_mb")+ "mb",
                            tenthtempc.getString("pressure_mb")+ "mb",
                            eleventhtempc.getString("pressure_mb")+ "mb",
                            twelevethtempc.getString("pressure_mb")+ "mb",
                            thirteenthtempc.getString("pressure_mb")+ "mb",
                            fourteentempc.getString("pressure_mb")+ "mb",
                            fifteentempc.getString("pressure_mb")+ "mb",
                            sixteentempc.getString("pressure_mb")+ "mb",
                            seventeentempc.getString("pressure_mb")+ "mb",
                            eightteentempc.getString("pressure_mb")+ "mb",
                            nightenntempc.getString("pressure_mb")+ "mb",
                            twentytempc.getString("pressure_mb")+ "mb",
                            twentyonetempc.getString("pressure_mb")+ "mb",
                            twentytwotempc.getString("pressure_mb")+ "mb",
                            twentythreetempc.getString("pressure_mb")+ "mb",
                            twentyfourtempc.getString("pressure_mb")+ "mb"
                        )

                        pressuredatain = arrayOf(
                            firsttempc.getString("pressure_in") + "in",
                            secondtempc.getString("pressure_in")+ "in",
                            thirdtempc.getString("pressure_in")+ "in",
                            fourthtempc.getString("pressure_in")+ "in",
                            fifthtempc.getString("pressure_in")+ "in",
                            sixthtempc.getString("pressure_in")+ "in",
                            seventhtempc.getString("pressure_in")+ "in",
                            eighthtempc.getString("pressure_in")+ "in",
                            ninthtempc.getString("pressure_in")+ "in",
                            tenthtempc.getString("pressure_in")+ "in",
                            eleventhtempc.getString("pressure_in")+ "in",
                            twelevethtempc.getString("pressure_in")+ "in",
                            thirteenthtempc.getString("pressure_in")+ "in",
                            fourteentempc.getString("pressure_in")+ "in",
                            fifteentempc.getString("pressure_in")+ "in",
                            sixteentempc.getString("pressure_in")+ "in",
                            seventeentempc.getString("pressure_in")+ "in",
                            eightteentempc.getString("pressure_in")+ "in",
                            nightenntempc.getString("pressure_in")+ "in",
                            twentytempc.getString("pressure_in")+ "in",
                            twentyonetempc.getString("pressure_in")+ "in",
                            twentytwotempc.getString("pressure_in")+ "in",
                            twentythreetempc.getString("pressure_in")+ "in",
                            twentyfourtempc.getString("pressure_in")+ "in"
                        )


                        tempdata = arrayOf(
                            firsttempc.getString("temp_c") + "°C",
                            secondtempc.getString("temp_c")+ "°C",
                            thirdtempc.getString("temp_c")+ "°C",
                            fourthtempc.getString("temp_c")+ "°C",
                            fifthtempc.getString("temp_c")+ "°C",
                            sixthtempc.getString("temp_c")+ "°C",
                            seventhtempc.getString("temp_c")+ "°C",
                            eighthtempc.getString("temp_c")+ "°C",
                            ninthtempc.getString("temp_c")+ "°C",
                            tenthtempc.getString("temp_c")+ "°C",
                            eleventhtempc.getString("temp_c")+ "°C",
                            twelevethtempc.getString("temp_c")+ "°C",
                            thirteenthtempc.getString("temp_c")+ "°C",
                            fourteentempc.getString("temp_c")+ "°C",
                            fifteentempc.getString("temp_c")+ "°C",
                            sixteentempc.getString("temp_c")+ "°C",
                            seventeentempc.getString("temp_c")+ "°C",
                            eightteentempc.getString("temp_c")+ "°C",
                            nightenntempc.getString("temp_c")+ "°C",
                            twentytempc.getString("temp_c")+ "°C",
                            twentyonetempc.getString("temp_c")+ "°C",
                            twentytwotempc.getString("temp_c")+ "°C",
                            twentythreetempc.getString("temp_c")+ "°C",
                            twentyfourtempc.getString("temp_c")+ "°C"
                        )

                        tempdataf = arrayOf(
                            firsttempc.getString("temp_f") + "°F",
                            secondtempc.getString("temp_f")+ "°F",
                            thirdtempc.getString("temp_f")+ "°F",
                            fourthtempc.getString("temp_f")+ "°F",
                            fifthtempc.getString("temp_f")+ "°F",
                            sixthtempc.getString("temp_f")+ "°F",
                            seventhtempc.getString("temp_f")+ "°F",
                            eighthtempc.getString("temp_f")+ "°F",
                            ninthtempc.getString("temp_f")+ "°F",
                            tenthtempc.getString("temp_f")+ "°F",
                            eleventhtempc.getString("temp_f")+ "°F",
                            twelevethtempc.getString("temp_f")+ "°F",
                            thirteenthtempc.getString("temp_f")+ "°F",
                            fourteentempc.getString("temp_f")+ "°F",
                            fifteentempc.getString("temp_f")+ "°F",
                            sixteentempc.getString("temp_f")+ "°F",
                            seventeentempc.getString("temp_f")+ "°F",
                            eightteentempc.getString("temp_f")+ "°F",
                            nightenntempc.getString("temp_f")+ "°F",
                            twentytempc.getString("temp_f")+ "°F",
                            twentyonetempc.getString("temp_f")+ "°F",
                            twentytwotempc.getString("temp_f")+ "°F",
                            twentythreetempc.getString("temp_f")+ "°F",
                            twentyfourtempc.getString("temp_f")+ "°F"
                        )

                        conddata = arrayOf(
                            c1,
                            c2,
                            c3,
                            c4,
                            c5,
                            c6,
                            c7,
                            c8,
                            c9,
                            c10,
                            c11,
                            c12,
                            c13,
                            c14,
                            c15,
                            c16,
                            c17,
                            c18,
                            c19,
                            c20,
                            c21,
                            c22,
                            c23,
                            c24
                        )


                    }
                    var icon1:Int=0
                    var icon2:Int=0
                    var icon3:Int=0
                    var icon4:Int=0
                    var icon5:Int=0
                    var icon6:Int=0
                    var icon7:Int=0
                    var icon8:Int=0
                    var icon9:Int=0
                    var icon10:Int=0
                    var icon11:Int=0
                    var icon12:Int=0
                    var icon13:Int=0
                    var icon14:Int=0
                    var icon15:Int=0
                    var icon16:Int=0
                    var icon17:Int=0
                    var icon18:Int=0
                    var icon19:Int=0
                    var icon20:Int=0
                    var icon21:Int=0
                    var icon22:Int=0
                    var icon23:Int=0
                    var icon24:Int=0

                    if(c1.equals("Clear")){
                        icon1 = R.drawable.clearskyicon
                    }else if(c1.equals("Sunny")){
                        icon1 = R.drawable.sunnyicon
                    }else if(c1.equals("Partly cloudy") || c1.equals("Cloudy") || c1.equals("Overcast")){
                        icon1 = R.drawable.cloudyicon
                    }else if(c1.equals("Mist")){
                        icon1 = R.drawable.misticon
                    }else if(c1.contains("rain")){
                        icon1 = R.drawable.rainicon
                    }else{
                        icon1 = R.drawable.clearskyicon
                    }

                    if(c2.equals("Clear")){
                        icon2 = R.drawable.clearskyicon
                    }else if(c2.equals("Sunny")){
                        icon2 = R.drawable.sunnyicon
                    }else if(c2.equals("Partly cloudy") || c2.equals("Cloudy") || c2.equals("Overcast")){
                        icon2 = R.drawable.cloudyicon
                    }else if(c2.equals("Mist")){
                        icon2 = R.drawable.misticon
                    }else if(c2.contains("rain")){
                        icon2 = R.drawable.rainicon
                    }else{
                        icon2 = R.drawable.clearskyicon
                    }

                    if(c3.equals("Clear")){
                        icon3 = R.drawable.clearskyicon
                    }else if(c3.equals("Sunny")){
                        icon3 = R.drawable.sunnyicon
                    }else if(c3.equals("Partly cloudy") || c3.equals("Cloudy") || c3.equals("Overcast")){
                        icon3 = R.drawable.cloudyicon
                    }else if(c3.equals("Mist")){
                        icon3 = R.drawable.misticon
                    }else if(c3.contains("rain")){
                        icon3 = R.drawable.rainicon
                    }else{
                        icon1 = R.drawable.clearskyicon
                    }

                    if(c4.equals("Clear")){
                        icon4 = R.drawable.clearskyicon
                    }else if(c4.equals("Sunny")){
                        icon4 = R.drawable.sunnyicon
                    }else if(c4.equals("Partly cloudy") || c4.equals("Cloudy") || c4.equals("Overcast")){
                        icon4 = R.drawable.cloudyicon
                    }else if(c4.equals("Mist")){
                        icon4 = R.drawable.misticon
                    }else if(c4.contains("rain")){
                        icon4 = R.drawable.rainicon
                    }else{
                        icon4 = R.drawable.clearskyicon
                    }

                    if(c5.equals("Clear")){
                        icon5 = R.drawable.clearskyicon
                    }else if(c5.equals("Sunny")){
                        icon5 = R.drawable.sunnyicon
                    }else if(c5.equals("Partly cloudy") || c5.equals("Cloudy") || c5.equals("Overcast")){
                        icon5 = R.drawable.cloudyicon
                    }else if(c5.equals("Mist")){
                        icon5 = R.drawable.misticon
                    }else if(c5.contains("rain")){
                        icon5 = R.drawable.rainicon
                    }else{
                        icon5 = R.drawable.clearskyicon
                    }

                    if(c6.equals("Clear")){
                        icon6 = R.drawable.clearskyicon
                    }else if(c6.equals("Sunny")){
                        icon6 = R.drawable.sunnyicon
                    }else if(c6.equals("Partly cloudy") || c6.equals("Cloudy") || c6.equals("Overcast")){
                        icon6 = R.drawable.cloudyicon
                    }else if(c6.equals("Mist")){
                        icon6 = R.drawable.misticon
                    }else if(c6.contains("rain")){
                        icon6 = R.drawable.rainicon
                    }else{
                        icon6 = R.drawable.clearskyicon
                    }

                    if(c7.equals("Clear")){
                        icon7 = R.drawable.clearskyicon
                    }else if(c7.equals("Sunny")){
                        icon7 = R.drawable.sunnyicon
                    }else if(c7.equals("Partly cloudy") || c7.equals("Cloudy") || c7.equals("Overcast")){
                        icon7 = R.drawable.cloudyicon
                    }else if(c7.equals("Mist")){
                        icon7 = R.drawable.misticon
                    }else if(c7.contains("rain")){
                        icon7 = R.drawable.rainicon
                    }else{
                        icon7 = R.drawable.clearskyicon
                    }

                    if(c8.equals("Clear")){
                        icon8 = R.drawable.clearskyicon
                    }else if(c8.equals("Sunny")){
                        icon8 = R.drawable.sunnyicon
                    }else if(c8.equals("Partly cloudy") || c8.equals("Cloudy") || c8.equals("Overcast")){
                        icon8 = R.drawable.cloudyicon
                    }else if(c8.equals("Mist")){
                        icon8 = R.drawable.misticon
                    }else if(c8.contains("rain")){
                        icon8 = R.drawable.rainicon
                    }else{
                        icon8 = R.drawable.clearskyicon
                    }

                    if(c9.equals("Clear")){
                        icon9 = R.drawable.clearskyicon
                    }else if(c9.equals("Sunny")){
                        icon9 = R.drawable.sunnyicon
                    }else if(c9.equals("Partly cloudy") || c9.equals("Cloudy") || c9.equals("Overcast")){
                        icon9 = R.drawable.cloudyicon
                    }else if(c9.equals("Mist")){
                        icon9 = R.drawable.misticon
                    }else if(c9.contains("rain")){
                        icon9 = R.drawable.rainicon
                    }else{
                        icon9 = R.drawable.clearskyicon
                    }

                    if(c10.equals("Clear")){
                        icon10 = R.drawable.clearskyicon
                    }else if(c10.equals("Sunny")){
                        icon10 = R.drawable.sunnyicon
                    }else if(c10.equals("Partly cloudy") || c10.equals("Cloudy") || c10.equals("Overcast")){
                        icon10 = R.drawable.cloudyicon
                    }else if(c10.equals("Mist")){
                        icon10 = R.drawable.misticon
                    }else if(c10.contains("rain")){
                        icon10 = R.drawable.rainicon
                    }else{
                        icon10 = R.drawable.clearskyicon
                    }

                    if(c11.equals("Clear")){
                        icon11 = R.drawable.clearskyicon
                    }else if(c11.equals("Sunny")){
                        icon11 = R.drawable.sunnyicon
                    }else if(c11.equals("Partly cloudy") || c11.equals("Cloudy") || c11.equals("Overcast")){
                        icon11 = R.drawable.cloudyicon
                    }else if(c11.equals("Mist")){
                        icon11 = R.drawable.misticon
                    }else if(c11.contains("rain")){
                        icon11 = R.drawable.rainicon
                    }else{
                        icon11 = R.drawable.clearskyicon
                    }

                    if(c12.equals("Clear")){
                        icon12 = R.drawable.clearskyicon
                    }else if(c12.equals("Sunny")){
                        icon12 = R.drawable.sunnyicon
                    }else if(c12.equals("Partly cloudy") || c12.equals("Cloudy") || c12.equals("Overcast")){
                        icon12 = R.drawable.cloudyicon
                    }else if(c12.equals("Mist")){
                        icon12 = R.drawable.misticon
                    }else if(c12.contains("rain")){
                        icon12 = R.drawable.rainicon
                    }else{
                        icon12 = R.drawable.clearskyicon
                    }

                    if(c13.equals("Clear")){
                        icon13 = R.drawable.clearskyicon
                    }else if(c13.equals("Sunny")){
                        icon13 = R.drawable.sunnyicon
                    }else if(c13.equals("Partly cloudy") || c13.equals("Cloudy") || c13.equals("Overcast")){
                        icon13 = R.drawable.cloudyicon
                    }else if(c13.equals("Mist")){
                        icon13 = R.drawable.misticon
                    }else if(c13.contains("rain")){
                        icon13 = R.drawable.rainicon
                    }else{
                        icon13 = R.drawable.clearskyicon
                    }

                    if(c14.equals("Clear")){
                        icon14 = R.drawable.clearskyicon
                    }else if(c14.equals("Sunny")){
                        icon14 = R.drawable.sunnyicon
                    }else if(c14.equals("Partly cloudy") || c14.equals("Cloudy") || c14.equals("Overcast")){
                        icon14 = R.drawable.cloudyicon
                    }else if(c14.equals("Mist")){
                        icon14 = R.drawable.misticon
                    }else if(c14.contains("rain")){
                        icon14 = R.drawable.rainicon
                    }else{
                        icon14 = R.drawable.clearskyicon
                    }

                    if(c15.equals("Clear")){
                        icon15 = R.drawable.clearskyicon
                    }else if(c15.equals("Sunny")){
                        icon15 = R.drawable.sunnyicon
                    }else if(c15.equals("Partly cloudy") || c15.equals("Cloudy") || c15.equals("Overcast")){
                        icon15 = R.drawable.cloudyicon
                    }else if(c15.equals("Mist")){
                        icon15 = R.drawable.misticon
                    }else if(c15.contains("rain")){
                        icon15 = R.drawable.rainicon
                    }else{
                        icon15 = R.drawable.clearskyicon
                    }

                    if(c16.equals("Clear")){
                        icon16 = R.drawable.clearskyicon
                    }else if(c16.equals("Sunny")){
                        icon16 = R.drawable.sunnyicon
                    }else if(c16.equals("Partly cloudy") || c16.equals("Cloudy") || c16.equals("Overcast")){
                        icon16 = R.drawable.cloudyicon
                    }else if(c16.equals("Mist")){
                        icon16 = R.drawable.misticon
                    }else if(c16.contains("rain")){
                        icon16 = R.drawable.rainicon
                    }else{
                        icon16 = R.drawable.clearskyicon
                    }

                    if(c17.equals("Clear")){
                        icon17 = R.drawable.clearskyicon
                    }else if(c17.equals("Sunny")){
                        icon17 = R.drawable.sunnyicon
                    }else if(c17.equals("Partly cloudy") || c17.equals("Cloudy") || c17.equals("Overcast")){
                        icon17 = R.drawable.cloudyicon
                    }else if(c17.equals("Mist")){
                        icon17 = R.drawable.misticon
                    }else if(c17.contains("rain")){
                        icon17 = R.drawable.rainicon
                    }else{
                        icon17 = R.drawable.clearskyicon
                    }

                    if(c18.equals("Clear")){
                        icon18 = R.drawable.clearskyicon
                    }else if(c18.equals("Sunny")){
                        icon18 = R.drawable.sunnyicon
                    }else if(c18.equals("Partly cloudy") || c18.equals("Cloudy") || c18.equals("Overcast")){
                        icon18 = R.drawable.cloudyicon
                    }else if(c18.equals("Mist")){
                        icon18 = R.drawable.misticon
                    }else if(c18.contains("rain")){
                        icon18 = R.drawable.rainicon
                    }else{
                        icon18 = R.drawable.clearskyicon
                    }

                    if(c19.equals("Clear")){
                        icon19 = R.drawable.clearskyicon
                    }else if(c19.equals("Sunny")){
                        icon19 = R.drawable.sunnyicon
                    }else if(c19.equals("Partly cloudy") || c19.equals("Cloudy") || c19.equals("Overcast")){
                        icon19 = R.drawable.cloudyicon
                    }else if(c19.equals("Mist")){
                        icon19 = R.drawable.misticon
                    }else if(c19.contains("rain")){
                        icon19 = R.drawable.rainicon
                    }else{
                        icon19 = R.drawable.clearskyicon
                    }

                    if(c20.equals("Clear")){
                        icon20 = R.drawable.clearskyicon
                    }else if(c20.equals("Sunny")){
                        icon20 = R.drawable.sunnyicon
                    }else if(c20.equals("Partly cloudy") || c20.equals("Cloudy") || c20.equals("Overcast")){
                        icon20 = R.drawable.cloudyicon
                    }else if(c20.equals("Mist")){
                        icon20 = R.drawable.misticon
                    }else if(c20.contains("rain")){
                        icon20 = R.drawable.rainicon
                    }else{
                        icon20 = R.drawable.clearskyicon
                    }

                    if(c21.equals("Clear")){
                        icon21 = R.drawable.clearskyicon
                    }else if(c21.equals("Sunny")){
                        icon21 = R.drawable.sunnyicon
                    }else if(c21.equals("Partly cloudy") || c21.equals("Cloudy") || c21.equals("Overcast")){
                        icon21 = R.drawable.cloudyicon
                    }else if(c21.equals("Mist")){
                        icon21 = R.drawable.misticon
                    }else if(c21.contains("rain")){
                        icon21 = R.drawable.rainicon
                    }else{
                        icon21 = R.drawable.clearskyicon
                    }

                    if(c22.equals("Clear")){
                        icon22 = R.drawable.clearskyicon
                    }else if(c22.equals("Sunny")){
                        icon22 = R.drawable.sunnyicon
                    }else if(c22.equals("Partly cloudy") || c22.equals("Cloudy") || c22.equals("Overcast")){
                        icon22 = R.drawable.cloudyicon
                    }else if(c22.equals("Mist")){
                        icon22 = R.drawable.misticon
                    }else if(c22.contains("rain")){
                        icon22 = R.drawable.rainicon
                    }else{
                        icon22 = R.drawable.clearskyicon
                    }

                    if(c23.equals("Clear")){
                        icon23 = R.drawable.clearskyicon
                    }else if(c23.equals("Sunny")){
                        icon23 = R.drawable.sunnyicon
                    }else if(c23.equals("Partly cloudy") || c23.equals("Cloudy") || c23.equals("Overcast")){
                        icon23 = R.drawable.cloudyicon
                    }else if(c23.equals("Mist")){
                        icon23 = R.drawable.misticon
                    }else if(c23.contains("rain")){
                        icon23 = R.drawable.rainicon
                    }else{
                        icon23 = R.drawable.clearskyicon
                    }

                    if(c24.equals("Clear")){
                        icon24 = R.drawable.clearskyicon
                    }else if(c24.equals("Sunny")){
                        icon24 = R.drawable.sunnyicon
                    }else if(c24.equals("Partly cloudy") || c24.equals("Cloudy") || c24.equals("Overcast")){
                        icon24 = R.drawable.cloudyicon
                    }else if(c24.equals("Mist")){
                        icon24 = R.drawable.misticon
                    }else if(c24.contains("rain")){
                        icon24 = R.drawable.rainicon
                    }else{
                        icon24 = R.drawable.clearskyicon
                    }
                    imageId2 = arrayListOf(
                        icon1,
                        icon2,
                        icon3,
                        icon4,
                        icon5,
                        icon6,
                        icon7,
                        icon8,
                        icon9,
                        icon10,
                        icon11,
                        icon12,
                        icon13,
                        icon14,
                        icon15,
                        icon16,
                        icon17,
                        icon18,
                        icon19,
                        icon20,
                        icon21,
                        icon22,
                        icon23,
                        icon24
                    )

                    imageId = arrayOf(
                        R.drawable.wind,
                        R.drawable.degree,
                        R.drawable.directions,
                        R.drawable.rain,
                        R.drawable.cloud,
                        R.drawable.whirlwind,
                        R.drawable.humidity,
                        R.drawable.gauge,
                        R.drawable.rays,
                        R.drawable.globalwarming
                    )

                    weatherdata = arrayOf(
                        "Wind Speed \n$windspeedz  km/hr",
                        "Wind Degree \n$winddegree °",
                        "Wind Direction \n$winddir",
                        "Precipitation \n$precipitate %",
                        "Cloudy \n$cloudz %",
                        "Gust \n$gustinfo km/hr",
                        "Humidity \n$humid %",
                        "Pressure \n$pressure mb",
                        "UV Rays\n$uvray",
                        "Feels like \n$feelz °C"
                    )

                    weatherdataf = arrayOf(
                        "Wind Speed \n$windspeedz  km/hr",
                        "Wind Degree \n$winddegree °",
                        "Wind Direction \n$winddir",
                        "Precipitation \n$precipitate %",
                        "Cloudy \n$cloudz %",
                        "Gust \n$gustmile m/hr",
                        "Humidity \n$humid %",
                        "Pressure \n$pressin in",
                        "UV Rays\n$uvray",
                        "Feels like \n$feelz °F"
                    )

                    weatherdata1 = arrayOf(
                        "Wind Speed \n$windmile  m/hr",
                        "Wind Degree \n$winddegree °",
                        "Wind Direction \n$winddir",
                        "Precipitation \n$precipitate %",
                        "Cloudy \n$cloudz %",
                        "Gust \n$gustinfo km/hr",
                        "Humidity \n$humid %",
                        "Pressure \n$pressure mb",
                        "UV Rays\n$uvray",
                        "Feels like \n$feelz °C"
                    )

                    weatherdata2 = arrayOf(
                        "Wind Speed \n$windmile  m/hr",
                        "Wind Degree \n$winddegree °",
                        "Wind Direction \n$winddir",
                        "Precipitation \n$precipitate %",
                        "Cloudy \n$cloudz %",
                        "Gust \n$gustmile m/hr",
                        "Humidity \n$humid %",
                        "Pressure \n$pressin in",
                        "UV Rays\n$uvray",
                        "Feels like \n$feelz °F"
                    )

                    if (astro != null) {
                        var sunvar = astro.getString("sunrise")
                        sun.text= "Sunrise \n$sunvar"
                    }
                    if (astro != null) {
                        var moonvar = astro.getString("moonrise")
                        moon.text = "Moonrise \n$moonvar"
                    }

                    var daydata:JSONObject?=null
                    for (i in 0 until forecastday.length()) {
                        forecast = forecastday.getJSONObject(i)
                        daydata = forecast.getJSONObject("day")
                        break
                    }

                    if (daydata != null) {
                        var maxtemp = daydata.getString("maxtemp_c")
                        var mintemp = daydata.getString("mintemp_c")
                        var maxtempf = daydata.getString("maxtemp_f")
                        var mintempf = daydata.getString("mintemp_f")
                        maxmin.text = "$maxtemp°C/$mintemp°C"
                        var avgtemp = daydata.getString("avgtemp_c")
                        windspd.text = "Temperature \n$avgtemp°C"
                        var avgtempf = daydata.getString("avgtemp_f")
                        var maxwind = daydata.getString("maxwind_kph")
                        var minwind = daydata.getString("maxwind_mph")
                        humidity.text = "Wind Speed\n$maxwind km/hr"
                        var chanceofrain = daydata.getString("daily_chance_of_rain")
                        winddeg.text = "Probability \n$chanceofrain %"
                        var precip = daydata.getString("totalprecip_mm")
                        cloud_per.text = "Precipitation \n$precip mm"
                        var avgvis = daydata.getString("avgvis_km")
                        feelc.text = "Visibility \n$avgvis km"
                        var avghum = daydata.getString("totalprecip_mm")
                        direc.text = "Humidity \n$avghum %"


                        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                        recyclerView.setHasFixedSize(true)
                        newarraylist = arrayListOf<ItemHolder>()


                        forcastrecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                        forcastrecyclerView.setHasFixedSize(true)
                        newarraylist2 = arrayListOf<DayforcastComponents>()


                        if(current_temp.equals("temp") && current_speed.equals("spd") && current_pressure.equals("pres")){
                            temperaturec.text="$celcius°C"
                            maxmin.text = "$maxtemp°C/$mintemp°C"
                            windspd.text = "Temperature \n$avgtemp°C"

                            humidity.text = "Wind Speed\n$maxwind km/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdata[i], imageId[i])
                                newarraylist.add(objz)
                            }

                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdata[i], conddata[i], spddata[i], dirdata[i], pressuredata[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else if(current_temp.equals("temp") && current_speed.equals("spd") && current_pressure.equals("pres_all")){
                            temperaturec.text="$celcius°C"
                            maxmin.text = "$maxtemp°C/$mintemp°C"
                            windspd.text = "Temperature \n$avgtemp°C"

                            humidity.text = "Wind Speed\n$maxwind km/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdataf[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdata[i], conddata[i], spddata[i], dirdata[i], pressuredatain[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else if(current_temp.equals("temp") && current_speed.equals("spd_all") && current_pressure.equals("pres")){
                            temperaturec.text="$celcius°C"
                            maxmin.text = "$maxtemp°C/$mintemp°C"
                            windspd.text = "Temperature \n$avgtemp°C"

                            humidity.text = "Wind Speed\n$minwind m/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdata1[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdata[i], conddata[i], spddatamile[i], dirdata[i], pressuredata[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else if(current_temp.equals("temp") && current_speed.equals("spd_all") && current_pressure.equals("pres_all")){
                            temperaturec.text="$celcius°C"
                            temperaturec.text="$celcius °C"
                            maxmin.text = "$maxtemp°C/$mintemp°C"
                            windspd.text = "Temperature \n$avgtemp°C"

                            humidity.text = "Wind Speed\n$minwind m/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdata2[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdata[i], conddata[i], spddatamile[i], dirdata[i], pressuredatain[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else if(current_temp.equals("temp_all") && current_speed.equals("spd") && current_pressure.equals("pres")){
                            temperaturec.text="$farenheit°F"
                            maxmin.text = "$maxtempf°F/$mintempf°F"
                            windspd.text = "Temperature \n$avgtempf°F"

                            humidity.text = "Wind Speed\n$maxwind km/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdata[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdataf[i], conddata[i], spddata[i], dirdata[i], pressuredata[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else if(current_temp.equals("temp_all") && current_speed.equals("spd") && current_pressure.equals("pres_all")){
                            temperaturec.text="$farenheit°F"
                            maxmin.text = "$maxtempf°F/$mintempf°F"
                            windspd.text = "Temperature \n$avgtempf°F"

                            humidity.text = "Wind Speed\n$maxwind km/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdataf[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdataf[i], conddata[i], spddata[i], dirdata[i], pressuredatain[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else if(current_temp.equals("temp_all") && current_speed.equals("spd_all") && current_pressure.equals("pres")){
                            temperaturec.text="$farenheit°F"
                            maxmin.text = "$maxtempf°F/$mintempf°F"
                            windspd.text = "Temperature \n$avgtempf°F"

                            humidity.text = "Wind Speed\n$minwind m/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdata1[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdataf[i], conddata[i], spddatamile[i], dirdata[i], pressuredata[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else if(current_temp.equals("temp_all") && current_speed.equals("spd_all") && current_pressure.equals("pres_all")){
                            temperaturec.text="$farenheit°F"
                            maxmin.text = "$maxtempf°F/$mintempf°F"
                            windspd.text = "Temperature \n$avgtempf°F"

                            humidity.text = "Wind Speed\n$minwind m/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdata2[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdataf[i], conddata[i], spddatamile[i], dirdata[i], pressuredatain[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }else{
                            temperaturec.text="$celcius°C"
                            maxmin.text = "$maxtemp°C/$mintemp°C"
                            windspd.text = "Temperature \n$avgtemp°C"

                            humidity.text = "Wind Speed\n$maxwind km/hr"
                            for(i in imageId.indices){
                                val objz = ItemHolder(weatherdata[i], imageId[i])
                                newarraylist.add(objz)
                            }
                            for(i in timedata.indices){
                                val objz = DayforcastComponents(timedata[i], tempdata[i], conddata[i], spddata[i], dirdata[i], pressuredata[i], imageId2[i])
                                newarraylist2.add(objz)
                            }
                        }

                    }

                    recyclerView.adapter = Adapter(newarraylist)

                    forcastrecyclerView.adapter = ForecastAdapter(newarraylist2)

                }catch (e2: JSONException){
                    e2.printStackTrace()
                }
            },
            { error ->
                val response = error.networkResponse
                if (error is ServerError && response != null) {
                    try {
                        Toast.makeText(applicationContext,"Pull up to Refresh",Toast.LENGTH_SHORT).show()
                    } catch (e1: UnsupportedEncodingException) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace()
                    } catch (e2: JSONException) {
                        // returned data is not JSONObject?
                        e2.printStackTrace()
                    }
                }
            }
        )

        queue2.add(jsonObjectRequest)




    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    override fun onProvideAssistContent(outContent: AssistContent) {
        super.onProvideAssistContent(outContent)
        outContent.setWebUri(Uri.parse("https://www.weatherapi.com/weather/q/$loc"))
    }


}




