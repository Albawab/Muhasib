package com.example.muhasib;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;

import com.example.muhasib.DAOs.AmountDao;
import com.example.muhasib.DAOs.UserKindDao;
import com.example.muhasib.models.Amount;
import com.example.muhasib.models.UserKind;


@Database(entities = {UserKind.class, Amount.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserKindDao userKindDao();
    public abstract AmountDao amountDao();

    private static volatile AppDatabase database;

    public static AppDatabase getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, AppDatabase.class, "muhasib_database")
                    .allowMainThreadQueries().build();
        }
        return database;
    }
}
