package com.internship.countryinfopedia.Models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Country.class}, version = 3)
public abstract class CountryDatabase extends RoomDatabase {
    public abstract CountryDao countryDao();
}
