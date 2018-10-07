package com.example.stacjapogodowabeta;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class DownloadAltitude extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);     //adres z kluczem API przekazany do funkcji
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int dane = reader.read();       //dane TEMP ktore reader odczyta z url

            String wynik_wys = "";              //pobrane WSZYSTKIE dane pogody z API
            char przyjete;
            //kiedy skonczy czytac to dane == -1 dlatego czytaj dopoki nie ma -1
            while (dane != -1) {
                przyjete = (char) dane;
                wynik_wys += przyjete;
                dane = reader.read();
            }

            return wynik_wys;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Po pobraniu danych powyzej (postExecute) wyciągamy dane do pojedynczych zmiennych


    @Override
    protected void onPostExecute(String wynik_wys) {
        super.onPostExecute(wynik_wys);
        try {
            JSONObject jsonObjectwys = new JSONObject(wynik_wys); //wszystkie dane JSON
            JSONObject wysokoscDane = jsonObjectwys.getJSONArray("results").getJSONObject(0); //tabela JSON z informacja o wysokosci bezwzgl.

            //SELEKCJA DANYCH Z BLOKOW JSON z API
            String jakaWysokosc = wysokoscDane.getString("elevation");
            double wb = Double.parseDouble(jakaWysokosc);

            //ustawienie cisnienia w main activity
            double c = Double.parseDouble(MainActivity.dane[3]); //pobrane cisnienie wzgledne ze stacji
            double cis_bezwzgledne = (c)/(Math.pow(1-(wb/44330),5.255));    //przeliczanie na cisnienie bezwzgledne ze wzoru producenta i wysokosci npm pobranej z API
            cis_bezwzgledne = Math.round((cis_bezwzgledne) * 10.0) / 10.0; //obcięcie do 1 miejsca po przecinku
            String cis_s = String.valueOf(cis_bezwzgledne);
            MainActivity.pressBox.setText(cis_s + "hPa"); //wyswietlenie w main

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
