package de.mensa.max.mensatu_dresden.Helpers;

import android.content.Context;
import android.util.Log;
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

import de.mensa.max.mensatu_dresden.MainActivity;
import de.mensa.max.mensatu_dresden.Meal;

/**
 * This class handles communication and parsing data from OpenMensa API.
 *
 * @author max
 * on 29.07.2018
 */

public class OpenMensaAPI {

    private static final String ENDPOINT = "http://openmensa.org/api/v2";

    // all meals for chosen mensa
    private List<List<Meal>> dailyMealsList;

    private RequestQueue queue;
    private MainActivity mainActivity;
    private int replyCount, requestCount;


    public OpenMensaAPI(MainActivity mainActivity) {
        // Instantiate the RequestQueue.
        this.mainActivity = mainActivity;
        queue = Volley.newRequestQueue(mainActivity);
    }

    public void getMeals(String mensaID, List<String> dates) {
        initMealList(dates.size()); // ToDo remove later
        replyCount = 0;
        requestCount = dates.size();

        for (int i=0; i < dates.size(); i++) {
            fetchMealsData(mensaID, dates.get(i), i);
        }

    }

    private void initMealList(int size) {
        dailyMealsList = new LinkedList<List<Meal>>();
        for (int i=0; i < size; i++) {
            dailyMealsList.add(new ArrayList<Meal>());
        }
    }

    /***
     * fetch meals data from OpenMensa API
     *
     * @param mensaID - mensa you want the meals data from
     * @param date - for which date the meal data gets fetched
     * @param index - request number, needed to put the response into the correct dailyMealsList Entry
     */
    public void fetchMealsData(String mensaID, final String date, final int index) {
        String url = ENDPOINT + String.format("/canteens/%s/days/:%s/meals", mensaID, date);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        addMeals(parseData(response), date, index);
                        replyCount += 1;
                        if (requestsCompleted()) {
                            mainActivity.onReceivedMeals(dailyMealsList);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", error.toString());
                Toast.makeText(mainActivity.getApplicationContext(), "That didn't work! No connection?",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private boolean requestsCompleted() {
        return replyCount == requestCount;
    }

    /**
     * @param mealsToday - list with meals for the given date
     * @param date       - date corresponding to the given date
     */
    private void addMeals(List<Meal> mealsToday, String date, int index) {
        dailyMealsList.set(index, mealsToday);
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
        // Todo consider making jsonParser static
        // Todo what happens/is displayed after error occurred
        JSONParser jsonParser = new JSONParser();
        try {
            meals = jsonParser.readJson(rawData);
        } catch (IOException e) {
            Log.e("MainActivity","Error while parsing Json");
            e.printStackTrace();
        }
        return meals;
    }

}
