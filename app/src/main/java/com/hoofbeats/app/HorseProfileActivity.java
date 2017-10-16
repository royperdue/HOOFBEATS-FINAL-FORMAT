package com.hoofbeats.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.hoofbeats.app.help.HelpOption;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.model.Note;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.DialogUtility;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HorseProfileActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private ImageButton horseProfileImage;
    private MaterialSpinner horseBreedSpinner;
    private MaterialSpinner horseSexSpinner;
    private MaterialSpinner horseColorSpinner;
    private MaterialSpinner horseHeightSpinner;
    private MaterialSpinner horseDisciplineSpinner;
    private Button saveProfileButton;
    private EditText horseNameEditText;
    private EditText horseAgeEditText;
    private EditText horseWeightEditText;
    private EditText horseNoteEditText;
    private String horseName;
    private String horseAge;
    private String horseHeight;
    private String horseWeight;
    private String discipline;
    private String horseColor;
    private String horseBreed;
    private String horseSex;
    private String horseNote;
    private static final int REQUEST_READ_PERMISSION = 786;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == REQUEST_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
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

        horseBreedSpinner = (MaterialSpinner) findViewById(R.id.horse_breed_spinner);
        horseBreedSpinner.setItems("Breed...", "Thoroughbred", "Quarter Horse", "Andalusian", "Morgan", "Belgian");
        horseBreedSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                horseBreed = item;
            }
        });

        horseColorSpinner = (MaterialSpinner) findViewById(R.id.horse_color_spinner);
        horseColorSpinner.setItems("Color...", "Bay", "Black", "Chestnut", "Grey", "Palomino");
        horseColorSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                horseColor = item;
            }
        });

        horseHeightSpinner = (MaterialSpinner) findViewById(R.id.horse_height_spinner);
        horseHeightSpinner.setItems("Height...", "14h", "15h", "16h", "17h", "17.1h");
        horseHeightSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                horseHeight = item.replace("h", "");
            }
        });

        horseSexSpinner = (MaterialSpinner) findViewById(R.id.horse_sex_spinner);
        horseSexSpinner.setItems("Sex...", "Mare", "Stallion", "Gelding", "Colt", "Filly");
        horseSexSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                horseSex = item;
            }
        });

        horseDisciplineSpinner = (MaterialSpinner) findViewById(R.id.horse_discipline_spinner);
        horseDisciplineSpinner.setItems("Discipline...", "Dressage", "Hunter", "Jumper", "Eventing", "Reining", "Driving");
        horseDisciplineSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                discipline = item;
            }
        });

        horseNameEditText = (EditText) findViewById(R.id.text_horse_name);
        horseAgeEditText = (EditText) findViewById(R.id.text_horse_age);
        horseWeightEditText = (EditText) findViewById(R.id.text_horse_weight);
        horseNoteEditText = (EditText) findViewById(R.id.text_horse_note);

        saveProfileButton = (Button) findViewById(R.id.save_horse_profile_button);
        saveProfileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (horseNameEditText.getText() != null && horseNameEditText.getText().toString().length() > 0)
                    horseName = horseNameEditText.getText().toString();
                if (horseAgeEditText.getText() != null && horseAgeEditText.getText().toString().length() > 0)
                    horseAge = horseAgeEditText.getText().toString();

                if (horseWeightEditText.getText() != null && horseWeightEditText.getText().toString().length() > 0)
                    horseWeight = horseWeightEditText.getText().toString();

                if (horseNoteEditText.getText() != null && horseNoteEditText.getText().toString().length() > 0)
                    horseNote = horseNoteEditText.getText().toString();

                if (horseName != null)
                {
                    if (horseAge != null)
                    {
                        if (horseHeight != null)
                        {
                            if (horseWeight != null)
                            {
                                if (horseColor != null)
                                {
                                    if (horseBreed != null)
                                    {
                                        if (horseSex != null)
                                        {
                                            if (discipline != null)
                                            {
                                                Horse horse = new Horse();
                                                horse.setHorseName(horseName);
                                                horse.setHorseAge(Double.parseDouble(horseAge));
                                                horse.setHorseBreed(horseBreed);
                                                horse.setDiscipline(discipline);
                                                horse.setHorseColor(horseColor);
                                                horse.setHorseHeight(Double.parseDouble(horseHeight));
                                                horse.setHorseSex(horseSex);

                                                Bitmap bitmap = Bitmap.createBitmap(horseProfileImage.getDrawingCache());

                                                //if (horse.getProfilePictureURI() != null)
                                                //DatabaseUtility.deleteHorseProfileImage(HorseProfileActivity.this, horse.getProfilePictureURI());

                                                horse.setProfilePictureURI(String.valueOf(DatabaseUtility.getImageUri(getApplicationContext(), bitmap, horseName)));
                                                MyApplication.getInstance().getDaoSession().getHorseDao().insert(horse);

                                                List<Note> noteList = horse.getNotes();
                                                Note note = new Note();
                                                note.setDate(new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime()));
                                                note.setNote(horseNote);

                                                MyApplication.getInstance().getDaoSession().getNoteDao().insert(note);
                                                noteList.add(note);

                                                horse.resetNotes();

                                                startActivity(new Intent(HorseProfileActivity.this, NavigationActivity.class));
                                                finish();
                                            } else
                                                DialogUtility.showAlertSnackBarMedium(HorseProfileActivity.this, getString(R.string.message_select_horse_discipline));
                                        } else
                                            DialogUtility.showAlertSnackBarMedium(HorseProfileActivity.this, getString(R.string.message_select_horse_sex));
                                    } else
                                        horseBreed = getString(R.string.message_unknown_breed);
                                } else
                                    DialogUtility.showAlertSnackBarMedium(HorseProfileActivity.this, getString(R.string.message_select_horse_color));
                            } else
                                DialogUtility.showAlertSnackBarMedium(HorseProfileActivity.this, getString(R.string.message_enter_horse_weight));
                        } else
                            DialogUtility.showAlertSnackBarMedium(HorseProfileActivity.this, getString(R.string.message_select_horse_height));
                    } else
                        DialogUtility.showAlertSnackBarMedium(HorseProfileActivity.this, getString(R.string.message_enter_horse_age));
                } else
                    DialogUtility.showAlertSnackBarMedium(HorseProfileActivity.this, getString(R.string.message_enter_horse_name));
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

    private void requestPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        } else
        {
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

            horseProfileImage.setDrawingCacheEnabled(true);
        }
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(HorseProfileActivity.this, NavigationActivity.class));
        finish();
        super.onBackPressed();
    }

    private void fillHelpOptionAdapter(HelpOptionAdapter adapter)
    {
        adapter.add(new HelpOption(R.string.config_settings_page_header_one, com.hoofbeats.app.R.string.config_settings_page_paragraph_one));
        adapter.add(new HelpOption(R.string.config_settings_page_header_two, com.hoofbeats.app.R.string.config_settings_page_paragraph_two));
        adapter.add(new HelpOption(com.hoofbeats.app.R.string.config_settings_page_header_three, com.hoofbeats.app.R.string.config_settings_page_paragraph_three));
        adapter.add(new HelpOption(com.hoofbeats.app.R.string.config_settings_page_header_four, com.hoofbeats.app.R.string.config_settings_page_paragraph_four));
    }

    @Override
    protected BaseAdapter getAdapter()
    {
        return null;
    }
}
