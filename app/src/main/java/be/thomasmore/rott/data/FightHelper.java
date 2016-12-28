package be.thomasmore.rott.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import be.thomasmore.rott.Helper;

/**
 * FightHelper implements helper methods for fighting.
 *
 * @author Koen Vanduffel
 * @version 1.0
 */
public class FightHelper {

    /**
     * A local static variable for use in this class.
     * We need some random numbers.
     */
    private static Random _rand = new Random();

    /**
     * Returns a {@link be.thomasmore.rott.data.Stats} that is correct for the
     * specified level.
     * @param level The level of the statistics you want
     * @return Stats This returns the stats
     */
    public static Stats getRandomStats(int level) {
        Stats stats = new Stats();

        stats.setIv_speed(randomBetween(0, 15));
        stats.setIv_attack(randomBetween(0, 15));
        stats.setIv_defense(randomBetween(0, 15));
        stats.setIv_hp(randomBetween(0, 15));

        stats.setBase_speed(randomBetween(0, 255));
        stats.setBase_attack(randomBetween(0, 255));
        stats.setBase_defense(randomBetween(0, 255));
        stats.setBase_hp(randomBetween(0, 255));

        stats.setEv_speed(randomBetween(0, 5));
        stats.setEv_attack(randomBetween(0, 5));
        stats.setEv_defense(randomBetween(0, 5));
        stats.setEv_hp(randomBetween(0, 5));

        stats.setLevel(0);
        stats.setExperience(0);

        // Level up to specific level
        // Run this at least once, so we get to calc our stats
        for (int i = 0; i < level; i++) {
            stats.setExperience(stats.getExpToLevel());
            stats = levelup(stats);
        }

        return stats;
    }

    /**
     * Calculate the stats when they level up.
     * @param stats The stats to level up
     * @return Stats The new stats after level up
     */
    public static Stats levelup(Stats stats) {
        // There is not enough exp to level up
        if (stats.getExperience() < stats.getExpToLevel()) {
            return stats;
        }

        stats.setLevel(stats.getLevel() + 1);
        stats.setSpeed(calculateSpeed(stats));
        stats.setAttack(calculateAttack(stats));
        stats.setDefense(calculateDefense(stats));
        stats.setHealthPoints(calculateHp(stats));
        stats.setExperience(stats.getExperience() - stats.getExpToLevel());
        stats.setExpToLevel(calculateExpToLevel(stats));

        return stats;
    }

    /**
     * Calculate the HP based on the stats given.
     * @param stats The stats used to calculate the HP
     * @return int The new HP
     */
    private static int calculateHp(Stats stats) {
        return calculateStat(
                stats.getBase_hp(),
                stats.getIv_hp(),
                stats.getEv_hp(),
                stats.getLevel()
        ) + 5 + stats.getLevel();
    }

    /**
     * Calculate the experience needed to level up based on the stats.
     * @param stats The stats used in the calculation
     * @return int The new experience needed to level up
     */
    private static int calculateExpToLevel(Stats stats) {
        double mod = 1.5;
        int lvl = stats.getLevel();
        return (int) ( Math.pow(lvl, 3) * mod );
    }

    /**
     * Calculate the speed based on stats.
     * @param stats The stats used in the calculation
     * @return int The new speed
     */
    private static int calculateSpeed(Stats stats) {
        return calculateStat(
                stats.getBase_speed(),
                stats.getIv_speed(),
                stats.getEv_speed(),
                stats.getLevel()
        );
    }

    /**
     * Calculate the attack based on stats.
     * @param stats The stats used in the calculation
     * @return int The new attack
     */
    private static int calculateAttack(Stats stats) {
        return calculateStat(
                stats.getBase_attack(),
                stats.getIv_attack(),
                stats.getEv_attack(),
                stats.getLevel()
        );
    }

    /**
     * Calculate the defense based on the stats
     * @param stats The stats used in the calculation
     * @return int The new defense
     */
    private static int calculateDefense(Stats stats) {
        return calculateStat(
                stats.getBase_defense(),
                stats.getIv_defense(),
                stats.getEv_defense(),
                stats.getLevel()
        );
    }

