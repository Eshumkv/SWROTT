package be.thomasmore.rott.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * DatabaseHelper implements helper methods for working with the SQLite database.
 *
 * @author Koen Vanduffel
 * @version 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * The database version. Can only be incremented.
     * When incremented, the database will be recreated.
     */
    private static final int DATABASE_VERSION = 22;

    /**
     * The name of the database.
     */
    private static final String DATABASE_NAME = "swrott";

    /**
     * Table name of the table Planet
     */
    private static final String PLANET = "Planet";

    /**
     * Table name of the table Team
     */
    private static final String TEAM = "Team";

    /**
     * Table name of the table Member
     */
    private static final String MEMBER = "Member";

    /**
     * Table name of the table People
     */
    private static final String PEOPLE = "People";

    /**
     * Table name of the table Picture
     */
    private static final String PICTURE = "Picture";

    /**
     * Table name of the table PersonSpecies
     */
    private static final String PERSONSPECIES = "PersonSpecies";

    /**
     * Table name of the table Species
     */
    private static final String SPECIES = "Species";

    /**
     * Constructor
     * @param context The context of the view that called it.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Method that gets executed when the database gets created.
     * Create the tables and optionally fill it with data.
     * @param db A handle to the newly created database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Make a list of queries that you want to execute (CREATE statements)
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
                "people TEXT," +
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
                "species TEXT," +
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
                "base_speed INTEGER," +
                "base_attack INTEGER," +
                "base_defense INTEGER," +
                "base_hp INTEGER," +
                "iv_speed INTEGER," +
                "iv_attack INTEGER," +
                "iv_defense INTEGER," +
                "iv_hp INTEGER," +
                "ev_speed INTEGER," +
                "ev_attack INTEGER," +
                "ev_defense INTEGER," +
                "ev_hp INTEGER," +
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
            // Execute all the queries
            for (String query : queries) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("SQL", "Could not create the tables!", e);
        }

        insertTestData(db);
    }

    /**
     * Method that gets executed when the database gets upgraded.
     * @param db The new database
     * @param oldVersion The old version number
     * @param newVersion The new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
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

    /**
     * Inserts some testdata into the database.
     * @param db The database into which you want to insert.
     */
    private void insertTestData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Team (name, planetId) VALUES ('The Sith Destroyers', 2)");

        db.execSQL("INSERT INTO Picture (path) VALUES ('profile_default.jpg');");
    }

    /*************************************************************************/

    /**
     * Get the contentvalues needed for the Planet table
     * @param planet The planet which you want to adjust/insert.
     * @return ContentValues The needed values.
     */
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

    /**
     * Get the contentvalues needed for the Team table
     * @param team The team you want to adjust/insert.
     * @return ContentValues The needed values.
     */
    private ContentValues getContentValues(Team team) {
        ContentValues values = new ContentValues();

        values.put("id", team.getId());
        values.put("name", team.getName());
        values.put("planetId", team.getPlanetId());
        values.put("isSystemEntity", team.isSystemEntity() ? 1 : 0);

        return values;
    }

    /**
     * Get the contentvalues needed for the Team table
     * @param picture The picture you want to adjust/insert.
     * @return ContentValues The needed values.
     */
    private ContentValues getContentValues(Picture picture) {
        ContentValues values = new ContentValues();

        values.put("id", picture.getId());
        values.put("path", picture.getPath());

        return values;
    }


    /**
     * Get the contentvalues needed for the Team table
     * @param member The member you want to adjust/insert.
     * @return ContentValues The needed values.
     */
    private ContentValues getContentValues(Member member) {
        ContentValues values = new ContentValues();

        values.put("id", member.getId());

        values.put("base_speed", member.getBase_speed());
        values.put("base_attack", member.getBase_attack());
        values.put("base_defense", member.getBase_defense());
        values.put("base_hp", member.getBase_hp());

        values.put("iv_speed", member.getIv_speed());
        values.put("iv_attack", member.getIv_attack());
        values.put("iv_defense", member.getIv_attack());
        values.put("iv_hp", member.getIv_hp());

        values.put("ev_speed", member.getEv_speed());
        values.put("ev_attack", member.getEv_attack());
        values.put("ev_defense", member.getEv_defense());
        values.put("ev_hp", member.getEv_hp());

        values.put("speed", member.getSpeed());
        values.put("attack", member.getAttack());
        values.put("defense", member.getDefense());
        values.put("healthPoints", member.getHealthPoints());

        values.put("experience", member.getExperience());
        values.put("expToLevel", member.getExpToLevel());
        values.put("level", member.getLevel());
        values.put("isSystemEntity", member.isSystemEntity() ? 1 : 0);
        values.put("pictureId", member.getPictureId());
        values.put("teamId", member.getTeamId());
        values.put("peopleId", member.getPeopleId());

        return values;
    }

    /**
     * Get the contentvalues needed for the Team table
     * @param person The people(person) you want to adjust/insert.
     * @return ContentValues The needed values.
     */
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
        values.put("species", person.getSpecies());

        return values;
    }

    /**
     * Get the contentvalues needed for the Team table
     * @param personSpecies The personSpecies you want to adjust/insert.
     * @return ContentValues The needed values.
     */
    private ContentValues getContentValues(PersonSpecies personSpecies) {
        ContentValues values = new ContentValues();

        values.put("id", personSpecies.getId());
        values.put("peopleId", personSpecies.getPeopleId());
        values.put("speciesId", personSpecies.getSpeciesId());

        return values;
    }

    /**
     * Get the contentvalues needed for the Team table
     * @param species The team you want to adjust/insert.
     * @return ContentValues The needed values.
     */
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
        values.put("people", species.getPeople());

        return values;
    }

    /**
     * Get the contentvalues based on the tablename.
     * @param table The name of the table.
     * @param o The object used to get the contentvalues.
     * @return ContentValues The needed values or an empty ContentValues if the table name is not found.
     */
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

    /*************************************************************************/

    /**
     * A helper method to do a generic insert into the database.
     * @param table The table name
     * @param o The object to insert.
     * @return long The id of the inserted object.
     */
    private long genericInsert(String table, Object o) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getContentValues(table, o);

        values.remove("id");

        long id = db.insert(table, null, values);
        db.close();

        return id;
    }

    /**
     * A helper method to do a generic update
     * @param table The table name
     * @param values The ContentValues
     * @param id The id to update
     * @return boolean true if succeeded, else false
     */
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

    /**
     * A helper method to delete an id from a table.
     * @param table The table to delete from.
     * @param id The id to delete
     * @return boolean True if success, else false.
     */
    private boolean genericDelete(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int numrows = db.delete(
                table,
                "id = ?",
                new String[] { String.valueOf(id) });
        db.close();

        return numrows > 0;
    }

    /**
     * Helper method to delete all rows in a table.
     * @param table The table to clear.
     * @return boolean True if success, else false.
     */
    private boolean genericDeleteAll(String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        int numrows = db.delete(table, null, null);
        db.close();

        return numrows > 0;
    }

    /**
     * A helper method to insert a list of objects into a table
     * @param table The table to insert into
     * @param objects The objects to insert
     * @param <T> Type param
     * @return boolean True if all objects where inserted, else false.
     */
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

    /**
     * A helper method to get the count of a specific table.
     * @param table The table name to get the count of.
     * @return int The number of rows in the table.
     */
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

    /*************************************************************************/

    /**
     * Helper method to delete all system entities in the database.
     */
    public void cleanUpTeamAndMembers() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TEAM, "isSystemEntity=?", new String[] { String.valueOf(1) });
        db.delete(MEMBER, "isSystemEntity=?", new String[] { String.valueOf(1) });

        db.close();
    }

    /*************************************************************************/
    /*************************************************************************/

    //=======================
    //  PLANET
    //=======================

    // -- Create

    /**
     * Insert a planet into the database.
     * @param planet The planet to insert
     * @return long The id of the inserted planet
     */
    public long insertPlanet(Planet planet) {
        return genericInsert(PLANET, planet);
    }

    /**
     * Inserts a list of planets into the database.
     * @param planets The list of planets
     * @return boolean True if successful, else false
     */
    public boolean insertPlanets(List<Planet> planets) {
        return genericInsertAll(PLANET, planets);
    }

    /*************************************************************************/

    // -- Read

    /**
     * Retrieve a planet with the id from the database.
     * @param id The id that you want
     * @return Planet The planet that you want or null.
     */
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

        Planet planet = null;

        if (cursor.moveToFirst()) {
            planet = new Planet();

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

    /**
     * Retrieve all planets from the database.
     * @return List A list of planets
     */
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

    /**
     * Retrieve a count of the Planet table
     * @return int The number of rows in the Planet table
     */
    public int getPlanetsCount() {
        return genericGetCount(PLANET);
    }

    /*************************************************************************/

    // -- Update

    /**
     * Update a planet
     * @param planet The planet to update
     * @return boolean true if successful, else false.
     */
    public boolean updatePlanet(Planet planet) {
        return genericUpdate(PLANET, getContentValues(planet), planet.getId());
    }

    /*************************************************************************/

    // -- Delete

    /**
     * Delete a planet
     * @param id The id to delete
     * @return boolean True if successful, else false.
     */
    public boolean deletePlanet(long id) {
        return genericDelete(PLANET, id);
    }

    /**
     * Delete all planets
     * @return boolean True if successful, else false.
     */
    public boolean deleteAllPlanets() {
        return genericDeleteAll(PLANET);
    }

    /*************************************************************************/
    /*************************************************************************/

    //=======================
    //  TEAM
    //=======================

    // -- Create

    /**
     * Insert a team into the database.
     * @param obj The team to insert.
     * @return long The id of the inserted item.
     */
    public long insertTeam(Team obj) {
        return genericInsert(TEAM, obj);
    }

    /**
     * Insert a team into the database, but also insert the members of this team.
     * @param obj The team to insert (with members)
     * @return Team The team that was inserted with ids.
     */
    public Team insertTeamFull(Team obj) {
        obj.setId(genericInsert(TEAM, obj));

        if (obj.getId() != -1) {
            for (Member m : obj.getMembers()) {
                m.setId(genericInsert(MEMBER, m));
            }
        }

        return obj;
    }

    /*************************************************************************/

    // -- Read

    /**
     * Get a single team from the database
     * @param id The id to retrieve
     * @return Team The team or null.
     */
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

        Team obj = null;

        if (cursor.moveToFirst()) {
            obj = new Team();

            obj.setId(cursor.getLong(0));
            obj.setName(cursor.getString(1));
            obj.setPlanetId(cursor.getLong(2));
            obj.setSystemEntity((cursor.getInt(3) == 1) ? true : false);
        }

        cursor.close();
        db.close();

        return obj;
    }

    /**
     * Get a team, with members.
     * @param id The id of the team
     * @return Team The team with members or null.
     */
    public Team getTeamFull(long id) {
        Team team = getTeam(id);

        if (team == null)
            return null;

        team.setMembers(getMembers(team.getId()));

        return team;
    }

    /**
     * Gets all teams, with members
     * @return List A list of teams with the members
     */
    public List<Team> getAllTeamsWithMembers() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                MEMBER,                                 // Tabel
                new String[] {                          // Kolommen
                        "teamId"
                },
                "isSystemEntity=?",                               // Where
                new String[] { String.valueOf(0) },    // Where-params
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

    /**
     * Get all teams, without members
     * @return List A list of teams, but the teams do not have members added (Does not mean they don't have members)
     */
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
                obj.setSystemEntity(cursor.getInt(3) == 1);

                obj.setPlanet(getPlanet(obj.getPlanetId()));

                teams.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return teams;
    }

    /**
     * Get a count of the team table
     * @return int Number of rows in the Team table
     */
    public int getTeamsCount() {
        return genericGetCount(TEAM);
    }

    /*************************************************************************/

    // -- Update

    /**
     * Update a team
     * @param obj The team to update.
     * @return boolean True if successful, else false.
     */
    public boolean updateTeam(Team obj) {
        return genericUpdate(TEAM, getContentValues(obj), obj.getId());
    }

    /*************************************************************************/

    // -- Delete

    /**
     * Delete a team
     * @param id The id of the team to delete
     * @return boolean True if successful, else false.
     */
    public boolean deleteTeam(long id) {
        List<Member> members = getMembers(id);
        boolean result = true;

        for (Member m : members) {
            result &= deleteMember(m.getId());
        }

        return result && genericDelete(TEAM, id);
    }

    /**
     * Deletes all teams in the databse
     * @return boolean True if successful, else false.
     */
    public boolean deleteAllTeams() {
        return genericDeleteAll(TEAM);
    }

    /*************************************************************************/
    /*************************************************************************/

    //=======================
    //  PICTURE
    //=======================

    // -- Create

    /**
     * Insert a picture into the database
     * @param obj The picture
     * @return long the inserted id.
     */
    public long insertPicture(Picture obj) {
        return genericInsert(PICTURE, obj);
    }

    /*************************************************************************/

    // -- Read

    /**
     * Get a picture
     * @param id The id of the picture to get
     * @return Picture The picture or null
     */
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

        Picture obj = null;

        if (cursor.moveToFirst()) {
            obj = new Picture();

            obj.setId(cursor.getLong(0));
            obj.setPath(cursor.getString(1));
        }

        cursor.close();
        db.close();

        return obj;
    }

    /**
     * Get the first picture with a specified path. If no picture with the
     * given path is found, it will be created.
     * @param path The path to search for
     * @return Picture The picture with the specified path.
     */
    public Picture getPicture(String path) {
        // If the path does not exist, add it
        // Then give back the picture you just added
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

    /**
     * Helper method to determine if the path already exists in the database.
     * @param path The path to search for
     * @return boolean True if path exists, else false.
     */
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

    /**
     * Get a count of the picture table
     * @return int The number of rows in the Picture table
     */
    public int getPicturesCount() {
        return genericGetCount(PICTURE);
    }

    /*************************************************************************/

    // -- Update

    /**
     * Updates a picture
     * @param obj The picture to update
     * @return boolean true if successful, else false.
     */
    public boolean updatePicture(Picture obj) {
        return genericUpdate(PICTURE, getContentValues(obj), obj.getId());
    }

    /*************************************************************************/

    // -- Delete

    /**
     * Delete a picture
     * @param id The id of the picture to delete
     * @return boolean true if successful, else false
     */
    public boolean deletePicture(long id) {
        return genericDelete(PICTURE, id);
    }

    /*************************************************************************/
    /*************************************************************************/

    //=======================
    //  SPECIES
    //=======================

    // -- Create

    /**
     * Insert a species
     * @param obj The species to insert
     * @return long The inserted id.
     */
    public long insertSpecies(Species obj) {
        return genericInsert(SPECIES, obj);
    }

    /**
     * Insert a list of species
     * @param objects the list of species
     * @return boolean True if successful, else false
     */
    public boolean insertSpecies(List<Species> objects) {
        return genericInsertAll(SPECIES, objects);
    }

    /*************************************************************************/

    // -- Read

    /**
     * Get a specie
     * @param id The specie to get
     * @return Species The species or null
     */
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
                        "homeworldId",
                        "people"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Species obj = null;

        if (cursor.moveToFirst()) {
            obj = new Species();

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
            obj.setPeople(cursor.getString(13));
        }

        cursor.close();
        db.close();

        return obj;
    }

    /**
     * Get all species
     * @return List a list of species.
     */
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
                        "homeworldId",
                        "people"
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
                obj.setPeople(cursor.getString(13));

                species.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return species;
    }

    /**
     * Get a count of the Species table
     * @return The number of rows in the Species table.
     */
    public int getSpeciesCount() {
        return genericGetCount(SPECIES);
    }

    /*************************************************************************/

    // -- Update

    /**
     * Update a specie
     * @param obj The specie to update
     * @return boolean True if successful, else false
     */
    public boolean updateSpecies(Species obj) {
        return genericUpdate(SPECIES, getContentValues(obj), obj.getId());
    }

    /*************************************************************************/

    // -- Delete

    /**
     * Delete a specie
     * @param id The id to delete
     * @return boolean True if succesful, else false
     */
    public boolean deleteSpecies(long id) {
        return genericDelete(SPECIES, id);
    }

    /**
     * Delete all species (GENOCIDE)
     * @return boolean True if successful, else false
     */
    public boolean deleteAllSpecies() {
        return genericDeleteAll(SPECIES);
    }

    /*************************************************************************/
    /*************************************************************************/

    //=======================
    //  PEOPLE
    //=======================

    // -- Create

    /**
     * Insert a person
     * @param obj The person to insert
     * @return long The inserted id
     */
    public long insertPeople(People obj) {
        return genericInsert(PEOPLE, obj);
    }

    /**
     * Insert a list of people
     * @param objects The list of people
     * @return boolean True if successful, else false
     */
    public boolean insertPeoples(List<People> objects) {
        return genericInsertAll(PEOPLE, objects);
    }

    /*************************************************************************/

    // -- Read

    /**
     * Get a person
     * @param id The id
     * @return People The person or null
     */
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
                        "homeworldId",
                        "species"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        People obj = null;

        if (cursor.moveToFirst()) {
            obj = new People();

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
            obj.setSpecies(cursor.getString(12));
        }

        cursor.close();
        db.close();

        return obj;
    }

    /**
     * Get all people
     * @return List A list of all people in the database
     */
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
                        "homeworldId",
                        "species"
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
                obj.setSpecies(cursor.getString(12));

                people.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return people;
    }

    /**
     * Get all people
     * @return List A list of all people in the database
     */
    public List<People> getAllPeopleExcept(List<Member> members) {
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT id, name, birthYear, eyeColor, gender, hairColor, height, ")
                .append("mass, skinColor, url, edited, homeworldId, species")
                .append(" FROM ").append(PEOPLE)
                .append(" WHERE id NOT IN (");

        String prefix = "";
        for (Member member : members) {
            sb.append(prefix).append(member.getPeopleId());
            prefix = ", ";
        }

        sb.append(") ORDER BY name ASC;");

        Cursor cursor = db.rawQuery(sb.toString(), null);

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
                obj.setSpecies(cursor.getString(12));

                people.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return people;
    }

    /**
     * Get a count of all the people
     * @return int Number of rows in the People table
     */
    public int getPeopleCount() {
        return genericGetCount(PEOPLE);
    }

    /*************************************************************************/

    // -- Update

    /**
     * Update a person
     * @param obj The person to update
     * @return boolean True if successful, else false
     */
    public boolean updatePeople(People obj) {
        return genericUpdate(PEOPLE, getContentValues(obj), obj.getId());
    }

    /*************************************************************************/

    // -- Delete

    /**
     * Delete a person
     * @param id The id to delete
     * @return boolean True if successful, else false
     */
    public boolean deletePeople(long id) {
        return genericDelete(PEOPLE, id);
    }

    /**
     * Delete all people
     * @return boolean True if successful, else false
     */
    public boolean deleteAllPeople() {
        return genericDeleteAll(PEOPLE);
    }

    /*************************************************************************/
    /*************************************************************************/

    //=======================
    //  PERSONSPECIES
    //=======================

    // -- Create

    /**
     * Insert a personspecie
     * @param obj The personspecie
     * @return long the inserted id
     */
    public long insertPersonSpecies(PersonSpecies obj) {
        return genericInsert(PERSONSPECIES, obj);
    }

    /*************************************************************************/

    // -- Read

    /**
     * Get a personspecie
     * @param id The id to get
     * @return PersonSpecie the personspecie or null
     */
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

        PersonSpecies obj = null;

        if (cursor.moveToFirst()) {
            obj = new PersonSpecies();

            obj.setId(cursor.getLong(0));
            obj.setPeopleId(cursor.getLong(1));
            obj.setSpeciesId(cursor.getLong(2));
        }

        cursor.close();
        db.close();

        return obj;
    }

    /**
     * Get a count
     * @return int The number of rows in the PersonSpecies table
     */
    public int getPersonSpeciesCount() {
        return genericGetCount(PERSONSPECIES);
    }

    /*************************************************************************/

    // -- Update

    /**
     * Update a personspecie
     * @param obj the personspecie
     * @return boolean true if successful, else false
     */
    public boolean updatePersonSpecies(PersonSpecies obj) {
        return genericUpdate(PERSONSPECIES, getContentValues(obj), obj.getId());
    }

    /*************************************************************************/

    // -- Delete

    /**
     * Delete a personspecie
     * @param id the id to delete
     * @return boolean true id successful, else false
     */
    public boolean deletePersonSpecies(long id) {
        return genericDelete(PERSONSPECIES, id);
    }

    /*************************************************************************/
    /*************************************************************************/

    //=======================
    //  MEMBER
    //=======================

    // -- Create

    /**
     * Insert a member
     * @param obj The member to insert
     * @return long The inserted id
     */
    public long insertMember(Member obj) {
        return genericInsert(MEMBER, obj);
    }

    /*************************************************************************/

    // -- Read

    /**
     * Get a member
     * @param id The member id
     * @return Member the member or null
     */
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
                        "pictureId",
                        "base_speed",
                        "base_attack",
                        "base_defense",
                        "iv_speed",
                        "iv_attack",
                        "iv_defense",
                        "ev_speed",
                        "ev_attack",
                        "ev_defense",
                        "base_hp",
                        "iv_hp",
                        "ev_hp"
                },
                "id = ?",                               // Where
                new String[] { String.valueOf(id) },    // Where-params
                null,                                   // Group By
                null,                                   // Having
                null,                                   // Sorting
                null                                    // Dunno
        );

        Member obj = null;

        if (cursor.moveToFirst()) {
            obj = new Member();

            obj.setId(cursor.getLong(0));
            obj.setSpeed(cursor.getInt(1));
            obj.setAttack(cursor.getInt(2));
            obj.setDefense(cursor.getInt(3));
            obj.setExperience(cursor.getInt(4));
            obj.setExpToLevel(cursor.getInt(5));
            obj.setLevel(cursor.getInt(6));
            obj.setHealthPoints(cursor.getInt(7));
            obj.setSystemEntity(cursor.getInt(8) == 1);
            obj.setTeamId(cursor.getLong(9));
            obj.setPeopleId(cursor.getLong(10));
            obj.setPictureId(cursor.getLong(11));

            obj.setBase_speed(cursor.getInt(12));
            obj.setBase_attack(cursor.getInt(13));
            obj.setBase_defense(cursor.getInt(14));

            obj.setIv_speed(cursor.getInt(15));
            obj.setIv_attack(cursor.getInt(16));
            obj.setIv_defense(cursor.getInt(17));

            obj.setEv_speed(cursor.getInt(18));
            obj.setEv_attack(cursor.getInt(19));
            obj.setEv_defense(cursor.getInt(20));

            obj.setBase_hp(cursor.getInt(21));
            obj.setIv_hp(cursor.getInt(22));
            obj.setEv_hp(cursor.getInt(23));

            obj.setPerson(getPeople(obj.getPeopleId()));
            obj.setTeam(getTeam(obj.getTeamId()));
            obj.setPicture(getPicture(obj.getPictureId()));
        }

        cursor.close();
        db.close();

        return obj;
    }

    /**
     * Get a list of members of the team
     * @param teamId The team id
     * @return List A list of members belonging to that team
     */
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
                        "pictureId",
                        "base_speed",
                        "base_attack",
                        "base_defense",
                        "iv_speed",
                        "iv_attack",
                        "iv_defense",
                        "ev_speed",
                        "ev_attack",
                        "ev_defense",
                        "base_hp",
                        "iv_hp",
                        "ev_hp"
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
                obj.setSystemEntity(cursor.getInt(8) == 1);
                obj.setTeamId(cursor.getLong(9));
                obj.setPeopleId(cursor.getLong(10));
                obj.setPictureId(cursor.getLong(11));

                obj.setBase_speed(cursor.getInt(12));
                obj.setBase_attack(cursor.getInt(13));
                obj.setBase_defense(cursor.getInt(14));

                obj.setIv_speed(cursor.getInt(15));
                obj.setIv_attack(cursor.getInt(16));
                obj.setIv_defense(cursor.getInt(17));

                obj.setEv_speed(cursor.getInt(18));
                obj.setEv_attack(cursor.getInt(19));
                obj.setEv_defense(cursor.getInt(20));

                obj.setBase_hp(cursor.getInt(21));
                obj.setIv_hp(cursor.getInt(22));
                obj.setEv_hp(cursor.getInt(23));

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

    /**
     * Get all members
     * @param full Is it a full get or not (full get -> get team and people too)
     * @return List A list of members
     */
    public List<Member> getAllMembers(boolean full) {
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
                        "pictureId",
                        "base_speed",
                        "base_attack",
                        "base_defense",
                        "iv_speed",
                        "iv_attack",
                        "iv_defense",
                        "ev_speed",
                        "ev_attack",
                        "ev_defense",
                        "base_hp",
                        "iv_hp",
                        "ev_hp"
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
                obj.setSystemEntity(cursor.getInt(8) == 1);
                obj.setTeamId(cursor.getLong(9));
                obj.setPeopleId(cursor.getLong(10));
                obj.setPictureId(cursor.getLong(11));

                obj.setBase_speed(cursor.getInt(12));
                obj.setBase_attack(cursor.getInt(13));
                obj.setBase_defense(cursor.getInt(14));

                obj.setIv_speed(cursor.getInt(15));
                obj.setIv_attack(cursor.getInt(16));
                obj.setIv_defense(cursor.getInt(17));

                obj.setEv_speed(cursor.getInt(18));
                obj.setEv_attack(cursor.getInt(19));
                obj.setEv_defense(cursor.getInt(20));

                obj.setBase_hp(cursor.getInt(21));
                obj.setIv_hp(cursor.getInt(22));
                obj.setEv_hp(cursor.getInt(23));

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

    /**
     * Get a count of the members
     * @return int Number of rows in Member table
     */
    public int getMembersCount() {
        return genericGetCount(MEMBER);
    }

    /*************************************************************************/

    // -- Update

    /**
     * Update a member
     * @param obj The member to update
     * @return boolean True if successful, else false
     */
    public boolean updateMember(Member obj) {
        return genericUpdate(MEMBER, getContentValues(obj), obj.getId());
    }

    /*************************************************************************/

    // -- Delete

    /**
     * Delete a member
     * @param id The member id
     * @return boolean True if successful, else false
     */
    public boolean deleteMember(long id) {
        return genericDelete(MEMBER, id);
    }


    /*************************************************************************/
    /*************************************************************************/

    /**
     * Wanted to use it for the wiki, but the idea got scrapped
     * @return
     */
    public List<String> getAllNames() {

        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT name FROM ").append(PLANET);
        sb.append(" UNION ALL ");
        sb.append("SELECT name FROM ").append(SPECIES);
        sb.append(" UNION ALL ");
        sb.append("SELECT name FROM ").append(PEOPLE);

        Cursor cursor = db.rawQuery(sb.toString(), null);

        List<String> names = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return names;
    }
}
