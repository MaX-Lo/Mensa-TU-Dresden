package de.mensa.max.mensatu_dresden;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RequestQueue queue;
    private String endpoint;
    private List<String> meals;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        endpoint = "http://openmensa.org/api/v2";

        meals = new LinkedList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, meals);
        ListView lvMeals = findViewById(R.id.lvMeals);
        lvMeals.setAdapter(adapter);
    }

    /**
     * @param mealsToday - list with meals for the given date
     * @param date       - date corresponding to the given date
     */
    private void addMeals(List<Meal> mealsToday, String date) {
        meals.add(String.format("--- %s ---", date));
        for (Meal meal : mealsToday) {
            String mealString = meal.getName() + " " + meal.getStudentPrice();
            meals.add(mealString);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * parse the received json data for one days meals
     * to a list with meals
     *
     * @param rawData - json data returned by the request
     * @return list with meals
     */
    private List<Meal> parseData(String rawData) {
        List<Meal> meals = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try {
            meals = jsonParser.readJson(rawData);
        } catch (IOException e) {
            System.out.println("Error while parsing Json");
            e.printStackTrace();
        }
        return meals;
    }

    /**
     * update mensa title and dispatch requests for fetching new meals
     * till next Sunday
     *
     * @param newMensaID - id of the new selected mensa
     */
    void handleMensaChange(String newMensaID) {
        updateTitle(getMensaName(newMensaID));
        meals.clear();
        for (String date : DateHelper.getNextSevenDays())
            fetchMealsData(newMensaID, date);
    }

    /**
     * Set the given string as new title
     *
     * @param title - mew toolbar title
     */
    private void updateTitle(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    /**
     * get to id corresponding mensa name
     *
     * @param mensaID
     * @return mensa name corresponding to mensaID
     */
    private String getMensaName(String mensaID) {
        switch (mensaID) {
            case "78":
                return "Zeltschloessschen";
            case "79":
                return "Alte Mensa";
            case "82":
                return "Siedepunkt";
            case "85":
                return "WUeins";
            default:
                throw new IllegalArgumentException("unknown id");
        }
    }

    /**
     * Fetch meals for one week. Starting with current date and ending with Sunday.
     */
    public void fetchMealsData(String mensaID, final String date) {
        String url = endpoint + String.format("/canteens/%s/days/:%s/meals", mensaID, date);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        addMeals(parseData(response), date);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ToDo implement proper error handling like notifying the user
                Log.e("MainActivity", "That didn't work! No connection?");
                Log.e("MainActivity", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /***
     * handle selection of another mensa
     * @param item selected menu item
     * @return returns that selection got handled
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_alte_mensa:
                handleMensaChange("79");
                break;
            case R.id.nav_zelt_schloss:
                handleMensaChange("78");
                break;
            case R.id.nav_siedepunkt:
                handleMensaChange("82");
                break;
            case R.id.nav_wu_eins:
                handleMensaChange("85");
                break;
            default:
                Log.i("MainActivity", "mapping to mensa item not found...");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
