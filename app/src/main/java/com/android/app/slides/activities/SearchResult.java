package com.android.app.slides.activities;

import android.os.Bundle;
import android.widget.ListView;

import com.android.app.slides.R;
import com.android.app.slides.adapters.SearchAdapter;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;

import java.util.ArrayList;

/**
 * Created by uni on 2/11/15.
 */
public class SearchResult extends BaseActivity {
    private ListView searchList;
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_lista_busqueda;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_busqueda);


        searchList=(ListView) findViewById(R.id.search_list);

        User x =new User();
        User y=new User();
        x.setName("Empresa 1");
        y.setName("Empresa 2");
        x.setSector(new Sector(2, "Servicios"));
        y.setSector(new Sector(1, "hosteleria"));

        ArrayList<User> list= new ArrayList<User>();

        list.add(x);
        list.add(y);

        SearchAdapter adapter=new SearchAdapter(this,list);


        searchList.setAdapter(adapter);

        //5 Button search= (Button) findViewById(R.id.searchButton);

    }
}
