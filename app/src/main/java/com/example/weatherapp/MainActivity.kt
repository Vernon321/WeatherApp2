package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

        val PERMISSION_ID = 42
        lateinit var mFusedLocationClient: FusedLocationProviderClient
        // weather url to get JSON
        var weather_url1 = ""
        var weather_url2 = ""
    var maincont : RelativeLayout? = null
    var imageHolder : RelativeLayout? = null
        private lateinit var btVar1: Button
        // api id for url
        var api_id1 = "f899e2146138cfa4ec1ea578f2a46c17"

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            maincont = findViewById<RelativeLayout>(R.id.maincontainer)
            imageHolder = findViewById<RelativeLayout>(R.id.imageHolder)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//            btVar1 = findViewById(R.id.btVar1)
//            btVar1.setOnClickListener {
                getLastLocation()
//            }

        }

        @SuppressLint("MissingPermission")
        private fun getLastLocation() {
            if (checkPermissions()) {
                if (isLocationEnabled()) {

                    mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            findViewById<TextView>(R.id.textViewTemp).text = location.latitude.toString()
                            findViewById<TextView>(R.id.txtViewWeather).text = location.longitude.toString()
//                            weather_url1 = "https://api.openweathermap.org/data/2.5/weather?lat=-25.979536&lon=28.25652&appid=f899e2146138cfa4ec1ea578f2a46c17"
                            weather_url1 = "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=$api_id1"
                            weather_url2 = "https://api.openweathermap.org/data/2.5/forecast?lat=${location.latitude}&lon=${location.longitude}&appid=$api_id1"
                            getTemp(weather_url1)
                            getForecast(weather_url2)
                        }
                    }
                } else {
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }
        }

        @SuppressLint("MissingPermission")
        private fun requestNewLocationData() {
            var mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = 0
            mLocationRequest.fastestInterval = 0
            mLocationRequest.numUpdates = 1

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            Looper.myLooper()?.let {
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    it
                )
            }
        }

    fun getDayOfWeek(date: String?): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).dayOfWeek.name
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    fun callerMethod() {
//        println(getDayOfWeek("06/02/2018")) //TUESDAY
    }

        private val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                var mLastLocation: Location = locationResult.lastLocation
