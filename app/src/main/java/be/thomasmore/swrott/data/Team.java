package be.thomasmore.swrott.data;

/**
 * Created by koenv on 8-12-2016.
 */
public class Team {

    private long id;

    private String name;

    private long planetId;

    private Planet planet;

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

    public long getPlanetId() {
        return planetId;
    }

    public void setPlanetId(long planetId) {
        this.planetId = planetId;
    }

    public Planet getPlanet() {
        return planet;
    }

    public void setPlanet(Planet planet) {
        this.planet = planet;
        this.planetId = planet.getId();
    }

    @Override
    public String toString() {
        return name + " (" + planet.getName() + ")";
    }
}
