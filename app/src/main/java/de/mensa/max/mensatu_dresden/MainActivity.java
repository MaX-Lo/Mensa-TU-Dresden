package de.mensa.max.mensatu_dresden;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.LinkedList;
import java.util.List;

import de.mensa.max.mensatu_dresden.Helpers.DateHelper;
import de.mensa.max.mensatu_dresden.Helpers.Mensen;
import de.mensa.max.mensatu_dresden.Helpers.OpenMensaAPI;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // determines how many days can get looked ahead
    private static final int FORECAST = 7;
    // ID from Mensa being displayed on startup
    private static final String INITIAL_MENSA_ID = "79";

    // a flexible view for providing a limited window into a large data set
    private RecyclerView.Adapter mealViewAdapter;

    // meals for chosen mensa and date
    private List<Meal> displayedMeals;
    // all meals for chosen mensa
    private List<List<Meal>> dailyMealsLists;
    private OpenMensaAPI openMensaAPI;

    private Spinner dateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Mensen.idToName(INITIAL_MENSA_ID));
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        // linear layout manager for arranging meals under each other
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        displayedMeals = new LinkedList<Meal>();
        // adapter for putting data into view
        mealViewAdapter = new MealRecyclerViewAdapter(displayedMeals);
        mRecyclerView.setAdapter(mealViewAdapter);

        dateSpinner = findViewById(R.id.spinner_nav);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (dailyMealsLists.size() != 0) {
                    updateShownMeals(dailyMealsLists.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dailyMealsLists = new LinkedList<>();
        openMensaAPI = new OpenMensaAPI(this);

        handleMensaChange(INITIAL_MENSA_ID);
    }

    /**
     * update view with @param shownMeals data or display a 'closed' notification image when no
     * meals are available (List is empty)
     */
    private void updateShownMeals(List<Meal> shownMeals) {
        displayedMeals.clear();
        displayedMeals.addAll(shownMeals);
        mealViewAdapter.notifyDataSetChanged();

        showClosedView(isMensaClosed());
    }

    private boolean isMensaClosed() {
        return mealViewAdapter.getItemCount() == 0;
    }

    /**
     * toggle view with image indicating the mensa is closed
     * @param isClosed - true if mensa is closed
     */
    private void showClosedView(boolean isClosed) {
        View closedLayout = findViewById(R.id.closedLayout);
        if (isClosed) {
            closedLayout.setVisibility(View.VISIBLE);
        } else {
            closedLayout.setVisibility(View.GONE);
        }
    }

    /**
     * update mensa title and dispatch requests for fetching new meals
     * @param newMensaID - id of the new selected mensa
     */
    void handleMensaChange(String newMensaID) {
        updateTitle(Mensen.idToName(newMensaID));

        List<String> dates = DateHelper.getNextNDays(FORECAST);
        List<String> weekdays = DateHelper.getNextNWeekdays(FORECAST, this);

        openMensaAPI.getMeals(newMensaID, dates);
        updateDateSpinner(weekdays);

    }

    /**
     * Set the given string as new action-/toolbar title
     * @param title - mew toolbar title
     */
    private void updateTitle(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    void updateDateSpinner(List<String> items) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dateSpinner.setAdapter(adapter);
    }

    /**
     * callback method, gets called after requests have been completed
     * @param dailyMealsLists - meal data received in replies
     */
    public void onReceivedMeals(List<List<Meal>> dailyMealsLists) {
        this.dailyMealsLists = dailyMealsLists;
        updateShownMeals(dailyMealsLists.get(0));
    }

    /**
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
