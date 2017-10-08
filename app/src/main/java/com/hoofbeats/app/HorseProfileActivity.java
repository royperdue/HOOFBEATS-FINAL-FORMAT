package com.hoofbeats.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.hoofbeats.app.help.HelpOption;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.hoofbeats.app.utility.DialogUtility;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;

public class HorseProfileActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private ImageButton horseProfileImage;
    private static final int REQUEST_READ_PERMISSION = 786;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ImageSelectorActivity.start(HorseProfileActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false, false, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horse_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.toolbar_title).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        }

        horseProfileImage = (ImageButton) findViewById(R.id.thumbnail_horse_profile);
        horseProfileImage.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                requestPermission();
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                HelpOptionAdapter adapter = new HelpOptionAdapter(HorseProfileActivity.this, com.hoofbeats.app.R.id.config_help_list);
                fillHelpOptionAdapter(adapter);
                DialogUtility.showHelpDialog(HorseProfileActivity.this, adapter);
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        } else {
            ImageSelectorActivity.start(HorseProfileActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false, false, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE)
        {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            //startActivity(new Intent(this, SelectResultActivity.class).putExtra(SelectResultActivity.EXTRA_IMAGES,images));

            Glide.with(HorseProfileActivity.this)
                    .load(new File(images.get(0)))
                    .centerCrop()
                    .into(horseProfileImage);
        }
    }

    private void fillHelpOptionAdapter(HelpOptionAdapter adapter)
    {
        adapter.add(new HelpOption(R.string.config_settings_page_header_one, com.hoofbeats.app.R.string.config_settings_page_paragraph_one));
        adapter.add(new HelpOption(R.string.config_settings_page_header_two, com.hoofbeats.app.R.string.config_settings_page_paragraph_two));
        adapter.add(new HelpOption(com.hoofbeats.app.R.string.config_settings_page_header_three, com.hoofbeats.app.R.string.config_settings_page_paragraph_three));
        adapter.add(new HelpOption(com.hoofbeats.app.R.string.config_settings_page_header_four, com.hoofbeats.app.R.string.config_settings_page_paragraph_four));
    }
}
