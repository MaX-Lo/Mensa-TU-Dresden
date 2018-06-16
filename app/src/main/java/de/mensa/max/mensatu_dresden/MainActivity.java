package de.mensa.max.mensatu_dresden;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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

    private static final String ENDPOINT = "http://openmensa.org/api/v2";
    // determine how many days can get looked ahead
    private static final int FORECAST = 7;
    // Mensa ID to start with
    private static final String INITIAL_MENSA_ID = "79";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private RequestQueue queue;
    // List containing of meals containing all informations e.g. description, price, ...
    private List<Meal> displayedMeals;
    private List<List<Meal>> dailyMeals;
    private Spinner dateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getMensaName(INITIAL_MENSA_ID));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // linear layout manager for arranging meals under each other
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initMealList();
        displayedMeals = new LinkedList<Meal>();
        // adapter for putting data into view
        mAdapter = new MyAdapter(displayedMeals);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        dateSpinner = (Spinner) findViewById(R.id.spinner_nav);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateShownDay(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        handleMensaChange(INITIAL_MENSA_ID);
    }

    /**
     *
     * @param index days between today and day that should be displayed
     */
    private void updateShownDay(int index) {
        Log.e("index", "index" + index );
        displayedMeals.clear();
        displayedMeals.addAll(dailyMeals.get(index));
        mAdapter.notifyDataSetChanged();
    }

    private void initMealList() {
        dailyMeals = new LinkedList<List<Meal>>();
        for (int i=0; i < FORECAST; i++) {
            dailyMeals.add(new ArrayList<Meal>());
        }
    }

    /**
     * @param mealsToday - list with meals for the given date
     * @param date       - date corresponding to the given date
     */
    private void addMeals(List<Meal> mealsToday, String date, int index) {
        dailyMeals.set(index, mealsToday);
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
            Log.e("MainActivity","Error while parsing Json");
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
        initMealList(); // ToDo remove later

        List<String> dates = DateHelper.getNextNDays(FORECAST);
        List<String> weekdays = DateHelper.getNextNWeekdays(FORECAST);

        for (int i=0; i < dates.size(); i++) {
            fetchMealsData(newMensaID, dates.get(i), i);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weekdays);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dateSpinner.setAdapter(adapter);
    }

    /**
     * Set the given string as new title
     * @param title - mew toolbar title
     */
    private void updateTitle(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    /**
     * get to id corresponding mensa name
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
    public void fetchMealsData(String mensaID, final String date, final int index) {
        String url = ENDPOINT + String.format("/canteens/%s/days/:%s/meals", mensaID, date);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        addMeals(parseData(response), date, index);
                        if (index==0) {
                            updateShownDay(index);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", error.toString());
                Toast.makeText(getApplicationContext(), "That didn't work! No connection?",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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
