package de.mensa.max.mensatu_dresden;

import java.util.List;
import java.util.Map;

/**
 * Created by max on 28.12.17.
 */

public class Meal {

    String id;
    String name;
    List<String> notes;
    Map<String, String> prices;
    String category;

    public Meal(
            String id,
            String name,
            List<String> notes,
            Map<String, String> prices,
            String category) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.prices = prices;
        this.category = category;
    }

}
