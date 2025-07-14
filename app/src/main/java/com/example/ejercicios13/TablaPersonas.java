package com.example.ejercicios13;

import android.provider.BaseColumns;

public final class TablaPersonas {
    // Para evitar que alguien instancie accidentalmente la clase de contrato,
    // haz que el constructor sea privado.
    private TablaPersonas() {}

    /* Clase interna que define el contenido de la tabla de personas */
    public static class PersonaEntry implements BaseColumns {
        public static final String TABLE_NAME = "personas";
        public static final String COLUMN_NAME_NOMBRES = "nombres";
        public static final String COLUMN_NAME_APELLIDOS = "apellidos";
        public static final String COLUMN_NAME_EDAD = "edad";
        public static final String COLUMN_NAME_CORREO = "correo";
        public static final String COLUMN_NAME_DIRECCION = "direccion";
    }
}