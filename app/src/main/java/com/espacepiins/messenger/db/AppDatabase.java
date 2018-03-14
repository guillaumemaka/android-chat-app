package com.espacepiins.messenger.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.espacepiins.messenger.application.Migrations;
import com.espacepiins.messenger.db.entity.ContactEntity;
import com.espacepiins.messenger.db.dao.ContactDao;

/**
 * Created by guillaume on 18-03-06.
 */
@Database(entities = {ContactEntity.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "piins-messenger";

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(final Context appContext) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                            .addMigrations(Migrations.MIGRATION_1_2, Migrations.MIGRATION_2_3)
                            .build();
                }
            }
        }
        return sInstance;
    }


    public abstract ContactDao contactDao();
}
