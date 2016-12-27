package be.thomasmore.swrott.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koenv on 11-12-2016.
 */
public class Species implements Serializable {

    private long id;

    private String name;
    private String classification;
    private String designation;
    private String averageHeight;
    private String averageLifespan;
    private String eyeColors;
    private String hairColors;
    private String skinColors;
    private String language;
    private String url;
    private String edited;
    private String people;

    private long homeworldId;

    private Planet homeworld;

    private List<Long> peopleIds;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getAverageHeight() {
        return averageHeight;
    }

    public void setAverageHeight(String averageHeight) {
        this.averageHeight = averageHeight;
    }

    public String getAverageLifespan() {
        return averageLifespan;
    }

    public void setAverageLifespan(String averageLifespan) {
        this.averageLifespan = averageLifespan;
    }

    public String getEyeColors() {
        return eyeColors;
    }

    public void setEyeColors(String eyeColors) {
        this.eyeColors = eyeColors;
    }

    public String getHairColors() {
        return hairColors;
    }

    public void setHairColors(String hairColors) {
        this.hairColors = hairColors;
    }

    public String getSkinColors() {
        return skinColors;
    }

    public void setSkinColors(String skinColors) {
        this.skinColors = skinColors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }

    public long getHomeworldId() {
        return homeworldId;
    }

    public void setHomeworldId(long homeworldId) {
        this.homeworldId = homeworldId;
    }

    public Planet getHomeworld() {
        return homeworld;
    }

    public void setHomeworld(Planet homeworld) {
        this.homeworld = homeworld;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public List<Long> getPeopleIds() {
        if (peopleIds == null) {
            peopleIds = new ArrayList<>();

            try {
                JSONArray peopleJson = new JSONArray(people);

                for (int i = 0; i < peopleJson.length(); i++) {
                    String r = peopleJson.getString(i);

                    try {
                        String[] urlparts = r.split("/");
                        peopleIds.add(Long.parseLong(urlparts[urlparts.length - 1]));
                    } catch (Exception e) {}
                }
            } catch (JSONException e) {}
        }

        return peopleIds;
    }

    @Override
    public String toString() {
        return name;
    }
}