    /**
     * Helper function to calculate a statistic (speed, attack, defense)
     * @param base The value of the base stat (0-255)
     * @param iv The individual value (0-15)
     * @param ev The effort value (0-65535)
     * @param level The level (0-100)
     * @return int The calculated stat
     */
    private static int calculateStat(int base, int iv, int ev, int level) {
        int basePlusIv = base + iv;
        int evSqrt4 = (int) (Math.sqrt(ev) / 4);
        int top = (basePlusIv * 2 + evSqrt4) * level;
        int result = top / 100;
        result += 5;
        return result;
    }

    /**
     * Return a random enemy based on the given team
     * @param fullTeam The team against which this enemy would fight
     * @param peopleList A list of people to choose from
     * @return Team A team around the same strength of the given team
     */
    public static Team getEnemy(final Team fullTeam, List<People> peopleList) {
        Team enemy = new Team();

        // Let the app know it's a system entity, so is allowed to be removed
        enemy.setSystemEntity(true);

        // Has a 50% chance of using the same amount of members
        // Has a 40% chance of using less members
        // Has a 10% chance of using more members
        int chance = randomBetween(0, 100);
        int numMembers;

        if (chance <= 50) {
            numMembers = fullTeam.getMembers().size();
        } else if (chance <= 90) {
            // Can't have less than one member
            numMembers = Math.max(
                    fullTeam.getMembers().size() - randomBetween(1, 2),
                    1
            );
        } else {
            // Can't have more than the maximum number of members
            numMembers = Math.min(
                    fullTeam.getMembers().size() + randomBetween(1, 2),
                    Helper.MAXMEMBERS
            );
        }

        int avgOpponentLvl = fullTeam.getAverageLevel();

        enemy.setName(Helper.getRandomTeamName());

        // Create random members for the team
        for (int i = 0; i < numMembers; i++) {
            Member member = new Member();

            member.setLevel(randomLevelFromAvg(avgOpponentLvl));
            member.setStats(getRandomStats(member.getLevel()));
            member.setSystemEntity(true);
            member.setPerson(peopleList.get(_rand.nextInt(peopleList.size())));

            member.setTeam(enemy);
            enemy.getMembers().add(member);
        }

        return enemy;
    }

