package be.thomasmore.swrott.data;

/**
 * Created by koenv on 11-12-2016.
 */
public class Member {

    private static final int MAX_EV = 65535;

    private long id;

    private int base_speed;
    private int base_attack;
    private int base_defense;
    private int base_hp;

    private int iv_speed;
    private int iv_attack;
    private int iv_defense;
    private int iv_hp;

    private int ev_speed;
    private int ev_attack;
    private int ev_defense;
    private int ev_hp;

    private int speed;
    private int attack;
    private int defense;
    private int healthPoints;

    private int experience;
    private int expToLevel;
    private int level;

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

    public int getBase_speed() {
        return base_speed;
    }

    public void setBase_speed(int base_speed) {
        this.base_speed = base_speed;
    }

    public int getBase_attack() {
        return base_attack;
    }

    public void setBase_attack(int base_attack) {
        this.base_attack = base_attack;
    }

    public int getBase_defense() {
        return base_defense;
    }

    public void setBase_defense(int base_defense) {
        this.base_defense = base_defense;
    }

    public int getIv_speed() {
        return iv_speed;
    }

    public void setIv_speed(int iv_speed) {
        this.iv_speed = iv_speed;
    }

    public int getIv_attack() {
        return iv_attack;
    }

    public void setIv_attack(int iv_attack) {
        this.iv_attack = iv_attack;
    }

    public int getIv_defense() {
        return iv_defense;
    }

    public void setIv_defense(int iv_defense) {
        this.iv_defense = iv_defense;
    }

    public int getEv_speed() {
        return ev_speed;
    }

    public void setEv_speed(int ev_speed) {
        this.ev_speed = ev_speed;
    }

    public int getEv_attack() {
        return ev_attack;
    }

    public void setEv_attack(int ev_attack) {
        this.ev_attack = ev_attack;
    }

    public int getEv_defense() {
        return ev_defense;
    }

    public void setEv_defense(int ev_defense) {
        this.ev_defense = ev_defense;
    }

    public int getBase_hp() {
        return base_hp;
    }

    public void setBase_hp(int base_hp) {
        this.base_hp = base_hp;
    }

    public int getIv_hp() {
        return iv_hp;
    }

    public void setIv_hp(int iv_hp) {
        this.iv_hp = iv_hp;
    }

    public int getEv_hp() {
        return ev_hp;
    }

    public void setEv_hp(int ev_hp) {
        this.ev_hp = ev_hp;
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
        healthPoints = stats.getHealthPoints();

        base_attack = stats.getBase_attack();
        base_defense = stats.getBase_defense();
        base_speed = stats.getBase_speed();
        base_hp = stats.getBase_hp();

        iv_attack = stats.getIv_attack();
        iv_defense = stats.getIv_defense();
        iv_speed = stats.getIv_speed();
        iv_hp = stats.getIv_hp();

        ev_attack = stats.getEv_attack();
        ev_defense = stats.getEv_defense();
        ev_speed = stats.getEv_speed();
        ev_hp = stats.getEv_hp();

        experience = stats.getExperience();
        expToLevel = stats.getExpToLevel();
        level = stats.getLevel();

        this.stats = stats;
    }

    public Stats getStats() {
        if (stats == null) {
            stats = new Stats();

            stats.setBase_attack(base_attack);
            stats.setBase_defense(base_defense);
            stats.setBase_speed(base_speed);
            stats.setBase_hp(base_hp);

            stats.setIv_attack(iv_attack);
            stats.setIv_defense(iv_defense);
            stats.setIv_speed(iv_speed);
            stats.setIv_hp(iv_hp);

            stats.setEv_attack(ev_attack);
            stats.setEv_defense(ev_defense);
            stats.setEv_speed(ev_speed);
            stats.setEv_hp(ev_hp);

            stats.setSpeed(speed);
            stats.setAttack(attack);
            stats.setDefense(defense);
            stats.setHealthPoints(healthPoints);

            stats.setExperience(experience);
            stats.setExpToLevel(expToLevel);
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

    public void addExperience(int amount) {
        experience += amount;

        while (experience >= expToLevel) {
            setStats(FightHelper.levelup(getStats()));
        }
    }

    public void addEv(int amount) {
        ev_hp += amount;
        ev_speed += amount;
        ev_defense += amount;
        ev_attack += amount;

        if (ev_speed >= MAX_EV)
            ev_attack = MAX_EV;

        if (ev_defense >= MAX_EV)
            ev_attack = MAX_EV;

        if (ev_hp >= MAX_EV)
            ev_attack = MAX_EV;

        if (ev_attack >= MAX_EV)
            ev_attack = MAX_EV;
    }

    @Override
    public String toString() {
        return this.getPerson().getName() + " - Lvl " + this.level;
    }
}
