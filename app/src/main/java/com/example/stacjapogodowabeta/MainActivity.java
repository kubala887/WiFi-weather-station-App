package com.example.stacjapogodowabeta;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class MainActivity extends AppCompatActivity {

    static String[] dane; //dane z modulu po www

    //Pola do wyswietlania pogody z modulu wifi
    TextView tempBox;
    TextView humBox;
    static TextView pressBox;

    //Pola do wyswietlania pobranej pogody z API
    static TextView locationBoxDown;
    static TextView tempBoxDown;
    static TextView windBoxDown;
    static TextView pressBoxDown;
    static TextView dataEpochBoxDown;
    static ImageView imageViewTloMain;

    //Klucze API
    static String wysokoscKlucz = "AIzaSyCEJLoV8utTy08H2g-ACvD-4fonn-GTBf4"; //elevation google API
    static String pogodaKlucz = "4435f002fb897a5c43d4470558189f63"; //openWeather API

    //przechowywanie ustawien
    String ip = "192.168.1.100";
    String stacjaAddress = "http://" + ip + "/";
    static Boolean isFahrenheit;
    static String wybraneMiasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //odwolanie do obiektow z xml
        tempBox = (TextView) findViewById(R.id.tempBox);
        humBox = (TextView) findViewById(R.id.humBox);
        pressBox = (TextView) findViewById(R.id.pressBox);
        locationBoxDown = (TextView) findViewById(R.id.textViewLokacja);
        tempBoxDown = (TextView) findViewById(R.id.textViewTemperaturaPobrana);
        windBoxDown = (TextView) findViewById(R.id.textViewWiatrPobrany);
        pressBoxDown = (TextView) findViewById(R.id.textViewCisnieniePobrane);
        dataEpochBoxDown = (TextView) findViewById(R.id.textViewDaneEpoch);
        Button buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
        Button buttonSettings = (Button) findViewById(R.id.buttonSettings);
        imageViewTloMain = (ImageView) findViewById(R.id.imageViewTloMain);

        //zaladowanie ustawien
        loadSettings();

        //odswiezenie pomiarow po kliknieciu buttona przez uzytkownika
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new dataIntake().execute();
            }
        });

        //otworzenie ustawien po kliknieciu buttona przez uzytkownika
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        //pobranie danych z czujnika
        new dataIntake().execute();

        //pobieranie pogody
        new DownloadWeather().execute("http://api.openweathermap.org/data/2.5/weather?q=" + wybraneMiasto + "&appid=" + pogodaKlucz);

    }

    //odswiezenie po powrocie z innej aplikacji lub odblokowania telefonu itd
    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
        new dataIntake().execute();
        new DownloadWeather().execute("http://api.openweathermap.org/data/2.5/weather?q=" + wybraneMiasto + "&appid=" + pogodaKlucz);
    }

    //wczytanie ustawien z shared preferences
    private void loadSettings() {
        isFahrenheit = Settings.getFahrenheitBoolean(this);
        ip = Settings.getStationIp(this);
        stacjaAddress = "http://" + ip + "/";
        wybraneMiasto = Settings.getMiasto(this);
    }

    //funkcja otwierajaca ustawienia start nowego activity przez intent
    public void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    //klasa laczaca sie ze stacja - pobiera z niej odczyty i zapisuje do tablicy "dane"
    public class dataIntake extends AsyncTask<Void, Void, Void> {
        String zawartosc;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(stacjaAddress).get();
                zawartosc = doc.text();
                dane = zawartosc.split(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isFahrenheit) {
                tempBox.setText(dane[0] + "\u00b0C");
            } else {
                double f = Double.parseDouble(dane[0]);
                f = Math.round((f * 1.8 + 32) * 10.0) / 10.0;
                String fTemp = String.valueOf(f);
                tempBox.setText(fTemp + "\u00b0F");
            }
            humBox.setText(dane[1] + "%");

        }
    }
}
