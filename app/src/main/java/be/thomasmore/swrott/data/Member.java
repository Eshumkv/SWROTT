package be.thomasmore.swrott.data;

/**
 * Created by koenv on 11-12-2016.
 */
public class Member {

    private long id;

    private int speed;
    private int attack;
    private int defense;
    private int experience;
    private int expToLevel;
    private int level;
    private int healthPoints;

    private Stats stats;

    private long teamId;
    private long peopleId;
    private long pictureId;

    private boolean isSystemEntity;

    private Team team;
    private People person;
    private Picture picture;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getExpToLevel() {
        return expToLevel;
    }

    public void setExpToLevel(int expToLevel) {
        this.expToLevel = expToLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(long peopleId) {
        this.peopleId = peopleId;
    }

    public long getPictureId() {
        return pictureId;
    }

    public void setPictureId(long pictureId) {
        this.pictureId = pictureId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        this.teamId = team.getId();
    }

    public People getPerson() {
        return person;
    }

    public void setPerson(People person) {
        this.person = person;
        this.peopleId = person.getId();
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
        this.pictureId = picture.getId();
    }

    public void setStats(Stats stats) {
        speed = stats.getSpeed();
        attack = stats.getAttack();
        defense = stats.getDefense();
        experience = stats.getExperience();
        expToLevel = stats.getExpToLevel();
        healthPoints = stats.getHealthPoints();
        level = stats.getLevel();

        this.stats = stats;
    }

    public Stats getStats() {
        if (stats == null) {
            stats = new Stats();

            stats.setSpeed(speed);
            stats.setAttack(attack);
            stats.setDefense(defense);
            stats.setExperience(experience);
            stats.setExpToLevel(expToLevel);
            stats.setHealthPoints(healthPoints);
            stats.setLevel(level);
        }

        return stats;
    }

    public boolean isSystemEntity() {
        return isSystemEntity;
    }

    public void setSystemEntity(boolean systemEntity) {
        isSystemEntity = systemEntity;
    }

    @Override
    public String toString() {
        return this.getPerson().getName() + " - Lvl " + this.level;
    }
}
