package com.example.listviewf1;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.IOException;
import java.util.ArrayList;

public class ContenidoAdapter extends ArrayAdapter<Contenido> {
    private final Context context;
    private final ArrayList<Contenido> contenidos;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public ContenidoAdapter(Context context, ArrayList<Contenido> contenidos, ActivityResultLauncher<Intent> launcher) {
        super(context, R.layout.entrada, contenidos);
        this.context = context;
        this.contenidos = contenidos;
        this.imagePickerLauncher = launcher;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.entrada, parent, false);
        }
        Contenido contenido = contenidos.get(position);
        ImageView imagen = convertView.findViewById(R.id.imagen);
        TextView titulo = convertView.findViewById(R.id.texto_titulo);
        TextView contenidoTextView = convertView.findViewById(R.id.texto_datos);


        if (contenido.getImagen() != null) {

            Bitmap bitmap = BitmapFactory.decodeFile(contenido.getImagen());
            if (bitmap != null) {
                imagen.setImageBitmap(bitmap);
            }
        }

        titulo.setText(contenido.getTitulo());
        contenidoTextView.setText(contenido.getContenido());

        convertView.setOnClickListener(view -> {
            showOptionsDialog(contenido);
        });

        return convertView;
    }

    private void showOptionsDialog(Contenido contenido) {
        new AlertDialog.Builder(context)
                .setTitle("Selecciona una opción")
                .setItems(new String[]{"Borrar", "Editar", "No"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Borrar
                            showDeleteConfirmationDialog(contenido);
                            break;
                        case 1: // Editar
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).showEditDialog(contenido);
                            }
                            break;

                        case 2: // No
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(Contenido contenido) {
        new AlertDialog.Builder(context)
                .setMessage("¿Estás seguro de que quieres borrar este registro de la LISTVIEW: " + contenido.getTitulo() + "?")
                .setCancelable(false)
                .setPositiveButton("Sí", (dialog, id) -> {
                    SQLiteHelper dbHelper = new SQLiteHelper(context);
                    dbHelper.delete(contenido.getId());
                    contenidos.remove(contenido);
                    notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

