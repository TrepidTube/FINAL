package com.example.misLugares.presentacion;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.misLugares.Aplicacion;
import com.example.misLugares.LugaresBDAdapter;
import com.example.misLugares.R;
import com.example.misLugares.casos_uso.CasosUsoLugar;
import com.example.misLugares.modelo.Lugar;
import com.example.misLugares.modelo.TipoLugar;

public class EdicionLugarActivity extends AppCompatActivity {

    private LugaresBDAdapter lugares;
    private CasosUsoLugar usoLugar;
    private int pos;
    private Lugar lugar;
    private EditText nombre, direccion, telefono, url, comentario;
    private Spinner tipo;
    private int _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", 0);
        lugares  = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this, lugares);

        _id = extras.getInt("_id", -1);
        if (_id != -1) lugar = lugares.elemento(_id);
        else           lugar = lugares.elementoPos(pos);

        actualizaVistas();

        // ── Botón CANCELAR ───────────────────────────────────────────────────
        LinearLayout btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(v -> {
            if (_id != -1) usoLugar.borrar(_id);
            finish();
        });

        // ── Botón GUARDAR ────────────────────────────────────────────────────
        LinearLayout btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {
            lugar.setNombre(nombre.getText().toString());
            lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
            lugar.setDireccion(direccion.getText().toString());
            String telStr = telefono.getText().toString().trim();
            lugar.setTelefono(telStr.isEmpty() ? 0 : Integer.parseInt(telStr));
            lugar.setUrl(url.getText().toString());
            lugar.setComentario(comentario.getText().toString());

            if (_id == -1) _id = lugares.getAdaptador().idPosition(pos);
            usoLugar.guardar(_id, lugar);
            finish();
        });
    }

    private void actualizaVistas() {
        nombre    = findViewById(R.id.nombre);
        tipo      = findViewById(R.id.tipo);
        direccion = findViewById(R.id.direccion);
        telefono  = findViewById(R.id.telefono);
        url       = findViewById(R.id.url);
        comentario = findViewById(R.id.comentario);

        nombre.setText(lugar.getNombre());

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        tipo.setSelection(lugar.getTipo().ordinal());

        direccion.setText(lugar.getDireccion());
        telefono.setText(lugar.getTelefono() == 0 ? "" : Integer.toString(lugar.getTelefono()));
        url.setText(lugar.getUrl());
        comentario.setText(lugar.getComentario());
    }
}