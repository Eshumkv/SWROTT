package be.thomasmore.swrott.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koenv on 8-12-2016.
 */
public class Team {

    private long id;

    private String name;
    private long planetId;

    private boolean isSystemEntity;

    private Planet planet;
    private List<Member> members;

    public Team() {
        members = new ArrayList<>();
    }

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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public boolean isSystemEntity() {
        return isSystemEntity;
    }

    public void setSystemEntity(boolean systemEntity) {
        isSystemEntity = systemEntity;
    }

    public int getAverageLevel() {
        if (members == null || members.size() == 0) {
            return 0;
        }

        int sum = 0;

        for (Member m : members) {
            sum += m.getLevel();
        }

        return sum / members.size();
    }

    @Override
    public String toString() {
        return name + " (" + planet.getName() + ")";
    }
}
