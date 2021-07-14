package com.internship.countryinfopedia.Models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CountryDao {

    @Insert
    public void addCountry(Country country);

    @Query("select * from Countries")
    public List<Country> getCountries();

    @Query("delete from Countries")
    public void deleteCountries();


}
