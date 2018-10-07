package com.example.stacjapogodowabeta;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


//Klasa pobierajaca dane z API pogody o zadanym adresie i kluczu

public class DownloadWeather extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);     //adres z kluczem API przekazany do funkcji
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int dane = reader.read();       //dane TEMP ktore reader odczyta z url

            String wynik = "";              //pobrane WSZYSTKIE dane pogody z API
            char przyjete;
            //kiedy skonczy czytac to dane == -1 dlatego czytaj dopoki nie ma -1
            while (dane != -1) {
                przyjete = (char) dane;
                wynik += przyjete;
                dane = reader.read();
            }

            return wynik;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Po pobraniu danych powyzej (postExecute) wyciągamy dane do pojedynczych zmiennych
    @Override
    protected void onPostExecute(String wynik) {
        super.onPostExecute(wynik);
        try {
            JSONObject jsonObject = new JSONObject(wynik);  //wszystkie dane
            JSONObject pogodaDane = new JSONObject(jsonObject.getString("main")); //temp,press,hum,min,max
            JSONObject wiatrDane = new JSONObject(jsonObject.getString("wind"));  //speed
            JSONObject lokalizacjaDane = new JSONObject(jsonObject.getString("coord")); //coordinates
            JSONObject opisPogoda = jsonObject.getJSONArray("weather").getJSONObject(0); //description pogody slowny

            //SELEKCJA DANYCH Z BLOKOW JSON z API
            String epochOdebrania = jsonObject.getString("dt");                   //data danych pogody z API
            epochOdebrania = epochOdebrania + "000";                                    //mnozymy przez 1000 bo to epoch unix w sekundach
            Date dataOdebrania = new Date(Long.parseLong(epochOdebrania));

            Double temperatura = Double.parseDouble(pogodaDane.getString("temp")); //temperatura
            temperatura = temperatura - 273.15;                                          //kelviny na stopnie celsjusza
            temperatura = Math.round((temperatura) * 10.0) / 10.0;                      //1 miejsce po przecinku

            String miejscowosc = jsonObject.getString("name");                     //nazwa miejscowosci z ktorej jest pogoda

            String cisnienie = pogodaDane.getString("pressure");                   //cisnienie

            String wiatr = wiatrDane.getString("speed");                           //predkosc wiatru w m/s

            String jakaPogoda = opisPogoda.getString("main");                      //slowny opis pogody do zmiany tla w glownymekranie

            String szerokosc = lokalizacjaDane.getString("lat");                    //szerokosc geograficzna miasta
            String dlugosc = lokalizacjaDane.getString("lon");                      //dlugosc

            //pobieranie wysokosci do obliczenia cisnienia wzgledem poziomu morza czyli takiego jak w normalnej pogodzie
            new DownloadAltitude().execute("https://maps.googleapis.com/maps/api/elevation/json?locations=" + szerokosc + "," + dlugosc + "&key=" + MainActivity.wysokoscKlucz);

            //WPISANIE DANYCH DO ODPOWIEDNICH POL W MainActivity
            MainActivity.locationBoxDown.setText(miejscowosc);                           //miejscowosc

            if (!MainActivity.isFahrenheit) {                                            //temperatura w jednostkach ustwionych przez uzytkownika
                String wyswietlTempC = String.valueOf(temperatura) + "\u00b0C";
                MainActivity.tempBoxDown.setText(wyswietlTempC);
            } else {
                temperatura = Math.round((temperatura * 1.8 + 32) * 10.0) / 10.0;       //przeliczanie na fahrenheity
                String wyswietlTempF = String.valueOf(temperatura) + "\u00b0F";
                MainActivity.tempBoxDown.setText(wyswietlTempF);
            }

            String wyswietlWiatr = wiatr + " m/s";                                      //predkosc wiatru
            MainActivity.windBoxDown.setText(wyswietlWiatr);

            String wyswietlCisnienie = cisnienie + " hPa";                              //cisnienie
            MainActivity.pressBoxDown.setText(wyswietlCisnienie);

            MainActivity.dataEpochBoxDown.setText(String.valueOf(dataOdebrania));       //czas aktualizacji danych


            //ZMIANA TŁA MainActivity W ZALEŻNOŚCI OD POGODY
            switch (jakaPogoda) {
                case "Clear":
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.clear);     //bezchmurnie
                    break;

                case "Clouds":
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.clouds);    //chmury
                    break;

                case "Rain":
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.rain);      //deszcz
                    break;

                case "Drizzle":
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.drizzle);   //mrzawka
                    break;

                case "Thunderstorm":
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.thunderstorm); //burza
                    break;

                case "Atmosphere":
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.atmosphere);   //mgła, smog
                    break;

                case "Snow":
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.snow);        //snieg
                    break;

                default:
                    MainActivity.imageViewTloMain.setImageResource(R.drawable.defa);        //default - tylko biale tlo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
