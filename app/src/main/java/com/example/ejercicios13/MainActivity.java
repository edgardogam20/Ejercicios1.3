package com.example.ejercicios13;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etNombres, etApellidos, etEdad, etCorreo, etDireccion;
    private Button btnSalvar, btnBuscar, btnEliminar, btnVerTodos;
    private TextView tvResultados;

    private PersonaDb dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etEdad = findViewById(R.id.etEdad);
        etCorreo = findViewById(R.id.etCorreo);
        etDireccion = findViewById(R.id.etDireccion);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnVerTodos = findViewById(R.id.btnVerTodos);
        tvResultados = findViewById(R.id.tvResultados);

        dbHelper = new PersonaDb(this);


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombres = etNombres.getText().toString().trim();
                String apellidos = etApellidos.getText().toString().trim();
                String edadStr = etEdad.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String direccion = etDireccion.getText().toString().trim();

                if (nombres.isEmpty() || apellidos.isEmpty() || edadStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Nombres, Apellidos y Edad son campos obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                int edad = Integer.parseInt(edadStr);


                Persona personaExistente = buscarPersonaPorNombreYApellido(nombres, apellidos);

                if (personaExistente != null) {
                    // Si la persona existe, la actualizamos
                    actualizarPersona(nombres, apellidos, edad, correo, direccion);
                } else {
                    // Si no existe, insertamos una nueva
                    insertarPersona(nombres, apellidos, edad, correo, direccion);
                }
                limpiarCampos();
            }
        });

        // BUSCAR
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombres = etNombres.getText().toString().trim();
                String apellidos = etApellidos.getText().toString().trim();

                if (nombres.isEmpty() && apellidos.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Introduce nombres o apellidos para buscar", Toast.LENGTH_SHORT).show();
                    return;
                }

                Persona personaEncontrada = null;
                if (!nombres.isEmpty() && !apellidos.isEmpty()) {
                    personaEncontrada = buscarPersonaPorNombreYApellido(nombres, apellidos);
                } else if (!nombres.isEmpty()) {
                    personaEncontrada = buscarPersonaPorNombre(nombres);
                } else { // Solo apellidos, aunque es menos común buscar solo por apellidos
                    personaEncontrada = buscarPersonaPorApellido(apellidos);
                }


                if (personaEncontrada != null) {
                    tvResultados.setText("Persona encontrada:\n" +
                            "ID: " + personaEncontrada.getId() + "\n" +
                            "Nombres: " + personaEncontrada.getNombres() + "\n" +
                            "Apellidos: " + personaEncontrada.getApellidos() + "\n" +
                            "Edad: " + personaEncontrada.getEdad() + "\n" +
                            "Correo: " + personaEncontrada.getCorreo() + "\n" +
                            "Dirección: " + personaEncontrada.getDireccion());
                    // Puedes cargar los datos encontrados en los EditText para editarlos
                    etNombres.setText(personaEncontrada.getNombres());
                    etApellidos.setText(personaEncontrada.getApellidos());
                    etEdad.setText(String.valueOf(personaEncontrada.getEdad()));
                    etCorreo.setText(personaEncontrada.getCorreo());
                    etDireccion.setText(personaEncontrada.getDireccion());
                } else {
                    tvResultados.setText("Persona no encontrada.");
                    Toast.makeText(MainActivity.this, "Persona no encontrada", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombres = etNombres.getText().toString().trim();
                String apellidos = etApellidos.getText().toString().trim();

                if (nombres.isEmpty() || apellidos.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Introduce nombres y apellidos para eliminar", Toast.LENGTH_SHORT).show();
                    return;
                }

                eliminarPersona(nombres, apellidos);
                limpiarCampos();
                tvResultados.setText("Resultados:"); // Limpiar resultados
            }
        });

        // VER TODOS
        btnVerTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verTodasLasPersonas();
            }
        });
    }




    private void insertarPersona(String nombres, String apellidos, int edad, String correo, String direccion) {
        // Obtiene la base de datos en modo escritura
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Crea un nuevo mapa de valores, donde los nombres de las columnas son las claves
        ContentValues values = new ContentValues();
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES, nombres);
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS, apellidos);
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD, edad);
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO, correo);
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION, direccion);

        // Inserta la nueva fila, devolviendo el valor de la clave primaria de la nueva fila.
        long newRowId = db.insert(TablaPersonas.PersonaEntry.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Persona guardada con ID: " + newRowId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar persona", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }


    private void actualizarPersona(String nombresAntiguos, String apellidosAntiguos, int nuevaEdad, String nuevoCorreo, String nuevaDireccion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD, nuevaEdad);
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO, nuevoCorreo);
        values.put(TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION, nuevaDireccion);


        String selection = TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES + " = ? AND " +
                TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS + " = ?";
        String[] selectionArgs = { nombresAntiguos, apellidosAntiguos };

        int count = db.update(
                TablaPersonas.PersonaEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Toast.makeText(this, "Persona actualizada: " + nombresAntiguos + " " + apellidosAntiguos, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se encontró la persona para actualizar o no hubo cambios", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }



    private void eliminarPersona(String nombres, String apellidos) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        String selection = TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES + " = ? AND " +
                TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS + " = ?";

        String[] selectionArgs = { nombres, apellidos };

        int deletedRows = db.delete(TablaPersonas.PersonaEntry.TABLE_NAME, selection, selectionArgs);

        if (deletedRows > 0) {
            Toast.makeText(this, "Persona eliminada: " + nombres + " " + apellidos, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se encontró la persona para eliminar", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private Persona buscarPersonaPorNombre(String nombres) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TablaPersonas.PersonaEntry._ID,
                TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES,
                TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS,
                TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD,
                TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO,
                TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION
        };

        String selection = TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES + " = ?";
        String[] selectionArgs = { nombres };

        Cursor cursor = db.query(
                TablaPersonas.PersonaEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Persona persona = null;
        if (cursor.moveToFirst()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry._ID));
            String itemNombres = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES));
            String itemApellidos = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS));
            int itemEdad = cursor.getInt(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD));
            String itemCorreo = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO));
            String itemDireccion = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION));

            persona = new Persona(itemId, itemNombres, itemApellidos, itemEdad, itemCorreo, itemDireccion);
        }
        cursor.close();
        db.close();
        return persona;
    }


    private Persona buscarPersonaPorNombreYApellido(String nombres, String apellidos) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TablaPersonas.PersonaEntry._ID,
                TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES,
                TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS,
                TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD,
                TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO,
                TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION
        };

        String selection = TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES + " = ? AND " +
                TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS + " = ?";
        String[] selectionArgs = { nombres, apellidos };

        Cursor cursor = db.query(
                TablaPersonas.PersonaEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null, null
        );

        Persona persona = null;
        if (cursor.moveToFirst()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry._ID));
            String itemNombres = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES));
            String itemApellidos = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS));
            int itemEdad = cursor.getInt(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD));
            String itemCorreo = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO));
            String itemDireccion = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION));

            persona = new Persona(itemId, itemNombres, itemApellidos, itemEdad, itemCorreo, itemDireccion);
        }
        cursor.close();
        db.close();
        return persona;
    }


    private Persona buscarPersonaPorApellido(String apellidos) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TablaPersonas.PersonaEntry._ID,
                TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES,
                TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS,
                TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD,
                TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO,
                TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION
        };

        String selection = TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS + " = ?";
        String[] selectionArgs = { apellidos };

        Cursor cursor = db.query(
                TablaPersonas.PersonaEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null, null
        );

        Persona persona = null;
        if (cursor.moveToFirst()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry._ID));
            String itemNombres = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES));
            String itemApellidos = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS));
            int itemEdad = cursor.getInt(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD));
            String itemCorreo = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO));
            String itemDireccion = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION));

            persona = new Persona(itemId, itemNombres, itemApellidos, itemEdad, itemCorreo, itemDireccion);
        }
        cursor.close();
        db.close();
        return persona;
    }


    // SELECCIONAR (Ver todas las personas)
    private void verTodasLasPersonas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TablaPersonas.PersonaEntry._ID,
                TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES,
                TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS,
                TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD,
                TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO,
                TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION
        };


        String sortOrder = TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS + " ASC";

        Cursor cursor = db.query(
                TablaPersonas.PersonaEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        StringBuilder results = new StringBuilder("Personas en la BD:\n");
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry._ID));
            String nombres = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_NOMBRES));
            String apellidos = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_APELLIDOS));
            int edad = cursor.getInt(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_EDAD));
            String correo = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_CORREO));
            String direccion = cursor.getString(cursor.getColumnIndexOrThrow(TablaPersonas.PersonaEntry.COLUMN_NAME_DIRECCION));

            results.append("ID: ").append(itemId)
                    .append(", Nombres: ").append(nombres)
                    .append(", Apellidos: ").append(apellidos)
                    .append(", Edad: ").append(edad)
                    .append(", Correo: ").append(correo)
                    .append(", Dirección: ").append(direccion)
                    .append("\n");
        }
        cursor.close();
        db.close();
        tvResultados.setText(results.toString());
        if (results.toString().equals("Personas en la BD:\n")) {
            Toast.makeText(this, "No hay personas en la base de datos.", Toast.LENGTH_SHORT).show();
        }
    }


    private void limpiarCampos() {
        etNombres.setText("");
        etApellidos.setText("");
        etEdad.setText("");
        etCorreo.setText("");
        etDireccion.setText("");
        etNombres.requestFocus(); //Nos lleva a la primer casilla
    }
}