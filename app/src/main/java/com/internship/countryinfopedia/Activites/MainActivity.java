package com.internship.countryinfopedia.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
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
import com.internship.countryinfopedia.Models.CountryDatabase;
import com.internship.countryinfopedia.R;
import com.internship.countryinfopedia.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<Country> countries;
    private CountryAdapter adapter;
    private static String URL = "https://restcountries.com/v3.1/region/asia";
    private ProgressDialog progressDialog;
    //DataBase
    public CountryDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Progress Bar
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("loading please wait ...");

        countries = new ArrayList<>();


        //country-db obj
        database = Room.databaseBuilder(getApplicationContext(), CountryDatabase.class, "country-db").allowMainThreadQueries().build();
        try {
            ArrayList<Country> roomCountry = (ArrayList<Country>) database.countryDao().getCountries();

            //Checking if the data is present in the room
            if (roomCountry.isEmpty()) {

                if (haveNetwork()) {
                    //Fetching data from internet
                    extractCountries();
                    binding.InternetConnection.setVisibility(View.GONE);
                    Log.d("FromVolley", "Volley");
                } else {
                    binding.InternetConnection.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            } else {
                if (!haveNetwork()) {
                    Toast.makeText(this, "Please turn on your internet for better performance", Toast.LENGTH_SHORT).show();
                }
                //Fetching data from room
                extractCountriesFromRoom(roomCountry);
                Log.d("FromRoom", "Room");
            }
        } catch (Exception e) {
            Log.d("readErrorMy", e.getMessage());
        }


    }

    // Setting data which was extracted from the room.
    private void extractCountriesFromRoom(ArrayList<Country> roomCountry) {
        countries.addAll(roomCountry);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new CountryAdapter(countries, getApplicationContext());
        binding.recyclerView.setAdapter(adapter);
        progressDialog.dismiss();
    }


    //Getting Data
    private void extractCountries() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                //Fetching response from the API
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject countryObj = response.getJSONObject(i);
                        Country country = new Country();

                        country.setName(countryObj.getJSONObject("name").getString("common"));
                        Log.d("NAME", "onResponse: " + countryObj.getJSONObject("name").getString("common"));

                        country.setCapital(countryObj.getJSONArray("capital").getString(0));
                        Log.d("CAPITAL", "onResponse: " + countryObj.getJSONArray("capital").getString(0));

                        country.setRegion(countryObj.getString("region"));
                        Log.d("REGION", "onResponse: " + countryObj.getString("region"));

                        country.setSubRegion(countryObj.getString("subregion"));
                        country.setPopulation(String.valueOf(countryObj.getInt("population")));
                        country.setFlag(countryObj.getJSONObject("flags").getString("png"));

                        country.setBorder(countryObj.getString("borders"));
                        Log.d("BORERS,", "onResponse: " + countryObj.getString("borders"));

                        country.setLanguages(countryObj.getString("languages"));
                        countries.add(country);

                        try {
                            // Adding data to room
                            database.countryDao().addCountry(country);
                        } catch (Exception e) {
                            Log.d("MyDB", e.getMessage());
                        }


                    } catch (JSONException e) {
//                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                //Binding data to the recyclerView
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new CountryAdapter(countries, getApplicationContext());
                binding.recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Error
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to the queue
        queue.add(jsonArrayRequest);
    }

    //Creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
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

    //Menu Options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearCache:
                clearCache();
                break;
            case R.id.refresh:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //Clear Cache Feature
    public void clearCache() {
        try {
            File dir = MainActivity.this.getCacheDir();
            deleteDir(dir);
            database.countryDao().deleteCountries();
            binding.recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    //Search Features
    private void filter(String newText) {
        ArrayList<Country> filteredCountries = new ArrayList<>();
        if (newText.isEmpty()) {
            adapter.filter(countries);
            return;
        } else {
            for (Country country : countries) {
                if (country.getName().toLowerCase().contains(newText.toLowerCase())) {
                    filteredCountries.add(country);
                }
            }
        }
        adapter.filter(filteredCountries);
    }

    //Get languages
    String getLangs(JSONArray array) throws JSONException {
        String lang = "";
        for (int i = 0; i < array.length(); i++) {
            lang += array.getJSONObject(i).getString("name");
            if (i == array.length() - 1) {
                lang += " .";
            } else {
                lang += ", ";
            }
        }
        return lang;
    }


    //Check Internet Connection
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean haveNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}