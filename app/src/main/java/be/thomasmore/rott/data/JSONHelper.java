package be.thomasmore.rott.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.thomasmore.rott.Helper;

/**
 * Created by koenv on 13-12-2016.
 */
public class JSONHelper {
    public static List<Planet> getPlanets(String json) {
        List<Planet> result = new ArrayList<>();

        try {
            JSONArray results = new JSONObject(json).getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                result.add(getJSONPlanet(results.getJSONObject(i)));
            }

        } catch (JSONException e) {
            Log.e("JSON PARSER", "Error parsing data: " + e.toString());
        }

        return result;
    }

    public static String getNextPage(String json) {
        String next = null;

        try {
            JSONObject obj = new JSONObject(json);
            next = obj.getString("next");
        } catch (JSONException e) {
            Log.e("JSON PARSER", "Error parsing data: " + e.toString());
        }

        return next;
    }

    public static String getPreviousPage(String json) {
        String previous = null;

        try {
            JSONObject obj = new JSONObject(json);
            previous = obj.getString("next");
        } catch (JSONException e) {
            Log.e("JSON PARSER", "Error parsing data: " + e.toString());
        }

        return previous;
    }

    public static int getCount(String json) {
        int count = 0;

        try {
            JSONObject obj = new JSONObject(json);
            count = obj.getInt("count");
        } catch (JSONException e) {
            Log.e("JSON PARSER", "Error parsing data: " + e.toString());
        }

        return count;
    }

    public static Species getJSONSpecies(JSONObject speciesObj) {
        Species s = null;

        try {
            s = new Species();

            s.setName(speciesObj.getString("name"));
            s.setClassification(speciesObj.getString("classification"));
            s.setDesignation(speciesObj.getString("designation"));
            s.setAverageHeight(speciesObj.getString("average_height"));
            s.setAverageLifespan(speciesObj.getString("average_lifespan"));
            s.setEyeColors(speciesObj.getString("eye_colors"));
            s.setHairColors(speciesObj.getString("hair_colors"));
            s.setSkinColors(speciesObj.getString("skin_colors"));
            s.setLanguage(speciesObj.getString("language"));
            s.setUrl(speciesObj.getString("url"));
            s.setEdited(speciesObj.getString("edited"));
            s.setPeople(speciesObj.getString("people"));

            String[] urlparts = new String[] {};

            try {
                urlparts = s.getUrl().split("/");
                s.setId(Long.parseLong(urlparts[urlparts.length - 1]));
            } catch (NumberFormatException e) {
                Log.e("JSON PARSER", "Error Species ID Parsing (" +
                        Helper.atos(urlparts, ", ") + ")(" +
                        (urlparts.length - 1) + "): " + e.toString());
            }

            try {
                String homeworld = speciesObj.getString("homeworld");
                if (!homeworld.equals("null")) {
                    urlparts = homeworld.split("/");
                    s.setHomeworldId(Long.parseLong(urlparts[urlparts.length - 1]));
                }
            } catch (NumberFormatException e) {
                Log.e("JSON PARSER", "Error Species homeworld Parsing (" +
                        Helper.atos(urlparts, ", ") + ")(" +
                        (urlparts.length - 1) + "): " + e.toString());
            }
        } catch (JSONException e) {
            Log.e("JSON PARSER", "Error parsing species data: " + e.toString());
        }

        return s;
    }

    public static Planet getJSONPlanet(JSONObject planetObj) {
        Planet p = null;

        try {
            p = new Planet();

            p.setName(planetObj.getString("name"));
            p.setDiameter(planetObj.getString("diameter"));
            p.setRotationPeriod(planetObj.getString("rotation_period"));
            p.setOrbitalPeriod(planetObj.getString("orbital_period"));
            p.setGravity(planetObj.getString("gravity"));
            p.setPopulation(planetObj.getString("population"));
            p.setClimate(planetObj.getString("climate"));
            p.setTerrain(planetObj.getString("terrain"));
            p.setSurfaceWater(planetObj.getString("surface_water"));
            p.setResidents(planetObj.getString("residents"));
            p.setEdited(planetObj.getString("edited"));
            p.setUrl(planetObj.getString("url"));

            String[] urlparts = new String[] {};

            try {
                urlparts = p.getUrl().split("/");
                p.setId(Long.parseLong(urlparts[urlparts.length - 1]));
            } catch (NumberFormatException e) {
                Log.e("JSON PARSER", "Error Planet ID Parsing (" +
                        Helper.atos(urlparts, ", ") + ")(" +
                        (urlparts.length - 1) + "): " + e.toString());
            }
        } catch (JSONException e) {
            Log.e("JSON PARSER", "Error parsing planet data: " + e.toString());
        }

        return p;
    }

    public static People getJSONPeople(JSONObject peopleObj) {
        People p = null;

        try {
            p = new People();

            p.setName(peopleObj.getString("name"));
            p.setBirthYear(peopleObj.getString("birth_year"));
            p.setEyeColor(peopleObj.getString("eye_color"));
            p.setGender(peopleObj.getString("gender"));
            p.setHairColor(peopleObj.getString("hair_color"));
            p.setHeight(peopleObj.getString("height"));
            p.setMass(peopleObj.getString("mass"));
            p.setSkinColor(peopleObj.getString("skin_color"));
            p.setUrl(peopleObj.getString("url"));
            p.setEdited(peopleObj.getString("edited"));
            p.setSpecies(peopleObj.getString("species"));

            String[] urlparts = new String[] {};

            try {
                urlparts = p.getUrl().split("/");
                p.setId(Long.parseLong(urlparts[urlparts.length - 1]));
            } catch (NumberFormatException e) {
                Log.e("JSON PARSER", "Error ID Parsing (" +
                        Helper.atos(urlparts, ", ") + ")(" +
                        (urlparts.length - 1) + "): " + e.toString());
            }

            try {
                String homeworld = peopleObj.getString("homeworld");
                if (!homeworld.equals("null")) {
                    urlparts = homeworld.split("/");
                    p.setHomeworldId(Long.parseLong(urlparts[urlparts.length - 1]));
                }
            } catch (NumberFormatException e) {
                Log.e("JSON PARSER", "Error People homeworld Parsing (" +
                        Helper.atos(urlparts, ", ") + ")(" +
                        (urlparts.length - 1) + "): " + e.toString());
            }
        } catch (JSONException e) {
            Log.e("JSON PARSER", "Error parsing people data: " + e.toString());
        }

        return p;
    }

    public enum JSONTypes {
        People,
        Planet,
        Species
    }

    public static <T> T getObject(JSONTypes type, JSONObject obj) {
        T result = null;

        switch (type) {
            case Species:
                result = (T)getJSONSpecies(obj);
                break;
            case People:
                result = (T)getJSONPeople(obj);
                break;
            case Planet:
                result = (T)getJSONPlanet(obj);
                break;
        }

        return result;
    }
}
