package com.example.chinyao.mow.mow.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cupboardTest.db";
    // private final static int DATABASE_VERSION = 1607280552;

    public DatabaseHelper(Context context) {
        // nothing can be placed before super
        super(context,
                DATABASE_NAME,
                null,
                //Integer.parseInt(context.getString(R.string.app_version))
                1607280605
        );
        // we need to update this when data model changes
        // update this with
        // app/build.gradle ***
        // strings.xml ***
        // DatabaseHelper.java
        // MowActivity.java
    }

    static {
        // register our models
        cupboard().register(TodoModel.class);
        cupboard().register(SettingModel.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
        // add indexes and other database tweaks in this method if you want

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted
        cupboard().withDatabase(db).upgradeTables();
        // do migration work if you have an alteration to make to your schema here

    }

}

