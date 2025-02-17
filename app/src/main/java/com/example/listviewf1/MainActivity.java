package com.example.listviewf1;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView lista;
    private SQLiteHelper dbHelper;
    private ContenidoAdapter contenidoAdapter;
    private ArrayList<Contenido> contenidos = new ArrayList<>();
    private Button boton, botonBorrar;
    private ImageView imageView;
    private Spinner Ordenspinner;
    private String orden = "ASC";
    private byte[] imagenBytes = null;
    private Context context;
    private EditText buscador;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageView.setImageURI(selectedImageUri);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                imagenBytes = byteArrayOutputStream.toByteArray();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        lista = findViewById(R.id.listView);
        boton = findViewById(R.id.buttonaaadir);
        botonBorrar = findViewById(R.id.buttonborrartodos);
        Ordenspinner = findViewById(R.id.spinnerOptions);
        buscador = findViewById(R.id.Buscador);

        dbHelper = new SQLiteHelper(this);
        dbHelper.open();
        dbHelper.createTable();
        ejecutarUnaVez(this);

        contenidoAdapter = new ContenidoAdapter(this, contenidos, imagePickerLauncher);
        lista.setAdapter(contenidoAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"ASC", "DESC"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Ordenspinner.setAdapter(adapter);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarRegistros(s.toString());  // Llamar a cargarRegistros con el filtro
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        Ordenspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orden = parent.getItemAtPosition(position).toString();
                cargarRegistros("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        cargarRegistros("");
        boton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Ingresar Contenido");
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
            builder.setView(dialogView);
            EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
            EditText editTextContenido = dialogView.findViewById(R.id.editTextContenido);
            imageView = dialogView.findViewById(R.id.imageView);
            Button botonSeleccionarImagen = dialogView.findViewById(R.id.botonSeleccionarImagen);
            botonSeleccionarImagen.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            });
            builder.setPositiveButton("Guardar", (dialog, which) -> {
                String titulo = editTextTitulo.getText().toString();
                String contenido = editTextContenido.getText().toString();

                if (imagenBytes != null && !titulo.isEmpty() && !contenido.isEmpty()) {
                    dbHelper.insert(imagenBytes, titulo, contenido);
                    cargarRegistros("");
                } else {
                    Toast.makeText(MainActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        botonBorrar.setOnClickListener(v -> showDeleteConfirmationDialog());
    }
    public void showEditDialog(Contenido contenido) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        EditText editTextContenido = dialogView.findViewById(R.id.editTextContenido);
        imageView = dialogView.findViewById(R.id.imageedit);

        editTextTitulo.setText(contenido.getTitulo());
        editTextContenido.setText(contenido.getContenido());
        if (contenido.getImagen() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(contenido.getImagen());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Editar Contenido")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoTitulo = editTextTitulo.getText().toString();
                    String nuevoContenido = editTextContenido.getText().toString();
                    byte[] nuevaImagenBytes = imagenBytes;
                    if (nuevaImagenBytes == null) {
                        nuevaImagenBytes = contenido.getImagen() != null ? contenido.getImagen().getBytes() : null;
                    }

                    dbHelper.update(contenido.getId(), nuevaImagenBytes, nuevoTitulo, nuevoContenido);
                    cargarRegistros("");
                })
                .setNegativeButton("Cancelar", null)
                .show();

        Button botonSeleccionarImagen = dialogView.findViewById(R.id.botonSeleccionarImagen);
        botonSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
    }
    private void showDeleteConfirmationDialog() {
        new android.app.AlertDialog.Builder(context)
                .setMessage("¿Estás seguro de que quieres borrar todos los registros?")
                .setCancelable(false)
                .setPositiveButton("Sí", (dialog, id) -> {
                    SQLiteHelper dbHelper = new SQLiteHelper(context);
                    dbHelper.deleteAll();
                    cargarRegistros("");
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cargarRegistros(String filtro) {
        contenidos.clear();
        SQLiteDatabase db = dbHelper.open();

        // Si el filtro está vacío, cargar todo
        String query;
        String[] args = null;
        if (filtro.isEmpty()) {
            query = "SELECT * FROM Contenido ORDER BY id " + orden;
        } else {
            query = "SELECT * FROM Contenido WHERE titulo LIKE ? ORDER BY id " + orden;
            args = new String[]{"%" + filtro + "%"};  // Filtra aunque sea una parte del título
        }

        Cursor cursor = db.rawQuery(query, args);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String imagenPath = cursor.getString(1);
                Contenido c = new Contenido(
                        cursor.getInt(0),
                        imagenPath,
                        cursor.getString(2),
                        cursor.getString(3)
                );
                contenidos.add(c);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        contenidoAdapter.notifyDataSetChanged();  // Refrescar la lista
    }
    private String guardarImagenEnAlmacenamientoInterno(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        String fileName = "image_" + resourceId + ".png";
        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return getFilesDir() + "/" + fileName;
        } catch (IOException e) {
            Log.e("MainActivity", "Error guardando la imagen", e);
            return null;
        }
    }

    public void ejecutarUnaVez(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        boolean yaEjecutado = prefs.getBoolean("metodoEjecutado", false);

        if (!yaEjecutado) {
            Log.d("DATOS DE SERIE", "Ejecutando el método por primera y única vez.");
            dbHelper.open();


            String pathAlonso = guardarImagenEnAlmacenamientoInterno(R.drawable.alonso);
            String pathSainz = guardarImagenEnAlmacenamientoInterno(R.drawable.carlossainz);
            String pathLando = guardarImagenEnAlmacenamientoInterno(R.drawable.lando);
            String pathVerstappen = guardarImagenEnAlmacenamientoInterno(R.drawable.maxverstappen);
            String pathHamilton = guardarImagenEnAlmacenamientoInterno(R.drawable.lewis);
            String pathSchumacher = guardarImagenEnAlmacenamientoInterno(R.drawable.michael);
            String pathPerez = guardarImagenEnAlmacenamientoInterno(R.drawable.sergio);
            String pathBottas = guardarImagenEnAlmacenamientoInterno(R.drawable.valtteri);

            dbHelper.insertdeserie(pathAlonso, "Fernando Alonso", "Escuderia Actual: Aston Martin");
            dbHelper.insertdeserie(pathSainz, "Carlos Sainz", "Escuderia Actual: Williams racing");
            dbHelper.insertdeserie(pathLando, "Lando Norris", "Escuderia Actual: Mclaren F1 Team");
            dbHelper.insertdeserie(pathVerstappen, "Max Verstappen", "Escudería Actual Red Bull Racing");
            dbHelper.insertdeserie(pathHamilton, "Lewis Hamilton", "Escuderia Actual: Scuderia Ferrari");
            dbHelper.insertdeserie(pathSchumacher, "Michael Schumacher", "Retiro : 2012");
            dbHelper.insertdeserie(pathPerez, "Sergio Pérez", "Retiro : 2024");
            dbHelper.insertdeserie(pathBottas, "Valtteri Bottas", "Escuderia Actual: Mercedes");

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("metodoEjecutado", true);
            editor.apply();
        } else {
            Log.d("DATOS DE SERIE", "El método ya fue ejecutado antes. No se ejecuta de nuevo.");
        }
    }
}
