package com.example.misLugares.presentacion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.misLugares.Aplicacion;
import com.example.misLugares.R;
import com.example.misLugares.casos_uso.RutasHelper;
import com.example.misLugares.modelo.GeoPunto;
import com.example.misLugares.modelo.Lugar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class RutaActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mapa;
    private GeoPunto origen;
    private GeoPunto destino;
    private Lugar lugarDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);

        // Obtener los datos del intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int pos = extras.getInt("pos", 0);
            lugarDestino = ((Aplicacion) getApplication()).lugares.elementoPos(pos);
            destino = lugarDestino.getPosicion();
            origen = ((Aplicacion) getApplication()).posicionActual;
        }

        // Inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }

        // Validar que tenemos las coordenadas necesarias
        if (origen == null || origen.equals(GeoPunto.SIN_POSICION)) {
            Toast.makeText(this, "No se pudo obtener tu ubicación actual",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (destino == null || destino.equals(GeoPunto.SIN_POSICION)) {
            Toast.makeText(this, "El lugar no tiene coordenadas válidas",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Agregar marcadores para origen y destino
        LatLng origenLatLng = new LatLng(origen.getLatitud(), origen.getLongitud());
        LatLng destinoLatLng = new LatLng(destino.getLatitud(), destino.getLongitud());

        // Marcador de origen (posición actual)
        mapa.addMarker(new MarkerOptions()
                .position(origenLatLng)
                .title("Mi ubicación")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Marcador de destino
        mapa.addMarker(new MarkerOptions()
                .position(destinoLatLng)
                .title(lugarDestino.getNombre())
                .snippet(lugarDestino.getDireccion())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // Ajustar la cámara para mostrar ambos marcadores
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origenLatLng);
        builder.include(destinoLatLng);
        LatLngBounds bounds = builder.build();

        int padding = 150; // Padding en píxeles
        mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        // Trazar la ruta usando RutasHelper
        RutasHelper.trazarRuta(this, origenLatLng, destinoLatLng, mapa);

        Toast.makeText(this, "Trazando ruta...", Toast.LENGTH_SHORT).show();
    }
}
