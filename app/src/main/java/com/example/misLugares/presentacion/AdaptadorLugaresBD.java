package com.example.misLugares.presentacion;

import android.database.Cursor;
import android.view.View;

import com.example.misLugares.AdaptadorLugares;
import com.example.misLugares.datos.LugaresBD;
import com.example.misLugares.datos.RepositorioLugares;
import com.example.misLugares.modelo.Lugar;

public class AdaptadorLugaresBD extends AdaptadorLugares {
    protected Cursor cursor;
    public AdaptadorLugaresBD(RepositorioLugares lugares, Cursor cursor) {
        super(lugares);
        this.cursor = cursor;
    }
    public Cursor getCursor() {
        return cursor;
    }
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
    public Lugar lugarPosition(int posicion) {
        cursor.moveToPosition(posicion);
        return LugaresBD.extraeLugar(cursor);
    }
    public int idPosition(int posicion) {
        cursor.moveToPosition(posicion);
        if (cursor.getCount() > 0)
            return cursor.getInt(0);
        else
            return -1;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Lugar lugar = lugarPosition(posicion);
        holder.personaliza(lugar);
        holder.itemView.setTag(new Integer(posicion));
    }
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
    }
    public int posicionId(int id) {
        int pos = 0;
        while (pos<getItemCount() && idPosition(pos)!=id) pos++;
        if (pos >= getItemCount()) return -1;
        else return pos;
    }
}