    /**
     * Simulate a battle between two teams. Be aware that these teams need to have
     * members with people attached to them!
     * @param challenger The first team
     * @param opponent The second team
     * @return FightOutcome The outcome of the fight. {@link be.thomasmore.rott.data.FightOutcome}
     */
    public static FightOutcome fight(final Team challenger, final Team opponent) {
        FightOutcome result = new FightOutcome();

        // If there's no members
        // Should NOT happen
        if (challenger.getMembers().size() == 0 || opponent.getMembers().size() == 0) {
            return null;
        }

        // Make some temp variables to hold all the info we need to give back
        List<String> log = new ArrayList<>();
        List<FightOutcomeDeath> deaths = new ArrayList<>();
        HashMap<Long, Integer> exp = new HashMap<>();

        // Set the initial score to 0
        int c_score = 0;
        int o_score = 0;

        // Shuffle the list and get the first fighter
        List<Member> c_members = sortByLevel(challenger);
        List<Member> o_members = sortByLevel(opponent);

        int c_index = 0;
        int o_index = 0;

        Member c_combatant = c_members.get(c_index);
        Member o_combatant = o_members.get(o_index);

        // do we gain more exp?
        boolean c_isWild = randomBetween(0, 4) == 0;
        boolean o_isWild = randomBetween(0, 4) == 0;

        int c_exp = calculateExpGained(
                o_isWild,
                o_combatant.getLevel(),
                challenger.getMembers().size()
        );
        int o_exp = calculateExpGained(
                c_isWild,
                c_combatant.getLevel(),
                opponent.getMembers().size()
        );

        boolean cIsWinner = true;
        int round = 1;

        log.add(String.format("%s VS %s", challenger.getName(), opponent.getName()));
        log.add(              "--------------");
        log.add(String.format("--- ROUND %d", round));
        log.add(String.format("--- %s (%s) VS %s (%s)",
                c_combatant.getPerson().getName(), challenger.getName(),
                o_combatant.getPerson().getName(), opponent.getName()));
        log.add(              "--------------");

        while(true) {
            // Calculate the damage
            int c_damage = getDamage(c_combatant, o_combatant);
            int o_damage = getDamage(o_combatant, c_combatant);

            // If challenger-team goes first
            if (fighter1GoesFirst(c_combatant, o_combatant)) {
                log.add(String.format("-> %s", c_combatant.getPerson().getName()));

                // Opponent takes damage,
                // challenger gets experience
                o_combatant.setHealthPoints(o_combatant.getHealthPoints() - c_damage);
                log.add(String.format(
                        "-%dHP %s (%d)",
                        c_damage,
                        o_combatant.getPerson().getName(),
                        o_combatant.getHealthPoints()
                ));

                // Did we kill the opponent?
                if (o_combatant.getHealthPoints() <= 0) {
                    o_index++;
                    c_score++;

                    deaths.add(new FightOutcomeDeath(o_combatant.getId(), opponent.getId()));
                    log.add(String.format("⚔ %s", o_combatant.getPerson().getName()));

                    // Give the current fighter some exp
                    exp = addToExpMap(exp, c_combatant.getId(), c_exp);

                    // Give the defeated one some exp
                    exp = addToExpMap(exp, o_combatant.getId(), o_exp / 3);

                    // There's no more opponents to fight
                    if (o_index >= o_members.size()) {
                        // Challenger is the winner
                        log.add(              "--------------");
                        log.add(              "--------------");
                        log.add(String.format("%s WIN", challenger.getName()));
                        cIsWinner = true;
                        break;
                    }

                    // Next opponent
                    o_combatant = o_members.get(o_index);

                    c_exp = calculateExpGained(
                            o_isWild,
                            o_combatant.getLevel(),
                            challenger.getMembers().size()
                    );

                    round++;
                    log.add(              "--------------");
                    log.add(String.format("--- ROUND %d", round));
                    log.add(String.format("--- %s (%s) VS %s (%s)",
                            c_combatant.getPerson().getName(), challenger.getName(),
                            o_combatant.getPerson().getName(), opponent.getName()));
                    log.add(              "--------------");
                }

                // Challenger takes damage
                // Opponent gets experience
                c_combatant.setHealthPoints(c_combatant.getHealthPoints() - o_damage);
                log.add(String.format(
                        "-%dHP %s (%d)",
                        o_damage,
                        c_combatant.getPerson().getName(),
                        c_combatant.getHealthPoints()
                ));

                // Did we kill the challenger?
                if (c_combatant.getHealthPoints() <= 0) {
                    c_index++;
                    o_score++;

                    deaths.add(new FightOutcomeDeath(c_combatant.getId(), challenger.getId()));
                    log.add(String.format("⚔ %s", c_combatant.getPerson().getName()));

                    // Give the current fighter some exp
                    exp = addToExpMap(exp, o_combatant.getId(), o_exp);

                    // Give the defeated one some exp
                    exp = addToExpMap(exp, c_combatant.getId(), o_exp / 3);

                    // There's no more opponents to fight
                    if (c_index >= c_members.size()) {
                        // Opponent is the winner
                        log.add(              "--------------");
                        log.add(              "--------------");
                        log.add(String.format("%s WIN", opponent.getName()));
                        cIsWinner = false;
                        break;
                    }

                    // Next challenger
                    c_combatant = c_members.get(c_index);
                    o_exp = calculateExpGained(
                            c_isWild,
                            c_combatant.getLevel(),
                            opponent.getMembers().size()
                    );

                    round++;
                    log.add(              "--------------");
                    log.add(String.format("--- ROUND %d", round));
                    log.add(String.format("--- %s (%s) VS %s (%s)",
                            c_combatant.getPerson().getName(), challenger.getName(),
                            o_combatant.getPerson().getName(), opponent.getName()));
                    log.add(              "--------------");
                }
            } else {
                // Fighter2 (Opponent) goes first
                log.add(String.format("-> %s", o_combatant.getPerson().getName()));

                // Challenger takes damage
                c_combatant.setHealthPoints(c_combatant.getHealthPoints() - o_damage);
                log.add(String.format(
                        "-%dHP %s (%d)",
                        o_damage,
                        c_combatant.getPerson().getName(),
                        c_combatant.getHealthPoints()
                ));

                if (c_combatant.getHealthPoints() <= 0) {
                    c_index++;
                    o_score++;

                    deaths.add(new FightOutcomeDeath(c_combatant.getId(), challenger.getId()));
                    log.add(String.format("⚔ %s", c_combatant.getPerson().getName()));

                    // Give the current fighter some exp
                    exp = addToExpMap(exp, o_combatant.getId(), o_exp);

                    // Give the defeated one some exp
                    exp = addToExpMap(exp, c_combatant.getId(), o_exp / 3);

                    if (c_index >= c_members.size()) {
                        // Opponent is the winner
                        log.add(              "--------------");
                        log.add(              "--------------");
                        log.add(String.format("%s WIN", opponent.getName()));
                        cIsWinner = false;
                        break;
                    }
                    c_combatant = c_members.get(c_index);
                    o_exp = calculateExpGained(
                            c_isWild,
                            c_combatant.getLevel(),
                            opponent.getMembers().size()
                    );

                    round++;
                    log.add(              "--------------");
                    log.add(String.format("--- ROUND %d", round));
                    log.add(String.format("--- %s (%s) VS %s (%s)",
                            c_combatant.getPerson().getName(), challenger.getName(),
                            o_combatant.getPerson().getName(), opponent.getName()));
                    log.add(              "--------------");
                }

                // Opponent takes damage
                o_combatant.setHealthPoints(o_combatant.getHealthPoints() - c_damage);
                log.add(String.format(
                        "-%dHP %s (%d)",
                        c_damage,
                        o_combatant.getPerson().getName(),
                        o_combatant.getHealthPoints()
                ));

                if (o_combatant.getHealthPoints() <= 0) {
                    o_index++;
                    c_score++;

                    deaths.add(new FightOutcomeDeath(o_combatant.getId(), opponent.getId()));
                    log.add(String.format("⚔ %s", o_combatant.getPerson().getName()));

                    // Give the current fighter some exp
                    exp = addToExpMap(exp, c_combatant.getId(), c_exp);

                    // Give the defeated one some exp
                    exp = addToExpMap(exp, o_combatant.getId(), o_exp / 3);

                    if (o_index >= o_members.size()) {
                        // Challenger is the winner
                        log.add(              "--------------");
                        log.add(              "--------------");
                        log.add(String.format("%s WIN", challenger.getName()));
                        cIsWinner = true;
                        break;
                    }
                    o_combatant = o_members.get(o_index);
                    c_exp = calculateExpGained(
                            o_isWild,
                            o_combatant.getLevel(),
                            challenger.getMembers().size()
                    );

                    round++;
                    log.add(              "--------------");
                    log.add(String.format("--- ROUND %d", round));
                    log.add(String.format("--- %s (%s) VS %s (%s)",
                            c_combatant.getPerson().getName(), challenger.getName(),
                            o_combatant.getPerson().getName(), opponent.getName()));
                    log.add(              "--------------");
                }
            }
        }

        // Fight is over, fill the return structure
        // Winner gets +3 points!
        result.setLog(log);
        result.setExperience(exp);
        result.setDeaths(deaths);

        if (cIsWinner) {
            c_score += 3;
            result.setWinner(challenger.getId());
            result.setWinnerScore(c_score);

            result.setLoser(opponent.getId());
            result.setLoserScore(o_score);
        } else {
            o_score += 3;
            result.setWinner(opponent.getId());
            result.setWinnerScore(o_score);

            result.setLoser(challenger.getId());
            result.setLoserScore(c_score);
        }

        // Reset the hp for all the members
        for (Member m : challenger.getMembers())
            m.setHealthPoints(calculateHp(m.getStats()));

        for (Member m : opponent.getMembers())
            m.setHealthPoints(calculateHp(m.getStats()));

        return result;
    }

