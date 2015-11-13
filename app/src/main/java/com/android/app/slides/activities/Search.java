package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOSector;
import com.android.app.slides.model.Sector;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.Utilities;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.Slider;

import java.util.ArrayList;

import butterknife.Bind;

public class Search extends BaseActivity {
    @Bind(R.id.searchButton)
    ButtonRectangle searchButton;
    @Bind(R.id.wordKey)
    EditText key;
    @Bind(R.id.sector)
    Spinner sectorList;
    @Bind(R.id.distance)
    Slider distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utilities.isNetworkAvailable(Search.this)){
                    Intent intent = new Intent(Search.this, SearchList.class);
                    intent.putExtra("key", key.getText().toString());
                    if(sectorList.getItemAtPosition(sectorList.getSelectedItemPosition())!=null
                            && !sectorList.getItemAtPosition(sectorList.getSelectedItemPosition()).toString().equalsIgnoreCase(Constants.ALL_SECTORS_NAME) ){
                        intent.putExtra("sector", DAOSector.getSectorIdByName(Search.this, sectorList.getItemAtPosition(sectorList.getSelectedItemPosition()).toString()) + "");
                    }else{
                        intent.putExtra("sector", Constants.ALL_SECTORS_ID + "");
                    }
                    intent.putExtra("distance", distance.getValue() + "");

                    startActivity(intent);
                }
            }
        });

        DAOSector daoSector = new DAOSector(Search.this);
        ArrayList<Sector> sectors = daoSector.loadSectors();
        ArrayAdapter sectorAdapter = new ArrayAdapter(Search.this, android.R.layout.simple_spinner_item);

        for(Sector sector : sectors){
            sectorAdapter.add(sector.getName());
        }

        sectorAdapter.add(Constants.ALL_SECTORS_NAME);

        sectorList.setAdapter(sectorAdapter);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.search;
    }
}
