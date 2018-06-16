package de.mensa.max.mensatu_dresden;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mensa.max.mensatu_dresden.Helpers.FormatHelper;

/**
 * Created by max on 28.12.17.
 */

class Meal {

    private String id;
    private String name;
    private List<String> notes;
    private Map<String, String> prices;
    private String category;

    Meal(
            String id,
            String name,
            List<String> notes,
            Map<String, String> prices,
            String category) {
        this.id = id;
        this.name = name;
        this.notes = notes;

        this.prices = new HashMap<String, String>();
        for (String customer_type : prices.keySet()) {
            this.prices.put(customer_type, FormatHelper.formatPricing(prices.get(customer_type)));
        }
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return prices.containsKey("students") ? "(" + prices.get("students") + "€)" : "";
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
