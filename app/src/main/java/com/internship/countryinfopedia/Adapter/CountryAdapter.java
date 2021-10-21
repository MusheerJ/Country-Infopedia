package com.internship.countryinfopedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.internship.countryinfopedia.Activites.CountryDetails;
import com.internship.countryinfopedia.Models.Country;
import com.internship.countryinfopedia.R;
import com.internship.countryinfopedia.databinding.SampleListBinding;

import java.util.ArrayList;


public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    ArrayList<Country> countries;
    Context context;

    public CountryAdapter(ArrayList<Country> countries, Context context) {
        this.countries = countries;
        this.context = context;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_list, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country country = countries.get(position);
        holder.binding.CountryName.setText(country.getName());
        holder.binding.SubRegion.setText(country.getSubRegion());

        // Placing the svg
        Utils.fetchSvg(context, country.getFlag(), holder.binding.imageView);

        holder.binding.ItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CountryDetails.class);
                intent.putExtra("Details", country);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {

                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.d("MyError", e.toString());
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    //Search Feature
    public void filter(ArrayList<Country> filteredCountries) {
        this.countries = filteredCountries;
        notifyDataSetChanged();
    }


    public class CountryViewHolder extends RecyclerView.ViewHolder {
        SampleListBinding binding;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleListBinding.bind(itemView);
        }
    }
}
