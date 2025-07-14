package com.example.ejercicios13;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PersonaDb extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PersonaReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TablaPersonas.PersonaEntry.TABLE_NAME + " (" +
                    TablaPersonas.PersonaEntry._ID + " INTEGER PRIMARY KEY," +
                    TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES + " TEXT," +
                    TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS + " TEXT," +
                    TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD + " INTEGER," +
                    TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO + " TEXT," +
                    TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TablaPersonas.PersonaEntry.TABLE_NAME;

    public PersonaDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public PersonaDb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Esta política de actualización simplemente descarta los datos y vuelve a crear la tabla.
        // En una aplicación real, deberías migrar los datos con cuidado para no perderlos.
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
