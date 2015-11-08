package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOSector;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.tools.SlidesApp;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by david on 6/11/15.
 */
public class Presentation extends BaseActivity {
    @Bind(R.id.UploadPptBtn) ButtonRectangle UploadPptBtn;

    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.userSector)
    TextView userSector;

    User user;

    @Override
    protected int getLayoutResource() {
        return R.layout.presentation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UploadPptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("file/*"); // intent type to filter application based on your requirement
                startActivityForResult(fileIntent, RESULT_OK);
            }
        });

        setUserInfo();
    }

    private void setUserInfo() {
        DAOUser daoUser = new DAOUser(this);
        user = daoUser.loadUser();

        if (user != null) {
            if (!user.getName().isEmpty()) {
                userName.setText(user.getName());
            }

            DAOSector daoSector = new DAOSector(this);
            ArrayList<Sector> sectors = daoSector.loadSectors();

            if (!user.getSector().getName().isEmpty()) {
                userSector.setText(user.getSector().getName());
            }


        }
    }
}
