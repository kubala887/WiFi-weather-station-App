package com.example.stacjapogodowabeta;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

//Klasa obsługujaca panel ustawien

public class Settings extends AppCompatActivity {

    //kontrolki do ustawien
    EditText ip;            //ustawione ip stacji
    Switch fahrenheit;      //czy zmieniamy jednostke z C na F
    Spinner refreshMiasto;    //miasto do pobrania pogody


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        createControls();
    }

    //funkcja obslugujaca przestawianie kontrolek i ich wyswietlanie
    private void createControls(){
        ip = (EditText) findViewById(R.id.editTextIp);
        fahrenheit = (Switch) findViewById(R.id.switchFahrenheit);
        refreshMiasto = (Spinner) findViewById(R.id.spinnerMiasto);

        //listiner zmiany ip
        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                saveStationIp(ip.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveStationIp(ip.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveStationIp(ip.getText().toString());
            }
        });

        //listener zmiany jednostek
        fahrenheit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveFahrenheitBoolean(fahrenheit.isChecked());
            }
        });

        //listener zmiany czasu odswiezania
        refreshMiasto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int itemIndex = refreshMiasto.getSelectedItemPosition();
                saveMiasto(refreshMiasto.getSelectedItem().toString(),itemIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int itemIndex = refreshMiasto.getSelectedItemPosition();
                saveMiasto(refreshMiasto.getSelectedItem().toString(),itemIndex);
            }
        });

        //WCZYTANIE USTAWIEN - aby je wyswietlic z pamieci sharedpreferences gdy wchodzimy do menu ustawien

        //stan ustawienia ip
        ip.setText(getStationIp(this));

        //stan ustawienia jednostki
        if (getFahrenheitBoolean(this) == true){
            fahrenheit.setChecked(true);
        }else{
            fahrenheit.setChecked(false);
        }

        //stan ustawionego miasta

        refreshMiasto.setSelection(getMiastoIndex(this));
}

    //OBSLUGA USTAWIEN - zapis w SharedPreferences

    private static final String PREFS_NAME = "AppPrefs"; //nazwa SharedPreferences gdzie zapisujemy

    //funkcja zapisujaca ip stacji w ustawieniach
    private void saveStationIp(String ip){
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ip",ip);
        editor.apply();
    }
    //funkcja odczytujaca ip stacji
    static public String getStationIp(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        return prefs.getString("ip","192.168.1.100");
    }

    //funkcja zapisujaca stan ustawienia jednostki wyswietlania temperatury
    private void saveFahrenheitBoolean(boolean isF) {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isF",isF);
        editor.apply();
    }
    //funkcja odczytujaca zapisany stan jednostki wyswietlanai temperatury
    static public boolean getFahrenheitBoolean(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        return prefs.getBoolean("isF",false);
    }

    //funkcja zapisujaca wybrane miasto
    private void saveMiasto(String miastoString,int itemIndex){
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("miastoString",miastoString);
        editor.putInt("itemIndex",itemIndex);
        editor.apply();
    }
    //funkcja odczytujaca ustawione miasto
    static public String getMiasto(Context context){
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs",MODE_PRIVATE);
        return prefs.getString("miastoString","Krakow");
    }
    //funkcja odczytujaca pozycje na dropdown liscie (spinner) wybranego miasta
    static public int getMiastoIndex(Context context){
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs",MODE_PRIVATE);
        return prefs.getInt("itemIndex",0);
    }

}