//                findViewById<TextView>(R.id.txtViewMin).text = mLastLocation.latitude.toString()
//                findViewById<TextView>(R.id.txtViewMax).text = mLastLocation.longitude.toString()
//                weather_url1 = "https://api.weatherbit.io/v2.0/current?lat=${mLastLocation.latitude.toString()}%20&lon=${mLastLocation.longitude.toString()}&key=320b00d374684837bfd392762bcbff8c"
//                getTemp(weather_url1)
            }
        }

        private fun isLocationEnabled(): Boolean {
            var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
            )
        }


        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == PERMISSION_ID) {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLastLocation()
                }
            }
        }



        @SuppressLint("WrongViewCast")
        fun getTemp(weather_url1: String) {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url: String = weather_url1
            Log.e("lat", url)


            // Request a string response
            // from the provided URL.
            val stringReq = StringRequest(Request.Method.GET, url,
                { response ->
                    Log.e("lat", response.toString())





                    // get the JSON object
                    val obj = JSONObject(response)
                    val objForecast = JSONObject(response)
//
//                    // get the JSON object from the
//                    // array at index position 0
                    val obj2 = obj.getJSONObject("main")
                    Log.e("lat obj2", obj2.toString())

                    val arr = obj.getJSONArray("weather")
                    Log.e("lat obj1", arr.toString())

                    //                // get the JSON object from the
//                // array at index position 0
                    val obj3 = arr.getJSONObject(0)
                    Log.e("lat obj3", obj3.toString())

                    val F1TempDbl1 = obj2["temp"].toString().toDouble()
                    val F1TempCel1 = F1TempDbl1.toInt() - 273.15
                    val F1TemMain = F1TempCel1.toString().substringBefore(".") + "°C"

                    val F1TempDbl2 = obj2["temp_min"].toString().toDouble()
                    val F1TempCel2 = F1TempDbl2.toInt() - 273.15
                    val F1TemMin = F1TempCel2.toString().substringBefore(".") + "°C"

                    val F1TempDbl3 = obj2["temp_max"].toString().toDouble()
                    val F1TempCel3 = F1TempDbl3.toInt() - 273.15
                    val F1TemMax = F1TempCel3.toString().substringBefore(".") + "°C"

                    if (obj3["main"].toString() == "Clouds") {
                        maincont?.setBackgroundColor(Color.parseColor("#54717A"))
                        imageHolder?.background = (resources.getDrawable(R.drawable.forest_cloudy))
                    }

                    if (obj3["main"].toString() == "Clear") {
                        maincont?.setBackgroundColor(Color.parseColor("#54717A"))
                        imageHolder?.background = (resources.getDrawable(R.drawable.forest_cloudy))
                    }

                    if (obj3["main"].toString() == "Rainy") {
                        maincont?.setBackgroundColor(Color.parseColor("#57575D"))
                        imageHolder?.background = (resources.getDrawable(R.drawable.forest_rainy))
                    }

                    if (obj3["main"].toString() == "Sunny") {
                        maincont?.setBackgroundColor(Color.parseColor("#47AB2F"))
                        imageHolder?.background = (resources.getDrawable(R.drawable.forest_sunny))
                    }

                    // name using getString() function
                    findViewById<TextView>(R.id.textViewTemp).text = F1TemMain.toString()
                    findViewById<TextView>(R.id.txtViewWeather).text = obj3["main"].toString()
                    findViewById<TextView>(R.id.txtViewMin).text = F1TemMin.toString()
                    findViewById<TextView>(R.id.txtViewMax).text = F1TemMax.toString()
                    findViewById<TextView>(R.id.txtViewCurrent).text = F1TemMain.toString()

                },
                // In case of any error
                { findViewById<TextView>(R.id.textViewTemp)!!.text = "That didn't work!" })
            queue.add(stringReq)
        }

        fun getForecast(weather_url2: String) {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url: String = weather_url2
            Log.e("lat", url)


            // Request a string response
            // from the provided URL.
            val stringReq = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    Log.e("lat", response.toString())

                    // get the JSON object
                    val obj = JSONObject(response)
                    //val objForecast = JSONObject(response)
//


                    val arr = obj.getJSONArray("list")
                    Log.e("lat obj1", arr.toString())

                    //                // get the JSON object from the
//                // array at index position 0
                    val objF1 = arr.getJSONObject(0)
                    Log.e("lat obj3", objF1.toString())

                    val objF1Temp = objF1.getJSONObject("main")
                    Log.e("lat obj3", objF1Temp.toString())

                    val objF2 = arr.getJSONObject(8)
                    Log.e("lat obj3", objF2.toString())

                    val objF2Temp = objF2.getJSONObject("main")
                    Log.e("lat obj3", objF2Temp.toString())

                    val objF3 = arr.getJSONObject(16)
                    Log.e("lat obj3", objF3.toString())

                    val objF3Temp = objF3.getJSONObject("main")
                    Log.e("lat obj3", objF3Temp.toString())

                    val objF4 = arr.getJSONObject(24)
                    Log.e("lat obj3", objF4.toString())

                    val objF4Temp = objF4.getJSONObject("main")
                    Log.e("lat obj3", objF4Temp.toString())

                    val objF5 = arr.getJSONObject(32)
                    Log.e("lat obj3", objF5.toString())

                    val objF5Temp = objF5.getJSONObject("main")
                    Log.e("lat obj3", objF5Temp.toString())


                    val F1TempDbl1 = objF1Temp["temp"].toString().toDouble()
                    val F1TempCel1 = F1TempDbl1.toInt() - 273.15
                    val F1Tem = F1TempCel1.toString().substringBefore(".") + "°C"

                    val F1TempDbl2 = objF2Temp["temp"].toString().toDouble()
                    val F1TempCel2 = F1TempDbl2.toInt() - 273.15
                    val F2Tem = F1TempCel2.toString().substringBefore(".") + "°C"
                    val F1TempDbl3 = objF3Temp["temp"].toString().toDouble()
                    val F1TempCel3 = F1TempDbl3.toInt() - 273.15
                    val F3Tem = F1TempCel3.toString().substringBefore(".") + "°C"
                    val F1TempDbl4 = objF4Temp["temp"].toString().toDouble()
                    val F1TempCel4 = F1TempDbl4.toInt() - 273.15
                    val F4Tem = F1TempCel4.toString().substringBefore(".") + "°C"
                    val F1TempDbl5 = objF4Temp["temp"].toString().toDouble()
                    val F1TempCel5 = F1TempDbl5.toInt() - 273.15
                    val F5Tem = F1TempCel5.toString().substringBefore(".") + "°C"

                    // name using getString() function
                      findViewById<TextView>(R.id.txtViewF1Day).text = getDayOfWeek(objF1["dt_txt"].toString().dropLast(9))

//                          objF1["dt_txt"].toString()
                    findViewById<TextView>(R.id.txtViewF1Temp).text = F1Tem.toString()
                    findViewById<TextView>(R.id.txtViewF2Day).text = getDayOfWeek(objF2["dt_txt"].toString().dropLast(9))
                    findViewById<TextView>(R.id.txtViewF2Temp).text = F2Tem.toString()
                    findViewById<TextView>(R.id.txtViewF3Day).text = getDayOfWeek(objF3["dt_txt"].toString().dropLast(9))
                    findViewById<TextView>(R.id.txtViewF3Temp).text = F3Tem.toString()
                    findViewById<TextView>(R.id.txtViewF4Day).text = getDayOfWeek(objF4["dt_txt"].toString().dropLast(9))
                    findViewById<TextView>(R.id.txtViewF4Temp).text = F4Tem.toString()
                    findViewById<TextView>(R.id.txtViewF5Day).text = getDayOfWeek(objF5["dt_txt"].toString().dropLast(9))
                    findViewById<TextView>(R.id.txtViewF5Temp).text = F5Tem.toString()

                },
                // In case of any error
                Response.ErrorListener { findViewById<TextView>(R.id.textViewTemp)!!.text = "That didn't work!" })
            queue.add(stringReq)
        }



    }



































