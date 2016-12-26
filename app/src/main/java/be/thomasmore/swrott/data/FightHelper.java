package be.thomasmore.swrott.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import be.thomasmore.swrott.Helper;

/**
 * Created by koenv on 24-12-2016.
 */
public class FightHelper {

    public static Random _rand = new Random();

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
            stats = levelup(stats);
        }

        return stats;
    }

    public static Stats levelup(Stats stats) {
        stats.setLevel(stats.getLevel() + 1);
        stats.setSpeed(calculateSpeed(stats));
        stats.setAttack(calculateAttack(stats));
        stats.setDefense(calculateDefense(stats));
        stats.setHealthPoints(calculateHp(stats));
        stats.setExpToLevel(calculateExpToLevel(stats));

        return stats;
    }

    private static int calculateHp(Stats stats) {
        return calculateStat(
                stats.getBase_hp(),
                stats.getIv_hp(),
                stats.getEv_hp(),
                stats.getLevel()
        ) + 5 + stats.getLevel();
    }

    private static int calculateExpToLevel(Stats stats) {
        return stats.getLevel() * stats.getLevel() * stats.getLevel();
    }

    private static int calculateSpeed(Stats stats) {
        return calculateStat(
                stats.getBase_speed(),
                stats.getIv_speed(),
                stats.getEv_speed(),
                stats.getLevel()
        );
    }

    private static int calculateAttack(Stats stats) {
        return calculateStat(
                stats.getBase_attack(),
                stats.getIv_attack(),
                stats.getEv_attack(),
                stats.getLevel()
        );
    }

    private static int calculateDefense(Stats stats) {
        return calculateStat(
                stats.getBase_defense(),
                stats.getIv_defense(),
                stats.getEv_defense(),
                stats.getLevel()
        );
    }

    private static int calculateStat(int base, int iv, int ev, int level) {
        int basePlusIv = base + iv;
        int evSqrt4 = (int) (Math.sqrt(ev) / 4);
        int top = (basePlusIv * 2 + evSqrt4) * level;
        int result = top / 100;
        result += 5;
        return result;
    }

    public static Team getEnemy(final Team fullTeam, List<People> peopleList) {
        Team enemy = new Team();
        int avgOpponentLvl = fullTeam.getAverageLevel();
        int max = fullTeam.getMembers().size() + 2;

        enemy.setSystemEntity(true);

        if (max >= Helper.MAXMEMBERS)
            max = Helper.MAXMEMBERS;

        int numMembers = randomBetween(
                fullTeam.getMembers().size() - 2,
                max);

        if (numMembers <= 0)
            numMembers = randomBetween(1, 3);

        // Create random members for the team
        for (int i = 0; i < numMembers; i++) {
            Member member = new Member();
            member.setLevel(randomLevelFromAvg(avgOpponentLvl));
            member.setStats(getRandomStats(member.getLevel()));
            member.setSystemEntity(true);
            member.setPerson(peopleList.get(randomBetween(0, peopleList.size() - 1)));

            member.setTeam(enemy);
            enemy.getMembers().add(member);
        }

        enemy.setName(Helper.getRandomTeamName());

        // Probably don't need this
        // enemy.setPlanetId(1);

        return enemy;
    }

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

        int c_exp = calculateExpGained(
                randomBetween(0, 1) == 1,
                o_combatant.getLevel(),
                challenger.getMembers().size()
        );
        int o_exp = calculateExpGained(
                randomBetween(0, 1) == 1,
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
                            randomBetween(0, 1) == 1,
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
                            randomBetween(0, 1) == 1,
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
                            randomBetween(0, 1) == 1,
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
                            randomBetween(0, 1) == 1,
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

        // Reset the healthpoints for all the members
        for (Member m : challenger.getMembers())
            m.setHealthPoints(calculateHp(m.getStats()));

        for (Member m : opponent.getMembers())
            m.setHealthPoints(calculateHp(m.getStats()));

        return result;
    }

    private static HashMap<Long, Integer> addToExpMap(HashMap<Long, Integer> map, long key, int exp) {
        if (map.containsKey(key))
            map.put(key, map.get(key) + exp);
        else
            map.put(key, exp);

        return map;
    }

    private static int calculateExpGained(boolean isWild, int loserLevel, int numMembers) {
        // 1 if wild, 1.5 if owned by trainer
        // We just randomize it
        double a = isWild ? 1 : 1.5;

        // Base experience yield per species
        // We don't have it, so just slap a value on it
        int b = randomBetween(40, 350);

        // Level of fainted enemy
        int L = loserLevel;

        // Number of participants in team
        int s = numMembers * 2;

        return (int) Math.floor((a * b * L) / (7 * s));
    }

    private static int getDamage(Member attacker, Member defender) {
        int damage = 0;
        double mod = getModifier(attacker);
        // Normally, this is based on the type of attack is being done,
        // Since we don't do it like this, set base damage to some multiple of 5
        int base = 50;

        damage = (int) Math.floor( (
                    ((2 * attacker.getLevel() + 10) / 250.0) *
                    (attacker.getAttack() / defender.getDefense()) *
                    base + 2
                ) * mod);

        return damage;
    }

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

    private static boolean isCrit(Member attacker) {
        int value = randomBetween(0, 255);
        int threshold = attacker.getSpeed() / 2;
        return value < threshold;
    }

    private static boolean fighter1GoesFirst(Member fighter1, Member fighter2) {
        int f1Speed = randomBetween(0, fighter1.getSpeed());
        int f2Speed = randomBetween(0, fighter2.getSpeed());

        return f1Speed > f2Speed;
    }

    private static List<Member> shuffleMembers(Team team) {
        List<Member> list = new ArrayList<>(team.getMembers());
        Collections.shuffle(list);
        return list;
    }

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

    private static int randomLevelFromAvg(int avg) {
        int third = avg / 3;
        int result = randomBetween(avg - third, avg + third);

        if (result <= 0)
            result = 1;

        return result;
    }

    private static int randomBetween(int min, int max) {
        return _rand.nextInt((max - min) + 1) + min;
    }
}
