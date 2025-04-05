package com.example.vintagestore.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.vintagestore.model.Item;
import com.example.vintagestore.model.Message;
import com.example.vintagestore.model.User;
import com.example.vintagestore.util.Converters;

@Database(entities = {User.class, Item.class, Message.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "vintage_store_db";
    private static AppDatabase instance;
    
    public abstract UserDao userDao();
    public abstract ItemDao itemDao();
    public abstract MessageDao messageDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}