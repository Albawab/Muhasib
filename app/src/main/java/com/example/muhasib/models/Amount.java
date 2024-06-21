package com.example.muhasib.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.muhasib.Converters;

import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = UserKind.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE))
public class Amount {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private double amount;

    @ColumnInfo
    private String description;

    @ColumnInfo
    @TypeConverters({Converters.class})
    private Date date;

    @ColumnInfo
    private boolean isPositive;

    @ColumnInfo
    private int userId; // Foreign key to User

    public Amount(double amount, String description, Date date, boolean isPositive, int userId) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.isPositive = isPositive;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
