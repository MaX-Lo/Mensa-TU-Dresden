package de.mensa.max.mensatu_dresden.Helpers;

import java.util.HashMap;

public class Mensen {

    private static HashMap<String, String> mensaIdNameMap;
    private static Mensen mensen = new Mensen();

    private Mensen() {
        mensaIdNameMap = new HashMap<>();
        mensaIdNameMap.put("78", "Zeltschlössschen");
        mensaIdNameMap.put("79", "Alte Mensa");
        mensaIdNameMap.put("82", "Siedepunkt");
        mensaIdNameMap.put("85", "WUeins");
    }

    /**
     * get corresponding mensa name to id
     * @param id - mensa ID you want the name from
     * @return mensa name corresponding to mensaID
     */
    public static String idToName(String id) {
        if (mensaIdNameMap.containsKey(id)) {
            return mensaIdNameMap.get(id);
        } else {
            throw new IllegalArgumentException("unknown mensa id");
        }
    }
}
