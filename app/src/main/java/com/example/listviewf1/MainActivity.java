package com.example.listviewf1;

import static android.icu.lang.UCharacter.toUpperCase;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ListView lista;
    private TextView texto;
    private RadioButton radioButtonPulsado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lista = findViewById(R.id.listView);
        texto = findViewById(R.id.textView);

        // Crear datos
        ArrayList<Encapsulador> datos = new ArrayList<>();
        datos.add(new Encapsulador(R.drawable.alonso, "Fernando Alonso", "Escuderia Actual: Aston Martin"));
        datos.add(new Encapsulador(R.drawable.carlossainz, "Carlos Sainz", "Escuderia Actual: Williams racing"));
        datos.add(new Encapsulador(R.drawable.lando, "Lando Norris", "Escuderia Actual: Mclaren F1 Team"));
        datos.add(new Encapsulador(R.drawable.maxverstappen, "Max Verstappen", "Red Bull Racing"));
        datos.add(new Encapsulador(R.drawable.lewis, "Lewis Hamilton", "Escuderia Actual: Scuderia Ferrari"));
        datos.add(new Encapsulador(R.drawable.michael, "Michael Schumacher", "Retiro : 2012"));
        datos.add(new Encapsulador(R.drawable.sergio, "Sergio Pérez", "Retiro : 2024"));

        lista.setAdapter(new Adaptador(this, R.layout.entrada, datos) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView textoTitulo = view.findViewById(R.id.texto_titulo);
                    TextView textoDatos = view.findViewById(R.id.texto_datos);
                    ImageView imagenEntrada = view.findViewById(R.id.imagen);
                    RadioButton miRadio = view.findViewById(R.id.boton);

                    Encapsulador item = (Encapsulador) entrada;

                    textoTitulo.setText(item.getTextoTitulo());
                    textoDatos.setText(item.getTextoContenido());
                    imagenEntrada.setImageResource(item.getIdImagen());

                    miRadio.setOnClickListener(v -> {
                        if (radioButtonPulsado != null) radioButtonPulsado.setChecked(false);
                        radioButtonPulsado = (RadioButton) v;
                        String nombre = toUpperCase(item.getTextoTitulo());
                        texto.setText("MARCADA LA OPCION "+nombre);
                    });
                }
            }
        });
    }
    public static class Encapsulador {
        private int idImagen;
        private String textoTitulo;
        private String textoContenido;

        public Encapsulador(int idImagen, String textoTitulo, String textoContenido) {
            this.idImagen = idImagen;
            this.textoTitulo = textoTitulo;
            this.textoContenido = textoContenido;
        }

        public int getIdImagen() {
            return idImagen;
        }

        public String getTextoTitulo() {
            return textoTitulo;
        }

        public String getTextoContenido() {
            return textoContenido;
        }

    }
}
