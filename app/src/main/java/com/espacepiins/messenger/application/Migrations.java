package com.espacepiins.messenger.application;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

/**
 * Created by guillaume on 18-03-07.
 */

public final class Migrations {
    public static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE contacts " +
                    "ADD COLUMN id TEXT");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE contacts " +
                    "ADD COLUMN photo_thumbnail_uri TEXT");
        }
    };
}
