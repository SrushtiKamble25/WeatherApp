@file:Suppress("FunctionName")

package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.airbnb.lottie.Lottie
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.airbnb.lottie.LottieAnimationView


class MainActivity : AppCompatActivity() {
//    private  lateinit var lottie: Lottie
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Jaipur")
        SearchCity()


    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?):Boolean{
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
// 87278e53aa0497fce915d58180f7bf0c               fbe328d56cf13854fd9e24b98eaa3cf8

    private fun fetchWeatherData(cityName:String) {
        val retrofit =Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"87278e53aa0497fce915d58180f7bf0c","metrix")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody= response.body()
                if(response.isSuccessful && responseBody !=null){
                    val temperature = responseBody.main.temp.toString()
                  //  Log.d("TAG", "onResponse: $temperature")
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunset= responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"

                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.textView16.text="$temperature °C"
                    binding.textView5.text= condition
                    binding.textView3.text="Max Temp: $maxTemp °C"
                    binding.textView4.text="Min Temp: $minTemp °C"
                    binding.textView8.text= "$humidity %"
                    binding.textView10.text= "$windSpeed m/s"
                    binding.textView14.text= "${time(sunRise)}"
                    binding.sunset.text= "${time(sunset)}"
                    binding.textView18.text= "$seaLevel hpa"
                    binding.textView13.text= condition
                    binding.textView7.text=  dayName(System.currentTimeMillis())
                        binding.textView6.text= date()
                    binding.textView1.text = cityName


                    changeImageAccordingToWeatherCondition(condition)


                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show();
            }

        })

        }

    private fun changeImageAccordingToWeatherCondition(conditions: String) {

        var lottie : LottieAnimationView = findViewById(R.id.imageView2)
        when(conditions){
            "Haze" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)

                  lottie.setAnimation(R.raw.cloud)

            }
        }
        lottie.playAnimation();
    }

    private fun date():String{

        val sdf = SimpleDateFormat("dd mmm yyy", Locale.getDefault())
        return sdf.format((Date()))

    }

    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("hh:mm",Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format((Date()))
    }
}

