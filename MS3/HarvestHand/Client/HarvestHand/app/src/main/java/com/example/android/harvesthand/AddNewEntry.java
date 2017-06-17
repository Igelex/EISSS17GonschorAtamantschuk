package com.example.android.harvesthand;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;

public class AddNewEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_entry);

        Spinner cropSpinner = (Spinner)findViewById(R.id.spinner_crop);
        Spinner soilSpinner = (Spinner)findViewById(R.id.spinner_soil);

        cropSpinner.setAdapter(setupSpinner(R.array.crop_spinner_array));
        soilSpinner.setAdapter(setupSpinner(R.array.soil_spinner_array));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_save:
                Toast.makeText(this, "Entry saved", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayAdapter setupSpinner(int strings){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                strings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }


}