//    // weather url to get JSON
//    var weather_url1 = ""
//
//    // api id for url
//    var api_id1 = "320b00d374684837bfd392762bcbff8c"
//
//    private lateinit var textView: TextView
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var btVar1: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//
//        // link the textView in which the
//        // temperature will be displayed
//        textView = findViewById(R.id.textView)
//        btVar1 = findViewById(R.id.btVar1)
//
//
//        // create an instance of the Fused
//        // Location Provider Client
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        Log.e("lat", weather_url1)
//
//        // on clicking this button function to
//        // get the coordinates will be called
//        btVar1.setOnClickListener {
//            Log.e("lat", "onClick")
//            // function to find the coordinates
//            // of the last location
//          val myWeather_url  = obtainLocation()
//            getTemp(myWeather_url)
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun obtainLocation() : String {
//        Log.e("lat", "function")
//        // get the last location
//
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                // get the latitude and longitude
//                // and create the http URL
//                weather_url1 = "https://api.weatherbit.io/v2.0/current?lat=-25.74688173512398%20&lon=28.289486925704068&key=320b00d374684837bfd392762bcbff8c"
//
//
////                    "https://api.weatherbit.io/v2.0/current" //?" + "lat=" + location?.latitude + "&lon=" + location?.longitude + "&key=" + api_id1
//                Log.e("lat", weather_url1.toString())
//                // this function will
//                // fetch data from URL
////                getTemp(weather_url1)
//            }
//        return weather_url1
//    }
//
//    fun getTemp(weather_url : String) {
//
//        Log.e("lat", weather_url.toString())
//        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
//        val url: String = weather_url
//        Log.e("lat", url)
//
//        // Request a string response
//        // from the provided URL.
//        val stringReq = StringRequest(Request.Method.GET, url,
//            Response.Listener<String> { response ->
//                Log.e("lat", response.toString())
//
//                // get the JSON object
//                val obj = JSONObject(response)
//
//                // get the Array from obj of name - "data"
//                val arr = obj.getJSONArray("data")
//                Log.e("lat obj1", arr.toString())
//
//                // get the JSON object from the
//                // array at index position 0
//                val obj2 = arr.getJSONObject(0)
//                Log.e("lat obj2", obj2.toString())
//
//                // set the temperature and the city
//                // name using getString() function
//                textView.text = obj2.getString("temp") + " deg Celsius in " + obj2.getString("city_name")
//            },
//            // In case of any error
//            Response.ErrorListener { textView!!.text = "That didn't work!" })
//        queue.add(stringReq)
//    }
