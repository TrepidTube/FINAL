package com.example.misLugares.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.misLugares.presentacion.AcercaDeActivity;

public class CasosUsoActividades {
    private Activity actividad;
    public CasosUsoActividades(Activity actividad) {
        this.actividad = actividad;
    }
    public void lanzarAcercaDe() {
        Intent i = new Intent(actividad, AcercaDeActivity.class);
        actividad.startActivity(i);
    }
}
