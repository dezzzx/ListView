package com.example.listviewf1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SQLiteHelper {
    private static final String DATABASE_NAME = "LISTVIEWF1.db";
    private SQLiteDatabase database;
    private final Context context;

    public SQLiteHelper(Context context) {
        this.context = context;
    }

    public SQLiteDatabase open() {
        if (database == null || !database.isOpen()) {
            database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        }
        return database;
    }

    public void createTable() {
        SQLiteDatabase db = open();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Contenido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "imagen TEXT," +
                "titulo TEXT," +
                "contenido TEXT)";
        db.execSQL(CREATE_TABLE);
    }


    public String saveImageToStorage(byte[] imagenData, String imageName) {
        File directory = new File(context.getFilesDir(), "imagenes");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File imageFile = new File(directory, imageName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imagenData);
        } catch (IOException e) {
            Log.e("SQLiteHelper", "Error al guardar la imagen en el almacenamiento", e);
            return null;
        }

        return imageFile.getAbsolutePath();
    }
    public void insertdeserie(String imagen, String titulo, String contenido) {
        SQLiteDatabase db = open();
        ContentValues values = new ContentValues();
        values.put("imagen", imagen);
        values.put("titulo", titulo);
        values.put("contenido", contenido);
        db.insert("Contenido", null, values);
    }


    public void insert(byte[] imagen, String titulo, String contenido) {
        SQLiteDatabase db = open();
        ContentValues values = new ContentValues();

        String imagePath = saveImageToStorage(imagen, titulo + ".png");
        values.put("imagen", imagePath);
        values.put("titulo", titulo);
        values.put("contenido", contenido);
        db.insert("Contenido", null, values);
        Log.d("INSERTAR","Insertado correctamente");
    }

    public void update(int id, byte[] imagen, String titulo, String contenido) {
        SQLiteDatabase db = null;
        try {
            db = open();
            ContentValues values = new ContentValues();
            if (imagen != null) {
                String imagePath = saveImageToStorage(imagen, titulo + ".png");
                values.put("imagen", imagePath);
                Log.d("LISTVIEW", "Imagen actualizada correctamente");
                Log.d("LISTVIEW", "Nuevo path de la imagen: " + imagePath);

            }
            if (titulo != null) {
                values.put("titulo", titulo);
                Log.d("LISTVIEW", "Título actualizado correctamente");
            }
            if (contenido != null) {
                values.put("contenido", contenido);
                Log.d("LISTVIEW", "Contenido actualizado correctamente");
            }
            int rowsUpdated = db.update("Contenido", values, "id = ?", new String[]{String.valueOf(id)});

            if (rowsUpdated == 0) {
                Log.w("LISTVIEW", "No se encontró una fila con el ID proporcionado.");
            }
        } catch (Exception e) {
            Log.e("LISTVIEW", "Error al actualizar el contenido", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public void delete(int id) {
        SQLiteDatabase db = open();
        db.delete("Contenido", "id = ?", new String[]{String.valueOf(id)});
    }
    public void deleteAll() {
        SQLiteDatabase db = open();
        db.delete("Contenido", null, null);
        Log.d("LISTVIEW", "Contenido eliminado correctamente");
    }
}

