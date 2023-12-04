package com.herick.previsodotempo

import android.annotation.SuppressLint
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


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var paises: CountryCodeResolver = CountryCodeResolver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val temp = main?.get("temp")?.toString() ?: "N/A"
        val temMin = main?.get("temp_min")?.toString() ?: "N/A"
        val temMax = main?.get("temp_max")?.toString() ?: "N/A"
        val humidity = main?.get("humidity")?.toString() ?: "N/A"
        var paisNomeCompleto = ""


        val sys = response.body()!!.sys
        val country = sys.get("country").asString

        val weather = response.body()!!.weather
        val main_weather = weather[0].main
        val description = weather[0].description
        val name = response.body()!!.name

        // Convertendo para Celsius
        val temp_c = (temp.toDouble() - 273.15)
        val temp_min_c = (temMin.toDouble() - 273.15)
        val temp_max_c = (temMax.toDouble() - 273.15)

        // Formatando para no máximo dois dígitos após o ponto decimal
        val decimalFormat = DecimalFormat("#")
        val tempFormatted = decimalFormat.format(temp_c)
        val tempMinFormatted = decimalFormat.format(temp_min_c)
        val tempMaxFormatted = decimalFormat.format(temp_max_c)

        paisNomeCompleto = paises.resolveCountryName("$country")


        binding.txtTemperatura.text = "$tempFormatted °C"
        binding.paisECidade.text = "$paisNomeCompleto - $name"
        binding.txtInformacoes1.text = "Clima \n $description \n\n Umidade \n $humidity%"
        binding.txtInformacoes2.text = "Temp.Min \n $tempMinFormatted  \n\n Temp.max \n $tempMaxFormatted"

        binding.progressBar.visibility = View.GONE

    }

}