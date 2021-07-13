package com.internship.countryinfopedia.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.internship.countryinfopedia.Models.Country;
import com.internship.countryinfopedia.Adapter.Utils;
import com.internship.countryinfopedia.databinding.ActivityCountryDetailsBinding;

public class CountryDetails extends AppCompatActivity {
    ActivityCountryDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCountryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Country country = (Country) getIntent().getSerializableExtra("Details");

        binding.countryName.setText(country.getName());
        binding.capital.setText(country.getCapital());
        binding.region.setText(country.getRegion());
        binding.subRegion.setText(country.getSubRegion());
        binding.population.setText(country.getPopulation());
        binding.borders.setText(country.getBorder());
        binding.languages.setText(null);

        Utils.fetchSvg(this,country.getFlag(),binding.flag);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}