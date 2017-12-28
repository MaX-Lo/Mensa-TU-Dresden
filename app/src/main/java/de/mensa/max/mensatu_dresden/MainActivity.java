package de.mensa.max.mensatu_dresden;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        endpoint = "http://openmensa.org/api/v2";

        meals = new LinkedList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, meals);
        ListView lvMeals = (ListView) findViewById(R.id.lvMeals);
        lvMeals.setAdapter(adapter);
    }

    private void addMeals(String rawData, String date) {
        List<Meal> mealsToday = parseData(rawData);

        meals.add(String.format("--- %s ---", date));
        for (int i=0; i < mealsToday.size(); i++) {
            meals.add(mealsToday.get(i).name);
        }

        adapter.notifyDataSetChanged();
    }

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

    void handleMensaChange(String newMensaID) {
        updateTitle(getMensaName(newMensaID));
        meals.clear();
        for (String date:  DateHelper.getWeekDatesTillSunday())
            fetchMealsData(newMensaID, date);
    }

    private void updateTitle(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

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
                return "kenn ich nicht";
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
                        addMeals(response, date);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ToDo implement proper error handling like notifying the user
                System.out.println("That didn't work! No connection?");
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_alte_mensa) {
            handleMensaChange("79");
        } else if (id == R.id.nav_zelt_schloss) {
            handleMensaChange("78");
        } else if (id == R.id.nav_siedepunkt) {
            handleMensaChange("82");
        } else if (id == R.id.nav_wu_eins) {
            handleMensaChange("85");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