    /**
     * Helper function to add experience to a map, where the memberId is the key.
     * @param map The map to add to
     * @param key The key (A member id)
     * @param exp The amount of experience to add
     * @return HasMap The map with the values added.
     */
    private static HashMap<Long, Integer> addToExpMap(HashMap<Long, Integer> map, long key, int exp) {
        if (map.containsKey(key))
            map.put(key, map.get(key) + exp);
        else
            map.put(key, exp);

        return map;
    }

    /**
     * Function to calculate the experience someone gained.
     * @param isWild if true, gain more experience
     * @param loserLevel The level of the one who lost
     * @param numMembers The amount of members the winner has in his team
     * @return int A value representing the amount of experience earned.
     */
    private static int calculateExpGained(boolean isWild, int loserLevel, int numMembers) {
        // 1 if wild, 1.5 if owned by trainer
        // We just randomize it
        double a = isWild ? 1 : 1.5;

        // Base experience yield per species
        // We don't have it, so just slap a value on it
        int b = randomBetween(30, 150);

        // Level of fainted enemy
        int L = loserLevel;

        // Number of participants in team
        int s = numMembers * 2;

        return (int) Math.floor((a * b * L) / (double)(7 * s));
    }

    /**
     * Damage calculations between two members.
     * @param attacker The one who does the attack.
     * @param defender The one who defends the attack
     * @return int The damage the attacker does to the defender
     */
    private static int getDamage(Member attacker, Member defender) {
        double mod = getModifier(attacker);
        // Normally, this is based on the type of attack is being done,
        // Since we don't do it like this, set base damage to some multiple of 5
        double base = 50.0;

        double part1 = (2 * attacker.getLevel() + 10) / 250.0;
        double part2 = attacker.getAttack() / (double) defender.getDefense();

        int damage = (int) Math.floor((part1 * part2 * base + 2) * mod);

        return damage;
    }

