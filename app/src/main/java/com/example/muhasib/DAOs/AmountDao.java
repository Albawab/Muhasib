package com.example.muhasib.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.muhasib.models.Amount;
import com.example.muhasib.models.UserKind;

import java.util.List;

@Dao
public interface AmountDao {
    @Insert
    void insert(Amount amount);

    @Query("SELECT * FROM amount ORDER BY date DESC")
    LiveData<List<Amount>> getAll();


    @Query("SELECT * FROM amount  WHERE status = :status ORDER BY date DESC")
    LiveData<List<Amount>> getAllWithStatus(boolean status);

    @Query("UPDATE amount SET status = :status WHERE id = :id")
    void updateStatus(int id, boolean status);

    @Delete
    void delete(Amount amount);
}