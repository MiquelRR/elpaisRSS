package com.miquel.elpais


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.miquel.elpais.databinding.ActivityMainBinding
import com.bumptech.glide.Glide

var categories = mapOf(
    "ciencia" to        "https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/section/ciencia/portada",
    "tecnología" to     "https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/section/tecnologia/portada",
    "internacional" to  "https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/section/internacional/portada",
    "cultura" to        "https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/section/cultura/portada",
    "gastronomía" to    "https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/section/gastronomia/portada",
    "economía" to       "https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/section/economia/portada"
)

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isFirstRun = preferences.getBoolean(R.string.is_first_run.toString(), true)
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val radioButtons = listOf(
            binding.radioOptionA,
            binding.radioOptionB,
            binding.radioOptionC,
            binding.radioOptionD,
            binding.radioOptionE,
            binding.radioOptionF
        )
        categories.keys.zip(radioButtons).forEach { (category, radioButton) ->
            radioButton.text = category
            radioButton.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                radioButton.isChecked = true

            }
        }
        if (!isFirstRun) {
            binding.name.setText(preferences.getString(R.string.stored_user_name.toString(), "Jacinto"))
            binding.pass.setText(preferences.getString(R.string.stored_user_pass.toString(), "1234"))
            val radioButton = radioButtons.find { it.text == preferences.getString(R.string.chosen_category.toString(), "ciencia") }
            radioButton?.isChecked = true
        }
        val intent = Intent(this, NewsListActivity::class.java)
        binding.button.setOnClickListener {
            with(preferences.edit()){
                putBoolean(R.string.is_first_run.toString(), false)
                putString(R.string.stored_user_name.toString(), binding.name.text.toString())
                putString(R.string.stored_user_pass.toString(), binding.pass.text.toString())
                putString(R.string.chosen_category.toString(), radioButtons.find { it.isChecked }?.text.toString())
                apply()
            }
            startActivity(intent)
        }

        Glide.with(this).load(getString(R.string.first_image_url)).into(binding.background)
        binding.background.alpha = 0.3f
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}