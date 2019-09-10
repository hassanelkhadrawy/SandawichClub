package com.example.hassan.sandawichclub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id";
    private static final int DEFAULT_POSITION = -1;
    String name, also_known, ingredients, place, description, image;
    int id;
    TextView s_name, s_also_known, s_ingredients, s_place, s_description;
    ImageView ingredientsIv;
    ProgressDialog Dialog;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ingredientsIv = findViewById(R.id.image_iv);
        s_name = findViewById(R.id.s_name);
        s_also_known = findViewById(R.id.also_known_tv);
        s_ingredients = findViewById(R.id.ingredients_tv);
        s_place = findViewById(R.id.place_tv);
        s_description = findViewById(R.id.description_tv);
        refreshLayout=findViewById(R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new JsonUtils().execute();

            }
        });




        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();

        } else {
            Bundle b = getIntent().getExtras();
            id = b.getInt("id");
            new JsonUtils().execute();


        }


    }




    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }



    class JsonUtils extends AsyncTask<Void, Void, String> {
        String urlJson = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            urlJson = "https://hassan-elkhadrawy.000webhostapp.com/sandwich.php?id=" + id;
            Dialog = new ProgressDialog(DetailActivity.this);
            Dialog.setMessage("Loading...");
            Dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(urlJson);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = new BufferedInputStream(con.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {

                    sb.append(line + "\n");

                }
                is.close();
                bufferedReader.close();
                con.disconnect();
                return sb.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s==null){
                    Toast.makeText(DetailActivity.this, "Please, Check Internet Connection", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }else {

                    loadIntoListView(s);
                    refreshLayout.setRefreshing(false);
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }
            Dialog.dismiss();

        }
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        JSONArray jsonArray = object.getJSONArray("customers");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            name = obj.getString("name");
            also_known = obj.getString("also_known");
            ingredients = obj.getString("ingredients");
            place = obj.getString("place");
            description = obj.getString("description");
            image = obj.getString("image");
        }

        s_name.append(name);
        s_ingredients.append(ingredients);
        s_description.append(description);
        s_also_known.append(also_known);
        s_place.append(place);
        Picasso.with(this).load("http://hassan-elkhadrawy.000webhostapp.com/" + image + "").placeholder(R.drawable.no_image).into(ingredientsIv);

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