    /**
     * The modifier used in the damage calculation.
     * @param attacker The one who does the attack.
     * @return double The damage modifier.
     */
    private static double getModifier(Member attacker) {
        // Same-type attack bonus
        double stab = 1;

        // Type effectiveness of the attack
        double type = 1;

        // Is it a critical hit? If so, crit is 1.5, otherwise 1
        double critical = isCrit(attacker) ? 1.5 : 1;

        // Random number between 0.85 and 1.00
        double random = randomBetween(85, 100) / 100.0;

        return stab * type * critical * random;
    }

    /**
     * Is the attack a critical hit?
     * @param attacker The one who does the attack
     * @return boolean True if critical hit, else false.
     */
    private static boolean isCrit(Member attacker) {
        int value = randomBetween(0, 255);
        int threshold = attacker.getSpeed() / 2;
        return value < threshold;
    }

    /**
     * Helper method to determine if fighter 1 goes first.
     * @param fighter1 The first fighter
     * @param fighter2 The second fighter
     * @return boolean True if fighter1 goes first
     */
    private static boolean fighter1GoesFirst(Member fighter1, Member fighter2) {
        return fighter1.getSpeed() > fighter2.getSpeed();
    }

    /**
     * Randomly shuffle the members of a team.
     * @param team The team whose members need to be shuffled
     * @return List The shuffled list of members.
     */
    private static List<Member> shuffleMembers(Team team) {
        List<Member> list = new ArrayList<>(team.getMembers());
        Collections.shuffle(list);
        return list;
    }

    /**
     * Sort the members of the team by level ascending (1, 2, 3)
     * @param team The team whose members need to be sorted
     * @return List List of sorted members.
     */
    private static List<Member> sortByLevel(Team team) {
        List<Member> result = new ArrayList<>(team.getMembers());

        Collections.sort(result, new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getLevel() - o2.getLevel();
            }
        });

        return result;
    }

    /**
     * Helper level to generate a random level based on the average.
     * @param avg The average level used in the calculation.
     * @return int The random level.
     */
    private static int randomLevelFromAvg(int avg) {
        int third = avg / 3;
        int result = randomBetween(avg - third, avg + third);

        if (result <= 0)
            result = 1;

        return result;
    }

    /**
     * Helper method to generate a random integer between min and max. (inclusive)
     * @param min The minimum value (Inclusive)
     * @param max The maximum value (Inclusive)
     * @return int random value between min and max.
     */
    private static int randomBetween(int min, int max) {
        return _rand.nextInt((max - min) + 1) + min;
    }
}
