package de.mensa.max.mensatu_dresden;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mensa.max.mensatu_dresden.Helpers.FormatHelper;

/**
 * this class contains the meal blueprint used for every meal in the application
 *
 * @author max
 * on 28.12.17.
 */

public class Meal {

    private String id;
    private String description;
    private List<String> notes;
    private Map<String, String> prices;
    private String category;

    /**
     * @param id - meal identifier could be a number or string, may be NULL
     * @param description - meal name with short garnish description
     * @param notes - extra information, special contents (e.g. lactose, vegetarian...)
     * @param prices - list with prices for different customers (students, employee, others...)
     * @param category - type of meal (e.g. Soup, Pizza, Pasta...)
     */
    public Meal(
            String id,
            String description,
            List<String> notes,
            Map<String, String> prices,
            String category) {
        this.id = id;
        this.description = description;
        this.notes = notes;

        this.prices = new HashMap<String, String>();
        for (String customer_type : prices.keySet()) {
            this.prices.put(customer_type, FormatHelper.formatPricing(prices.get(customer_type)));
        }
        Log.d("meal", this.description);
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String name) {
        this.description = name;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public Map<String, String> getPrices() {
        return prices;
    }

    public String getStudentPrice() {
        return prices.containsKey("students") ? prices.get("students") + "€" : "";
    }

    public void setStudentPrice(String price) {
        prices.put("students", price);
    }

    public void setPrices(Map<String, String> prices) {
        this.prices = prices;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
