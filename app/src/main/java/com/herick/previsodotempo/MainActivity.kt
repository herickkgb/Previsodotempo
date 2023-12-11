package com.herick.previsodotempo

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.herick.previsodotempo.Constantes.Const
import com.herick.previsodotempo.CountryCodeResolver.CountryCodeResolver
import com.herick.previsodotempo.databinding.ActivityMainBinding
import com.herick.previsodotempo.model.Main
import com.herick.previsodotempo.services.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var paises: CountryCodeResolver = CountryCodeResolver()



    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.parseColor("#042444")


        binding.switchModo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#000000"))
                binding.containerInfo.setBackgroundResource(R.drawable.contaniner_container_info_escuro)
                binding.txtInformacoes1.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#000000"))
                binding.txtTituloInfo.setTextColor(Color.parseColor("#000000"))
                window.statusBarColor = Color.parseColor("#000000")
                binding.btnBuscar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.btnBuscar.setTextColor(Color.parseColor("#000000"))
            } else if (!isChecked) {
                binding.containerPrincipal.setBackgroundResource(R.drawable.border_view)
                binding.containerInfo.setBackgroundResource(R.drawable.borda_container_info)
                binding.txtInformacoes1.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtTituloInfo.setTextColor(Color.parseColor("#FFFFFF"))
                window.statusBarColor = Color.parseColor("#042444")
                binding.btnBuscar.setBackgroundColor(Color.parseColor("#042444"))
                binding.btnBuscar.setTextColor(Color.parseColor("#FFFFFF"))

            }
        }



        binding.btnBuscar.setOnClickListener {
            val cidadeBuscar = binding.editText.text.toString()


            binding.progressBar.visibility = View.VISIBLE

            val retrofit: Api = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build()
                .create(Api::class.java)

            retrofit.weatherMap(cidadeBuscar, Const.API_KEY).enqueue(object : Callback<Main> {
                override fun onResponse(call: Call<Main>, response: Response<Main>) {
                    if (response.isSuccessful) {
                        respostaServidor(response)
                    } else {
                        Toast.makeText(applicationContext, "Cidade Inválida...", Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Main>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Erro fatal de servidor...",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            })
        }

    }

    override fun onResume() {
        super.onResume()

        binding.progressBar.visibility = View.VISIBLE

        val retrofit: Api = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(Api::class.java)

        retrofit.weatherMap("Brasilia", Const.API_KEY).enqueue(object : Callback<Main> {
            override fun onResponse(call: Call<Main>, response: Response<Main>) {
                if (response.isSuccessful) {
                    respostaServidor(response)
                } else {
                    Toast.makeText(applicationContext, "Cidade Inválida...", Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Main>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro fatal de servidor...", Toast.LENGTH_LONG)
                    .show()
                binding.progressBar.visibility = View.GONE
            }
        })


    }

    @SuppressLint("SetTextI18n")
    private fun respostaServidor(response: Response<Main>) {
        val main = response.body()!!.main
        val temp = main.get("temp")?.toString() ?: "N/A"
        val temMin = main.get("temp_min")?.toString() ?: "N/A"
        val temMax = main.get("temp_max")?.toString() ?: "N/A"
        val humidity = main.get("humidity")?.toString() ?: "N/A"
        var paisNomeCompleto = ""


        val sys = response.body()!!.sys
        val country = sys.get("country").asString

        val weather = response.body()!!.weather
        val main_weather = weather[0].main
        val description = weather[0].description
        val name = response.body()!!.name

        val temp_c = (temp.toDouble() - 273.15)
        val temp_min_c = (temMin.toDouble() - 273.15)
        val temp_max_c = (temMax.toDouble() - 273.15)

        val decimalFormat = DecimalFormat("#")
        val tempFormatted = decimalFormat.format(temp_c)
        val tempMinFormatted = decimalFormat.format(temp_min_c)
        val tempMaxFormatted = decimalFormat.format(temp_max_c)

        paisNomeCompleto = paises.resolveCountryName(country)


        if (main_weather.equals("Clouds") && description.equals("few clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.flewclouds)
        } else if (main_weather.equals("Clouds") && description.equals("scattered clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        } else if (main_weather.equals("Clouds") && description.equals("broken clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        } else if (main_weather.equals("Clouds") && description.equals("overcast clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        } else if (main_weather.equals("Clear") && description.equals("clear sky")) {
            binding.imgClima.setBackgroundResource(R.drawable.clearsky)
        } else if (main_weather.equals("Snow")) {
            binding.imgClima.setBackgroundResource(R.drawable.snow)
        } else if (main_weather.equals("Rain")) {
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather.equals("Drizzle")) {
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather.equals("Thunderstorm")) {
            binding.imgClima.setBackgroundResource(R.drawable.trunderstorm)
        }

        val descricaoClima: String = when (description.lowercase(Locale.ROOT)) {
            "clear sky" -> "Céu Limpo"
            "few clouds" -> "Poucas Nuvens"
            "scattered clouds" -> "Nuvens Dispersas"
            "broken clouds" -> "Nuvens Quebradas"
            "shower rain" -> "Chuva de Curta Duração"
            "rain" -> "Chuva"
            "thunderstorm" -> "Tempestade"
            "snow" -> "Neve"
            "mist" -> "Névoa"
            "ice" -> "Gelo"
            "fog" -> "Nevoeiro"
            "light rain" -> "Chuva Leve"
            "moderate rain" -> "Chuva Moderada"
            "heavy rain" -> "Chuva Forte"
            "freezing rain" -> "Chuva Congelante"
            "drizzle" -> "Garoa"
            "sleet" -> "Aguaceiro de Neve"
            "hail" -> "Granizo"
            "blowing snow" -> "Neve Ventando"
            "smoke" -> "Fumaça"
            "dust" -> "Poeira"
            "sand" -> "Areia"
            "foggy" -> "Nevoeiro"
            "hurricane" -> "Furacão"
            "tornado" -> "Tornado"
            "windy" -> "Ventania"
            else -> description
        }

        binding.txtTemperatura.text = "$tempFormatted °C"
        binding.paisECidade.text = "$paisNomeCompleto - $name"
        binding.txtInformacoes1.text = "Clima \n $descricaoClima \n\n Umidade \n $humidity%"
        binding.txtInformacoes2.text =
            "Temp.Min \n $tempMinFormatted  \n\n Temp.max \n $tempMaxFormatted"

        binding.progressBar.visibility = View.GONE

    }


}