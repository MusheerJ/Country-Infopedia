package com.internship.countryinfopedia.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.internship.countryinfopedia.Models.Country;
import com.internship.countryinfopedia.Adapter.CountryAdapter;
import com.internship.countryinfopedia.R;
import com.internship.countryinfopedia.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<Country> countries;
    CountryAdapter adapter;
    private static final  String URL = "https://restcountries.eu/rest/v2/region/asia";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        countries = new ArrayList<>();
        extractCountries();


    }

    private void extractCountries() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject countryObj = response.getJSONObject(i);
                        Country country = new Country();
                        country.setName(countryObj.getString("name"));
                        country.setCapital(countryObj.getString("capital"));
                        country.setRegion(countryObj.getString("region"));
                        country.setSubRegion(countryObj.getString("subregion"));
                        country.setPopulation(String.valueOf(countryObj.getInt("population")));
                        country.setFlag(countryObj.getString("flag"));
                        country.setBorder(countryObj.getString("borders"));
                        country.setLanguages(countryObj.getString("languages"));
                        countries.add(country);

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new CountryAdapter(countries,getApplicationContext());
                binding.recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        queue.add(jsonArrayRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Search country");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String newText){
        ArrayList<Country> filteredCountries = new ArrayList<>();
        if (newText.isEmpty()){
            adapter.filter(countries);
            return;
        }
        else{
            for (Country country : countries){
                if (country.getName().toLowerCase().contains(newText.toLowerCase())){
                    filteredCountries.add(country);
                }
            }
        }
        adapter.filter(filteredCountries);
    }
}