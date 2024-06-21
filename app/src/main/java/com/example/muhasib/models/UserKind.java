package com.example.muhasib.models;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.muhasib.AppDatabase;

import java.util.List;

@Entity
public class UserKind {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;


    public UserKind(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void delete(Context context, UserKind user) {
        if (user != null) {
            AppDatabase.getDatabase(context).userKindDao().delete(user);
        }
    }


    public static LiveData<List<UserKind>> getAll(Context context) {
        return AppDatabase.getDatabase(context).userKindDao().getAllUsers();
    }

    public static UserKind getById(int userid,Context context) {
        return AppDatabase.getDatabase(context).userKindDao().getUserById(userid);
    }

    @Override
    public String toString() {
        return name;
    }
}