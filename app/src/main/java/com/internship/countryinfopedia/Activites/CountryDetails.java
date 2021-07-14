package com.internship.countryinfopedia.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.internship.countryinfopedia.Models.Country;
import com.internship.countryinfopedia.Adapter.Utils;
import com.internship.countryinfopedia.databinding.ActivityCountryDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CountryDetails extends AppCompatActivity {
    ActivityCountryDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCountryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Getting the Country obj from the countryAdapter
        Country country = (Country) getIntent().getSerializableExtra("Details");


        //Setting the country details
        binding.countryName.setText(country.getName());
        binding.capital.setText(country.getCapital());
        binding.region.setText(country.getRegion());
        binding.subRegion.setText(country.getSubRegion());
        binding.population.setText(country.getPopulation());
        binding.languages.setText(country.getLanguages());

        //removing unwanted characters from the string
        String borders = country.getBorder().replaceAll("\"", "");
        borders = borders.replace("[","");
        borders = borders.replace("]","");
        if (borders.isEmpty()){
            binding.borders.setText("None");
        }
        else{
            binding.borders.setText(borders);
        }

        //Loading the svg
        Utils.fetchSvg(this,country.getFlag(),binding.flag);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}