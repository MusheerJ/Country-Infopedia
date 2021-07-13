package com.internship.countryinfopedia.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
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

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<Country> countries;
    CountryAdapter adapter;
    private static String URL = "https://restcountries.eu/rest/v2/region/asia";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("loading please wait ...");

        countries = new ArrayList<>();
        progressDialog.show();
        extractCountries();


    }

    private void extractCountries() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                //Fetching response from the API
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
                        JSONArray array = countryObj.getJSONArray("languages");
                        country.setLanguages(getLangs(array));
                        countries.add(country);

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new CountryAdapter(countries,getApplicationContext());
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.clearCache:
                clearCache();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearCache() {

        try {
            File dir = MainActivity.this.getCacheDir();
            deleteDir(dir);
            Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    //Search Features
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

    //Get langs
    String getLangs(JSONArray array) throws JSONException {
        String lang = "";
        for (int i = 0;i<array.length();i++){
            lang+=array.getJSONObject(i).getString("name");
            if (i == array.length()-1){
                lang+=" .";
            }
            else
            {
                lang+=", ";
            }
        }
        return lang;
    }
}