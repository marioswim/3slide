package com.android.app.slides.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.app.slides.R;
import com.android.app.slides.model.User;

import java.util.ArrayList;


/**
 * Created by uni on 2/11/15.
 */
public class UserAdapter extends BaseAdapter {

    private ArrayList<User> entradas;
    private int R_layout_IdView;
    private Context contexto;

    private static LayoutInflater inflater = null;

    public UserAdapter(Context contexto, ArrayList<User> entradas) {

        this.contexto = contexto;
        this.entradas = entradas;
        inflater = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return entradas.size();
    }

    @Override
    public Object getItem(int position) {
        return entradas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TextView nombre;
        TextView sector;
        ImageView image;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_busqueda, parent, false);

            nombre = (TextView) convertView.findViewById(R.id.nombre);
            sector = (TextView) convertView.findViewById(R.id.sector);
            image = (ImageView) convertView.findViewById(R.id.imagen1);


            convertView.setTag(R.id.nombre, nombre);
            convertView.setTag(R.id.sector, sector);
            convertView.setTag(R.id.imagen1, image);

        } else {

            nombre = (TextView) convertView.getTag(R.id.nombre);
            sector = (TextView) convertView.getTag(R.id.sector);
            image = (ImageView) convertView.getTag(R.id.imagen1);

        }

        nombre.setText(entradas.get(position).getName());
        if(entradas.get(position).getSector()!=null){
            sector.setText(entradas.get(position).getSector().getName());
        }
        image.setImageResource(R.drawable.avatar);

        return convertView;
    }

    public void newAdapter(ArrayList<User> entradas) {
        this.entradas = entradas;
        inflater = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
}
