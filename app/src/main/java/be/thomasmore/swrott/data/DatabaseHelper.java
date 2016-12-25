package be.thomasmore.swrott.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by koenv on 11-12-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 17;
    private static final String DATABASE_NAME = "swrott";

    private static final String PLANET = "Planet";
    private static final String TEAM = "Team";
    private static final String MEMBER = "Member";
    private static final String PEOPLE = "People";
    private static final String PICTURE = "Picture";
    private static final String PERSONSPECIES = "PersonSpecies";
    private static final String SPECIES = "Species";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // methode wordt uitgevoerd als de database gecreëerd wordt
    // hierin de tables creëren en opvullen met gegevens
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Maak een lijst met alle queries die je wil uitvoeren
        List<String> queries = new ArrayList<>();

        queries.add("CREATE TABLE " + PLANET + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "diameter TEXT," +
                "rotationPeriod TEXT," +
                "orbitalPeriod TEXT," +
                "gravity TEXT," +
                "population TEXT," +
                "climate TEXT," +
                "terrain TEXT," +
                "surfaceWater TEXT," +
                "residents TEXT," +
                "url TEXT," +
                "edited TEXT" +
                ")");
        
        queries.add("CREATE TABLE " + SPECIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "classification TEXT," +
                "designation TEXT," +
                "averageHeight TEXT," +
                "averageLifespan TEXT," +
                "eyeColors TEXT," +
                "hairColors TEXT," +
                "skinColors TEXT," +
                "language TEXT," +
                "url TEXT," +
                "edited TEXT," +
                "homeworldId INTEGER," +
                "personSpeciesId INTEGER," +
                "FOREIGN KEY (homeworldId) REFERENCES " + PLANET + "(id)" +
                ")");

        queries.add("CREATE TABLE " + PEOPLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "birthYear TEXT," +
                "eyeColor TEXT, " +
                "gender TEXT," +
                "hairColor TEXT," +
                "height TEXT," +
                "mass TEXT," +
                "skinColor TEXT," +
                "url TEXT," +
                "edited TEXT," +
                "homeworldId INTEGER," +
                "personSpeciesId INTEGER," +
                "FOREIGN KEY (homeworldId) REFERENCES " + PLANET + "(id)" +
                ")");

        queries.add("CREATE TABLE " + PERSONSPECIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "peopleId INTEGER," +
                "speciesId INTEGER," +
                "FOREIGN KEY (speciesId) REFERENCES " + SPECIES + "(id)," +
                "FOREIGN KEY (peopleId) REFERENCES " + PEOPLE + "(id)" +
                ")");

        queries.add("CREATE TABLE " + TEAM + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "planetId INTEGER," +
                "isSystemEntity INTEGER," +
                "FOREIGN KEY (planetId) REFERENCES " + PLANET + "(id)" +
                ")");

        queries.add("CREATE TABLE " + PICTURE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "path TEXT" +
                ")");

        queries.add("CREATE TABLE " + MEMBER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "speed INTEGER," +
                "attack INTEGER," +
                "defense INTEGER," +
                "experience INTEGER," +
                "expToLevel INTEGER," +
                "level INTEGER," +
                "healthPoints INTEGER," +
                "isSystemEntity INTEGER," +
                "teamId INTEGER," +
                "peopleId INTEGER," +
                "pictureId INTEGER," +
                "FOREIGN KEY (teamId) REFERENCES " + TEAM + "(id)," +
                "FOREIGN KEY (peopleId) REFERENCES " + PEOPLE + "(id)," +
                "FOREIGN KEY (pictureId) REFERENCES " + PICTURE + "(id)" +
                ")");

        try {
            for (String query : queries) {
                //Log.e("SQL", query);
                db.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("SQL", "Could not create the tables!", e);
        }

        // TODO: REMOVE this
        insertTestData(db);
    }

    // methode wordt uitgevoerd als database geupgrade wordt
    // hierin de vorige tabellen wegdoen en opnieuw creëren
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PICTURE);
        db.execSQL("DROP TABLE IF EXISTS " + PLANET);
        db.execSQL("DROP TABLE IF EXISTS " + SPECIES);
        db.execSQL("DROP TABLE IF EXISTS " + PEOPLE);
        db.execSQL("DROP TABLE IF EXISTS " + PERSONSPECIES);
        db.execSQL("DROP TABLE IF EXISTS " + MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TEAM);

        // Create tables again
        onCreate(db);
    }

    private void insertTestData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Team (name, planetId) VALUES ('The Sith Destroyers', 2)");

        db.execSQL("INSERT INTO Picture (path) VALUES ('profile_default.jpg');");
    }

    /*************************************************************************
     * CRUD
     ************************************************************************/
    private ContentValues getContentValues(Planet planet) {
        ContentValues values = new ContentValues();

        values.put("id", planet.getId());
        values.put("name", planet.getName());
        values.put("diameter", planet.getDiameter());
        values.put("rotationPeriod", planet.getRotationPeriod());
        values.put("orbitalPeriod", planet.getOrbitalPeriod());
        values.put("gravity", planet.getGravity());
        values.put("population", planet.getPopulation());
        values.put("climate", planet.getClimate());
        values.put("terrain", planet.getTerrain());
        values.put("surfaceWater", planet.getSurfaceWater());
        values.put("residents", planet.getResidents());
        values.put("url", planet.getUrl());
        values.put("edited", planet.getEdited());

        return values;
    }

    private ContentValues getContentValues(Team team) {
        ContentValues values = new ContentValues();

        values.put("id", team.getId());
        values.put("name", team.getName());
        values.put("planetId", team.getPlanetId());
        values.put("isSystemEntity", team.isSystemEntity() ? 1 : 0);

        return values;
    }

    private ContentValues getContentValues(Picture picture) {
        ContentValues values = new ContentValues();

        values.put("id", picture.getId());
        values.put("path", picture.getPath());

        return values;
    }


    private ContentValues getContentValues(Member member) {
        ContentValues values = new ContentValues();

        values.put("id", member.getId());
        values.put("speed", member.getSpeed());
        values.put("attack", member.getAttack());
        values.put("defense", member.getDefense());
        values.put("experience", member.getExperience());
        values.put("expToLevel", member.getExpToLevel());
        values.put("level", member.getLevel());
        values.put("healthPoints", member.getHealthPoints());
        values.put("isSystemEntity", member.isSystemEntity() ? 1 : 0);
        values.put("pictureId", member.getPictureId());
        values.put("teamId", member.getTeamId());
        values.put("peopleId", member.getPeopleId());

        return values;
    }

    private ContentValues getContentValues(People person) {
        ContentValues values = new ContentValues();

        values.put("id", person.getId());
        values.put("name", person.getName());
        values.put("birthYear", person.getBirthYear());
        values.put("eyeColor", person.getEyeColor());
        values.put("gender", person.getGender());
        values.put("hairColor", person.getHairColor());
        values.put("height", person.getHeight());
        values.put("mass", person.getMass());
        values.put("skinColor", person.getSkinColor());
        values.put("url", person.getUrl());
        values.put("edited", person.getEdited());
        values.put("homeworldId", person.getHomeworldId());

        return values;
    }

    private ContentValues getContentValues(PersonSpecies personSpecies) {
        ContentValues values = new ContentValues();

        values.put("id", personSpecies.getId());
        values.put("peopleId", personSpecies.getPeopleId());
        values.put("speciesId", personSpecies.getSpeciesId());

        return values;
    }

    private ContentValues getContentValues(Species species) {
        ContentValues values = new ContentValues();

        values.put("id", species.getId());
        values.put("name", species.getName());
        values.put("classification", species.getClassification());
        values.put("designation", species.getDesignation());
        values.put("averageHeight", species.getAverageHeight());
        values.put("averageLifespan", species.getAverageLifespan());
        values.put("eyeColors", species.getEyeColors());
        values.put("hairColors", species.getHairColors());
        values.put("skinColors", species.getSkinColors());
        values.put("language", species.getLanguage());
        values.put("url", species.getUrl());
        values.put("edited", species.getEdited());
        values.put("homeworldId", species.getHomeworldId());

        return values;
    }

    private ContentValues getContentValues(String table, Object o) {
        ContentValues values;

        switch (table) {
            case PLANET:
                values = getContentValues((Planet)o);
                break;
            case TEAM:
                values = getContentValues((Team)o);
                break;
            case MEMBER:
                values = getContentValues((Member)o);
                break;
            case PICTURE:
                values = getContentValues((Picture)o);
                break;
            case PEOPLE:
                values = getContentValues((People)o);
                break;
            case PERSONSPECIES:
                values = getContentValues((PersonSpecies)o);
                break;
            case SPECIES:
                values = getContentValues((Species)o);
                break;
            default:
                values = new ContentValues();
                break;
        }

        return values;
    }

    private long genericInsert(String table, Object o) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getContentValues(table, o);

        values.remove("id");

        long id = db.insert(table, null, values);
        db.close();

        return id;
    }

    private boolean genericUpdate(String table, ContentValues values, long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int numrows = db.update(
                table,
                values,
                "id = ?",
                new String[] { String.valueOf(id) }
        );
        db.close();

        return numrows > 0;
    }

    private boolean genericDelete(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int numrows = db.delete(
                table,
                "id = ?",
                new String[] { String.valueOf(id) });
        db.close();

        return numrows > 0;
    }

    private boolean genericDeleteAll(String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        int numrows = db.delete(table, null, null);
        db.close();

        return numrows > 0;
    }

    public <T> boolean genericInsertAll(String table, List<T> objects) {
        boolean result = true;

        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        for (T obj : objects) {
            id = db.insert(table, null, getContentValues(table, obj));

            if (id == -1)
                result = false;
        }

        db.close();
        return result;
    }

    private int genericGetCount(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                table,                               // Tabel
                new String[] {                          // Kolommen
                        "id"
                },
                null,                               // Where
                null,    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );
        int aantal = cursor.getCount();

        cursor.close();
        db.close();

        return aantal;
    }

    public void cleanUpTeamAndMembers() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TEAM, "isSystemEntity=?", new String[] { String.valueOf(1) });
        db.delete(MEMBER, "isSystemEntity=?", new String[] { String.valueOf(1) });

        db.close();
    }

    //=======================
    //  PLANET
    //=======================

    // -- Create
    public long insertPlanet(Planet planet) {
        return genericInsert(PLANET, planet);
    }

    public boolean insertPlanets(List<Planet> planets) {
        return genericInsertAll(PLANET, planets);
    }

    // -- Read
    public Planet getPlanet(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PLANET,                               // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "diameter",
                        "rotationPeriod",
                        "orbitalPeriod",
                        "gravity",
                        "population",
                        "climate",
                        "terrain",
                        "surfaceWater",
                        "residents",
                        "url",
                        "edited"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Planet planet = new Planet();

        if (cursor.moveToFirst()) {
            planet.setId(cursor.getLong(0));
            planet.setName(cursor.getString(1));
            planet.setDiameter(cursor.getString(2));
            planet.setRotationPeriod(cursor.getString(3));
            planet.setOrbitalPeriod(cursor.getString(4));
            planet.setGravity(cursor.getString(5));
            planet.setPopulation(cursor.getString(6));
            planet.setClimate(cursor.getString(7));
            planet.setTerrain(cursor.getString(8));
            planet.setSurfaceWater(cursor.getString(9));
            planet.setResidents(cursor.getString(10));
            planet.setUrl(cursor.getString(11));
            planet.setEdited(cursor.getString(12));
        }

        cursor.close();
        db.close();

        return planet;
    }

    public List<Planet> getAllPlanets() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PLANET,                               // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "diameter",
                        "rotationPeriod",
                        "orbitalPeriod",
                        "gravity",
                        "population",
                        "climate",
                        "terrain",
                        "surfaceWater",
                        "residents",
                        "url",
                        "edited"
                },
                null,                               // Where
                null,    // Where-params
                null,                                   // Group By
                null,                                   // Having
                "name ASC",                                   // Sorting
                null                                    // Dunno
        );

        List<Planet> planets = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Planet planet = new Planet();

                planet.setId(cursor.getLong(0));
                planet.setName(cursor.getString(1));
                planet.setDiameter(cursor.getString(2));
                planet.setRotationPeriod(cursor.getString(3));
                planet.setOrbitalPeriod(cursor.getString(4));
                planet.setGravity(cursor.getString(5));
                planet.setPopulation(cursor.getString(6));
                planet.setClimate(cursor.getString(7));
                planet.setTerrain(cursor.getString(8));
                planet.setSurfaceWater(cursor.getString(9));
                planet.setResidents(cursor.getString(10));
                planet.setUrl(cursor.getString(11));
                planet.setEdited(cursor.getString(12));

                planets.add(planet);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return planets;
    }

    public int getPlanetsCount() {
        return genericGetCount(PLANET);
    }

    // -- Update
    public boolean updatePlanet(Planet planet) {
        return genericUpdate(PLANET, getContentValues(planet), planet.getId());
    }

    // -- Delete
    public boolean deletePlanet(long id) {
        return genericDelete(PLANET, id);
    }
    public boolean deleteAllPlanets() {
        return genericDeleteAll(PLANET);
    }

    //=======================
    //  TEAM
    //=======================
    // -- Create
    public long insertTeam(Team obj) {
        return genericInsert(TEAM, obj);
    }

    public Team insertTeamFull(Team obj) {
        obj.setId(genericInsert(TEAM, obj));

        if (obj.getId() != -1) {
            for (Member m : obj.getMembers()) {
                m.setId(genericInsert(MEMBER, m));
            }
        }

        return obj;
    }


    // -- Read
    public Team getTeam(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TEAM,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "planetId",
                        "isSystemEntity"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Team obj = new Team();

        if (cursor.moveToFirst()) {
            obj.setId(cursor.getLong(0));
            obj.setName(cursor.getString(1));
            obj.setPlanetId(cursor.getLong(2));
            obj.setSystemEntity((cursor.getInt(3) == 1) ? true : false);
        }

        cursor.close();
        db.close();

        return obj;
    }

    public Team getTeamFull(long id) {
        Team team = getTeam(id);
        team.setMembers(getMembers(team.getId()));
        return team;
    }

    public List<Team> getAllTeamsWithMembers() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                MEMBER,                                 // Tabel
                new String[] {                          // Kolommen
                        "teamId"
                },
                null,                               // Where
                null,    // Where-params
                "teamId",                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        List<Team> members = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                members.add(getTeam(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return members;
    }

    public List<Team> getAllTeams() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TEAM,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "planetId",
                        "isSystemEntity"
                },
                null,                                   // Where
                null,                                   // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        List<Team> teams = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Team obj = new Team();

                obj.setId(cursor.getLong(0));
                obj.setName(cursor.getString(1));
                obj.setPlanetId(cursor.getLong(2));
                obj.setSystemEntity((cursor.getInt(3) == 1) ? true : false);

                obj.setPlanet(getPlanet(obj.getPlanetId()));

                teams.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return teams;
    }

    public int getTeamsCount() {
        return genericGetCount(TEAM);
    }

    // -- Update
    public boolean updateTeam(Team obj) {
        return genericUpdate(TEAM, getContentValues(obj), obj.getId());
    }

    // -- Delete
    public boolean deleteTeam(long id) {
        List<Member> members = getMembers(id);
        boolean result = true;

        for (Member m : members) {
            result &= deleteMember(m.getId());
        }

        return result && genericDelete(TEAM, id);
    }
    public boolean deleteAllTeams() {
        return genericDeleteAll(TEAM);
    }

    public int getAverageLevelOfTeam(long teamId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                MEMBER,                                 // Tabel
                new String[] {                          // Kolommen
                        "AVG(level)",
                },
                "teamId = ?",                               // Where
                new String[] { String.valueOf(teamId) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        int avg = 0;

        if (cursor.moveToFirst()) {
            cursor.getLong(0);
        }

        cursor.close();
        db.close();

        return avg;
    }


    //=======================
    //  PICTURE
    //=======================
    // -- Create
    public long insertPicture(Picture obj) {
        return genericInsert(PICTURE, obj);
    }

    // -- Read
    public Picture getPicture(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PICTURE,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "path"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Picture obj = new Picture();

        if (cursor.moveToFirst()) {
            obj.setId(cursor.getLong(0));
            obj.setPath(cursor.getString(1));
        }

        cursor.close();
        db.close();

        return obj;
    }

    public Picture getPicture(String path) {
        if (!doesPicturePathExist(path)) {
            Picture picture = new Picture();
            picture.setPath(path);

            long id = insertPicture(picture);

            return getPicture(id);
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PICTURE,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "path"
                },
                "path = ?",                               // Where
                new String[] { String.valueOf(path) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Picture obj = new Picture();

        if (cursor.moveToFirst()) {
            obj.setId(cursor.getLong(0));
            obj.setPath(cursor.getString(1));
        }

        cursor.close();
        db.close();

        return obj;
    }

    public boolean doesPicturePathExist(String path) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PICTURE,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "path"
                },
                "path = ?",                               // Where
                new String[] { String.valueOf(path) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        boolean result = false;
        if (cursor.moveToFirst()) {
            result = true;
        }

        cursor.close();
        db.close();

        return result;
    }

    public int getPicturesCount() {
        return genericGetCount(PICTURE);
    }

    // -- Update
    public boolean updatePicture(Picture obj) {
        return genericUpdate(PICTURE, getContentValues(obj), obj.getId());
    }

    // -- Delete
    public boolean deletePicture(long id) {
        return genericDelete(PICTURE, id);
    }

    //=======================
    //  SPECIES
    //=======================
    // -- Create
    public long insertSpecies(Species obj) {
        return genericInsert(SPECIES, obj);
    }

    public boolean insertSpecies(List<Species> objects) {
        return genericInsertAll(SPECIES, objects);
    }

    // -- Read
    public Species getSpecies(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                SPECIES,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "classification",
                        "designation",
                        "averageHeight",
                        "averageLifespan",
                        "eyeColors",
                        "hairColors",
                        "skinColors",
                        "language",
                        "url",
                        "edited",
                        "homeworldId"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Species obj = new Species();

        if (cursor.moveToFirst()) {
            obj.setId(cursor.getLong(0));
            obj.setName(cursor.getString(1));
            obj.setClassification(cursor.getString(2));
            obj.setDesignation(cursor.getString(3));
            obj.setAverageHeight(cursor.getString(4));
            obj.setAverageLifespan(cursor.getString(5));
            obj.setEyeColors(cursor.getString(6));
            obj.setHairColors(cursor.getString(7));
            obj.setSkinColors(cursor.getString(8));
            obj.setLanguage(cursor.getString(9));
            obj.setUrl(cursor.getString(10));
            obj.setEdited(cursor.getString(11));
            obj.setHomeworldId(cursor.getLong(12));
        }

        cursor.close();
        db.close();

        return obj;
    }

    public List<Species> getAllSpecies() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                SPECIES,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "classification",
                        "designation",
                        "averageHeight",
                        "averageLifespan",
                        "eyeColors",
                        "hairColors",
                        "skinColors",
                        "language",
                        "url",
                        "edited",
                        "homeworldId"
                },
                null,                               // Where
                null,    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );


        List<Species> species = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Species obj = new Species();

                obj.setId(cursor.getLong(0));
                obj.setName(cursor.getString(1));
                obj.setClassification(cursor.getString(2));
                obj.setDesignation(cursor.getString(3));
                obj.setAverageHeight(cursor.getString(4));
                obj.setAverageLifespan(cursor.getString(5));
                obj.setEyeColors(cursor.getString(6));
                obj.setHairColors(cursor.getString(7));
                obj.setSkinColors(cursor.getString(8));
                obj.setLanguage(cursor.getString(9));
                obj.setUrl(cursor.getString(10));
                obj.setEdited(cursor.getString(11));
                obj.setHomeworldId(cursor.getLong(12));

                species.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return species;
    }

    public int getSpeciesCount() {
        return genericGetCount(SPECIES);
    }

    // -- Update
    public boolean updateSpecies(Species obj) {
        return genericUpdate(SPECIES, getContentValues(obj), obj.getId());
    }

    // -- Delete
    public boolean deleteSpecies(long id) {
        return genericDelete(SPECIES, id);
    }
    public boolean deleteAllSpecies() {
        return genericDeleteAll(SPECIES);
    }

    //=======================
    //  PEOPLE
    //=======================
    // -- Create
    public long insertPeople(People obj) {
        return genericInsert(PEOPLE, obj);
    }

    public boolean insertPeoples(List<People> objects) {
        return genericInsertAll(PEOPLE, objects);
    }

    // -- Read
    public People getPeople(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PEOPLE,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "birthYear",
                        "eyeColor",
                        "gender",
                        "hairColor",
                        "height",
                        "mass",
                        "skinColor",
                        "url",
                        "edited",
                        "homeworldId"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        People obj = new People();

        if (cursor.moveToFirst()) {
            obj.setId(cursor.getLong(0));
            obj.setName(cursor.getString(1));
            obj.setBirthYear(cursor.getString(2));
            obj.setEyeColor(cursor.getString(3));
            obj.setGender(cursor.getString(4));
            obj.setHairColor(cursor.getString(5));
            obj.setHeight(cursor.getString(6));
            obj.setMass(cursor.getString(7));
            obj.setSkinColor(cursor.getString(8));
            obj.setUrl(cursor.getString(9));
            obj.setEdited(cursor.getString(10));
            obj.setHomeworldId(cursor.getLong(11));
        }

        cursor.close();
        db.close();

        return obj;
    }

    public List<People> getAllPeople() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PEOPLE,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "name",
                        "birthYear",
                        "eyeColor",
                        "gender",
                        "hairColor",
                        "height",
                        "mass",
                        "skinColor",
                        "url",
                        "edited",
                        "homeworldId"
                },
                null,                               // Where
                null,    // Where-params
                null,                                   // Group By
                null,                                   // Having
                "name ASC",                                   // Sorting
                null                                    // Dunno
        );


        List<People> people = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                People obj = new People();

                obj.setId(cursor.getLong(0));
                obj.setName(cursor.getString(1));
                obj.setBirthYear(cursor.getString(2));
                obj.setEyeColor(cursor.getString(3));
                obj.setGender(cursor.getString(4));
                obj.setHairColor(cursor.getString(5));
                obj.setHeight(cursor.getString(6));
                obj.setMass(cursor.getString(7));
                obj.setSkinColor(cursor.getString(8));
                obj.setUrl(cursor.getString(9));
                obj.setEdited(cursor.getString(10));
                obj.setHomeworldId(cursor.getLong(11));

                people.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return people;
    }

    public int getPeopleCount() {
        return genericGetCount(PEOPLE);
    }

    // -- Update
    public boolean updatePeople(People obj) {
        return genericUpdate(PEOPLE, getContentValues(obj), obj.getId());
    }

    // -- Delete
    public boolean deletePeople(long id) {
        return genericDelete(PEOPLE, id);
    }
    public boolean deleteAllPeople() {
        return genericDeleteAll(PEOPLE);
    }

    //=======================
    //  PERSONSPECIES
    //=======================
    // -- Create
    public long insertPersonSpecies(PersonSpecies obj) {
        return genericInsert(PERSONSPECIES, obj);
    }

    // -- Read
    public PersonSpecies getPersonSpecies(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PERSONSPECIES,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "peopleId",
                        "speciesId"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        PersonSpecies obj = new PersonSpecies();

        if (cursor.moveToFirst()) {
            obj.setId(cursor.getLong(0));
            obj.setPeopleId(cursor.getLong(1));
            obj.setSpeciesId(cursor.getLong(2));
        }

        cursor.close();
        db.close();

        return obj;
    }

    public int getPersonSpeciesCount() {
        return genericGetCount(PERSONSPECIES);
    }

    // -- Update
    public boolean updatePersonSpecies(PersonSpecies obj) {
        return genericUpdate(PERSONSPECIES, getContentValues(obj), obj.getId());
    }

    // -- Delete
    public boolean deletePersonSpecies(long id) {
        return genericDelete(PERSONSPECIES, id);
    }

    //=======================
    //  MEMBER
    //=======================
    // -- Create
    public long insertMember(Member obj) {
        return genericInsert(MEMBER, obj);
    }

    // -- Read
    public Member getMember(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                MEMBER,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "speed",
                        "attack",
                        "defense",
                        "experience",
                        "expToLevel",
                        "level",
                        "healthPoints",
                        "isSystemEntity",
                        "teamId",
                        "peopleId",
                        "pictureId"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Member obj = new Member();

        if (cursor.moveToFirst()) {
            obj.setId(cursor.getLong(0));
            obj.setSpeed(cursor.getInt(1));
            obj.setAttack(cursor.getInt(2));
            obj.setDefense(cursor.getInt(3));
            obj.setExperience(cursor.getInt(4));
            obj.setExpToLevel(cursor.getInt(5));
            obj.setLevel(cursor.getInt(6));
            obj.setHealthPoints(cursor.getInt(7));
            obj.setSystemEntity((cursor.getInt(8) == 1) ? true : false);
            obj.setTeamId(cursor.getLong(9));
            obj.setPeopleId(cursor.getLong(10));
            obj.setPictureId(cursor.getLong(11));

            obj.setPerson(getPeople(obj.getPeopleId()));
            obj.setTeam(getTeam(obj.getTeamId()));
            obj.setPicture(getPicture(obj.getPictureId()));
        }

        cursor.close();
        db.close();

        return obj;
    }

    public List<Member> getMembers(long teamId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                MEMBER,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "speed",
                        "attack",
                        "defense",
                        "experience",
                        "expToLevel",
                        "level",
                        "healthPoints",
                        "isSystemEntity",
                        "teamId",
                        "peopleId",
                        "pictureId"
                },
                "teamId = ?",                               // Where
                new String[] { String.valueOf(teamId) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        List<Member> members = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Member obj = new Member();

                obj.setId(cursor.getLong(0));
                obj.setSpeed(cursor.getInt(1));
                obj.setAttack(cursor.getInt(2));
                obj.setDefense(cursor.getInt(3));
                obj.setExperience(cursor.getInt(4));
                obj.setExpToLevel(cursor.getInt(5));
                obj.setLevel(cursor.getInt(6));
                obj.setHealthPoints(cursor.getInt(7));
                obj.setSystemEntity((cursor.getInt(8) == 1) ? true : false);
                obj.setTeamId(cursor.getLong(9));
                obj.setPeopleId(cursor.getLong(10));
                obj.setPictureId(cursor.getLong(11));

                obj.setPerson(getPeople(obj.getPeopleId()));
                obj.setTeam(getTeam(obj.getTeamId()));
                obj.setPicture(getPicture(obj.getPictureId()));

                members.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return members;
    }

    private List<Member> getAllMembers() {
        return _getAllMembers(true);
    }

    private List<Member> getAllMembersNonFull() {
        return _getAllMembers(false);
    }

    private List<Member> _getAllMembers(boolean full) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                MEMBER,                                 // Tabel
                new String[] {                          // Kolommen
                        "id",
                        "speed",
                        "attack",
                        "defense",
                        "experience",
                        "expToLevel",
                        "level",
                        "healthPoints",
                        "isSystemEntity",
                        "teamId",
                        "peopleId",
                        "pictureId"
                },
                null,                               // Where
                null,    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        List<Member> members = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Member obj = new Member();

                obj.setId(cursor.getLong(0));
                obj.setSpeed(cursor.getInt(1));
                obj.setAttack(cursor.getInt(2));
                obj.setDefense(cursor.getInt(3));
                obj.setExperience(cursor.getInt(4));
                obj.setExpToLevel(cursor.getInt(5));
                obj.setLevel(cursor.getInt(6));
                obj.setHealthPoints(cursor.getInt(7));
                obj.setSystemEntity((cursor.getInt(8) == 1) ? true : false);
                obj.setTeamId(cursor.getLong(9));
                obj.setPeopleId(cursor.getLong(10));
                obj.setPictureId(cursor.getLong(11));

                if (full) {
                    obj.setPerson(getPeople(obj.getPeopleId()));
                    obj.setTeam(getTeam(obj.getTeamId()));
                    obj.setPicture(getPicture(obj.getPictureId()));
                }

                members.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return members;
    }

    public int getMembersCount() {
        return genericGetCount(MEMBER);
    }

    // -- Update
    public boolean updateMember(Member obj) {
        return genericUpdate(MEMBER, getContentValues(obj), obj.getId());
    }

    // -- Delete
    public boolean deleteMember(long id) {
        return genericDelete(MEMBER, id);
    }
}
