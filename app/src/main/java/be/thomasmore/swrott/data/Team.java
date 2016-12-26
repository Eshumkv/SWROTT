package be.thomasmore.swrott.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koenv on 8-12-2016.
 */
public class Team {

    public final static int MAX_LEVEL = 100;

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

    public int getAverageBaseStats() {
        if (members == null || members.size() == 0) {
            return 0;
        }

        int sum = 0;

        for (Member m : members) {
            sum += m.getBase_attack();
            sum += m.getBase_defense();
            sum += m.getBase_hp();
            sum += m.getBase_speed();
        }

        return sum / (members.size() * 4);
    }

    public int getTeamExperience() {
        if (members == null || members.size() == 0) {
            return 0;
        }

        int sum = 0;

        for (Member m : members) {
            sum += m.getEv_attack();
            sum += m.getEv_defense();
            sum += m.getEv_hp();
            sum += m.getEv_speed();
        }

        return sum / (members.size() * 4);
    }

    @Override
    public String toString() {
        return name + " (" + planet.getName() + ")";
    }
}
