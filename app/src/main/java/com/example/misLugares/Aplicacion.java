package com.example.misLugares;

import android.app.Application;

import com.example.misLugares.datos.LugaresBD;
import com.example.misLugares.modelo.GeoPunto;
import com.example.misLugares.presentacion.AdaptadorLugaresBD;

public class Aplicacion extends Application {

    //public RepositorioLugares lugares = new LugaresLista(); //Comentado para el ejercicio 16 cap 9
    public LugaresBDAdapter lugares;
    //public AdaptadorLugares adaptador = new AdaptadorLugares(lugares);//Comentado para el ejercicio 16 cap 9
    public AdaptadorLugaresBD adaptador;
    public GeoPunto posicionActual = new GeoPunto(0.0, 0.0);
    @Override public void onCreate() {
        super.onCreate();
        lugares = new LugaresBDAdapter(this);
        adaptador = new AdaptadorLugaresBD(lugares, lugares.extraeCursor());
        lugares.setAdaptador(adaptador);
    }
}
