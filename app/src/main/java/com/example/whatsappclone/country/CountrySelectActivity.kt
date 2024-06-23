package com.example.whatsappclone.country

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R


class CountrySelectActivity : AppCompatActivity(), CountryAdapter.ClickListener {

    private val countries = listOf(
        CountryData(name = "India", code = "+91", flag = "🇮🇳"),
        CountryData(name = "Pakistan", code = "+92", flag = "🇵🇰"),
        CountryData(name = "United States", code = "+1", flag = "🇺🇸"),
        CountryData(name = "South Africa", code = "+27", flag = "🇿🇦"),
        CountryData(name = "Afghanistan", code = "+93", flag = "🇦🇫"),
        CountryData(name = "United Kingdom", code = "+44", flag = "🇬🇧"),
        CountryData(name = "Italy", code = "+39", flag = "🇮🇹")
    )

    private lateinit var rcv : RecyclerView
    var name = ""
    var code = ""
    var flag = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_select)

        rcv = findViewById(R.id.RCV)
        rcv.layoutManager = LinearLayoutManager(this)
        val adapter = CountryAdapter(countries, this@CountrySelectActivity)
        rcv.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("countryName", name) // Put your data here
        resultIntent.putExtra("countryCode", code) // Put your data here
        resultIntent.putExtra("countryFlag",flag)
        // Set the result and finish the current activity
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }


    override fun onFollowClicked(country: CountryData) {
        name = country.name
        code = country.code
        flag = country.flag
        onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the leading back arrow click event
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
