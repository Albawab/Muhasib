package com.example.muhasib.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.muhasib.models.UserKind;

import java.util.List;

@Dao
public interface UserKindDao {

    @Insert
    void insert(UserKind user);

    @Query("SELECT * FROM UserKind")
    LiveData<List<UserKind>> getAllUsers();

    @Query("SELECT * FROM UserKind WHERE id = :userId")
    UserKind getUserById(int userId);

    @Delete
    void delete(UserKind user);
}
