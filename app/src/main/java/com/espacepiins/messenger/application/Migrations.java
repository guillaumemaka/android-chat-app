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

    public static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE contacts " +
                    "ADD COLUMN photo_thumbnail_uri TEXT");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("create temporary table pn_backup " +
//                    "(" +
//                    "id TEXT PRIMARY KEY NOT NULL," +
//                    "phone_number TEXT NOT NULL," +
//                    "phone_type TEXT NOT NULL," +
//                    "user_id TEXT NOT NULL" +
//                    ")");
//
//            database.execSQL("INSERT INTO pn_backup SELECT id, phone_number, phone_type, user_id FROM phone_numbers");
//
//            database.execSQL("DROP TABLE phone_numbers");
//
//            database.execSQL("create table phone_numbers " +
//                    "(" +
//                    "id TEXT PRIMARY KEY NOT NULL," +
//                    "phone_number TEXT NOT NULL," +
//                    "phone_type TEXT NOT NULL," +
//                    "contact_id TEXT NOT NULL" +
//                    ")");
//
//            database.execSQL("INSERT INTO phone_numbers SELECT id, phone_number, phone_type, user_id as contact_id FROM pn_backup");
//
//            database.execSQL("create temporary table ea_backup " +
//                    "(" +
//                    "id TEXT PRIMARY KEY NOT NULL," +
//                    "email_address TEXT NOT NULL," +
//                    "email_type TEXT NOT NULL," +
//                    "user_id TEXT NOT NULL" +
//                    ")");
//
//            database.execSQL("INSERT INTO ea_backup SELECT id, email_address, email_type, user_id FROM email_addresses");
//
//            database.execSQL("DROP TABLE email_addresses");
//
//            database.execSQL("create table email_addresses " +
//                    "(" +
//                    "id TEXT PRIMARY KEY NOT NULL," +
//                    "email_addressTEXT NOT NULL," +
//                    "email_type TEXT NOT NULL," +
//                    "contact_id TEXT NOT NULL" +
//                    ")");
//
//            database.execSQL("INSERT INTO email_addresses SELECT id, email_address, email_type, user_id as contact_id FROM ea_backup");
        }
    };
}
