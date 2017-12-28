package de.mensa.max.mensatu_dresden;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by max on 28.12.17.
 */

public class JSONParser {

    public List<Meal> readJson(String rawData) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(rawData));
        System.out.println(rawData);
        try {
            return readMealsArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<Meal> readMealsArray(JsonReader reader) throws IOException {
        List<Meal> meals = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            meals.add(readMeal(reader));
        }
        reader.endArray();
        return meals;
    }

    public Meal readMeal(JsonReader reader) throws IOException {
        String id = "-1";
        String mealName = "unbekannt";
        String category = "unbekannt";
        Map<String, String> prices = new HashMap<>();
        List<String> notes = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    id = reader.nextString();
                    break;
                case "name":
                    mealName = reader.nextString();
                    break;
                case "category":
                    category = reader.nextString();
                    break;
                case "prices":
                    prices = readStringObject(reader);
                    break;
                case "notes":
                    notes = readStringArray(reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return new Meal(id, mealName, notes, prices, category);
    }

    public List<String> readStringArray(JsonReader reader) throws IOException {
        List<String> stringArray = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            stringArray.add(reader.nextString());
        }
        reader.endArray();
        return stringArray;
    }

    private Map<String, String> readStringObject(JsonReader reader) throws IOException {
        Map<String, String> stringObject = new HashMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.peek() != JsonToken.NULL) {
                String name = reader.nextName();
                if (reader.peek() == JsonToken.NULL)
                    reader.skipValue();
                else
                    stringObject.put(name, reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return stringObject;
    }
}
