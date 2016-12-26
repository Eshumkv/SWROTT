package be.thomasmore.swrott.data;

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
        int speed = randomBetween(1, 2);
        if (hasPercentChance(15))
            speed = 3;

        int attack = randomBetween(4, 13);
        int defense = randomBetween(2, 7);

        Stats stats = new Stats();

        stats.setLevel(1);
        stats.setSpeed(speed);
        stats.setAttack(attack);
        stats.setDefense(defense);

        stats.setExperience(0);
        stats.setHealthPoints(calculateHp(stats));
        stats.setExpToLevel(calculateExpToLevel(stats));

        // Level up to specific level
        for (int i = 1; i < level; i++) {
            stats = levelup(stats);
        }

        return stats;
    }

    public static Stats levelup(Stats stats) {
        int level = stats.getLevel();
        stats.setLevel(level + 1);
        stats.setSpeed(stats.getSpeed() + randomBetween(1, level));
        stats.setAttack(stats.getAttack() + randomBetween(1, level));
        stats.setDefense(stats.getDefense() + randomBetween(1, level));

        stats.setHealthPoints(calculateHp(stats));
        stats.setExpToLevel(calculateExpToLevel(stats));

        return stats;
    }

    private static int calculateHp(Stats stats) {
        int mod = Math.max(1, (int)Math.floor(stats.getLevel() * 0.25));
        int hp = stats.getDefense() * 4 * mod;

        return hp;
    }

    private static int calculateExpToLevel(Stats stats) {
        if (stats.getLevel() == 1)
            return randomBetween(8, 20);

        return stats.getExpToLevel() * stats.getLevel();
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

        List<String> log = new ArrayList<>();
        List<FightOutcomeDeath> deaths = new ArrayList<>();
        HashMap<Long, Integer> exp = new HashMap<>();

        List<Member> c_members = sortBySpeed(challenger);
        List<Member> o_members = sortBySpeed(opponent);

        int c_index = 0;
        int o_index = 0;

        Member c_combatant = c_members.get(c_index);
        Member o_combatant = o_members.get(o_index);

        int c_score = 0;
        int o_score = 0;

        boolean cIsWinner = true;

        while(true) {
            // Let the two fight
            int c_damage = getDamage(c_combatant, o_combatant);
            int o_damage = getDamage(o_combatant, c_combatant);

            if (fighter1GoesFirst(c_combatant, o_combatant)) {
                o_combatant.setHealthPoints(o_combatant.getHealthPoints() - c_damage);
                exp = addToExpMap(exp, o_combatant.getId());
                log.add(String.format(
                        "%s (%s) -%dHP",
                        o_combatant.getPerson().getName(),
                        opponent.getName(),
                        c_damage
                ));

                if (o_combatant.getHealthPoints() <= 0) {
                    o_index++;
                    c_score++;
                    deaths.add(new FightOutcomeDeath(o_combatant.getId(), opponent.getId()));

                    if (o_index >= o_members.size()) {
                        // Challenger is the winner
                        log.add(String.format("%s WIN", challenger.getName()));
                        cIsWinner = true;
                        break;
                    }
                    o_combatant = o_members.get(o_index);
                }

                c_combatant.setHealthPoints(c_combatant.getHealthPoints() - o_damage);
                exp = addToExpMap(exp, c_combatant.getId());
                log.add(String.format(
                        "%s (%s) -%dHP",
                        c_combatant.getPerson().getName(),
                        challenger.getName(),
                        o_damage
                ));

                if (c_combatant.getHealthPoints() <= 0) {
                    c_index++;
                    o_score++;
                    deaths.add(new FightOutcomeDeath(c_combatant.getId(), challenger.getId()));

                    if (c_index >= c_members.size()) {
                        // Opponent is the winner
                        log.add(String.format("%s WIN", opponent.getName()));
                        cIsWinner = false;
                        break;
                    }
                    c_combatant = c_members.get(c_index);
                }
            } else {
                c_combatant.setHealthPoints(c_combatant.getHealthPoints() - o_damage);
                exp = addToExpMap(exp, c_combatant.getId());
                log.add(String.format(
                        "%s (%s) -%dHP",
                        c_combatant.getPerson().getName(),
                        challenger.getName(),
                        o_damage
                ));

                if (c_combatant.getHealthPoints() <= 0) {
                    c_index++;
                    o_score++;
                    deaths.add(new FightOutcomeDeath(c_combatant.getId(), challenger.getId()));

                    if (c_index >= c_members.size()) {
                        // Opponent is the winner
                        log.add(String.format("%s WIN", opponent.getName()));
                        cIsWinner = false;
                        break;
                    }
                    c_combatant = c_members.get(c_index);
                }

                o_combatant.setHealthPoints(o_combatant.getHealthPoints() - c_damage);
                exp = addToExpMap(exp, o_combatant.getId());
                log.add(String.format(
                        "%s (%s) -%dHP",
                        o_combatant.getPerson().getName(),
                        opponent.getName(),
                        c_damage
                ));

                if (o_combatant.getHealthPoints() <= 0) {
                    o_index++;
                    c_score++;
                    deaths.add(new FightOutcomeDeath(o_combatant.getId(), opponent.getId()));

                    if (o_index >= o_members.size()) {
                        // Challenger is the winner
                        log.add(String.format("%s WIN", challenger.getName()));
                        cIsWinner = true;
                        break;
                    }
                    o_combatant = o_members.get(o_index);
                }
            }
        }

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

    private static HashMap<Long, Integer> addToExpMap(HashMap<Long, Integer> map, long key) {
        if (map.containsKey(key))
            map.put(key, map.get(key) + 1);
        else
            map.put(key, 1);

        return map;
    }

    private static int getDamage(Member attacker, Member defender) {
        double mod = 1;
        int chanceToCrit = 10;

        // Attacker is higher level, so he has more chance to crit
        // Crit chance: 30%
        // Attacker is _LOWER_ level, so less chance
        // Crit chance: 5%
        if (attacker.getLevel() > defender.getLevel()) {
            chanceToCrit = 30;
        } else if (attacker.getLevel() < defender.getLevel()) {
            chanceToCrit = 5;
        }

        // Is it a crit?
        if (hasPercentChance(chanceToCrit))
            mod += 0.5;

        int attackRoll = randomBetween(0, attacker.getAttack());
        int defenseRoll = randomBetween(0, defender.getDefense());

        int baseDamage = attacker.getAttack() / 2;
        int chanceToMiss = 25;

        // Is it a glancing blow or a miss?
        if (attackRoll < defenseRoll) {
            if (hasPercentChance(chanceToMiss)) {
                // miss
                return 0;
            } else {
                // Glancing blow
                baseDamage = attacker.getAttack() / 4;
            }
        } else if (attackRoll == defenseRoll) {
            if (hasPercentChance(50))
                baseDamage = attacker.getAttack() / 2;
            else {
                if (hasPercentChance(chanceToMiss)) {
                    // miss
                    return 0;
                } else {
                    // Glancing blow
                    baseDamage = attacker.getAttack() / 4;
                }
            }
        }

        int damage = baseDamage +
                Math.max(attacker.getAttack() - defender.getDefense(), 0);
        damage *= mod;

        return damage;
    }

    private static boolean fighter1GoesFirst(Member fighter1, Member fighter2) {
        int f1Speed = randomBetween(0, fighter1.getSpeed());
        int f2Speed = randomBetween(0, fighter2.getSpeed());

        return f1Speed > f2Speed;
    }

    private static List<Member> sortBySpeed(Team team) {
        List<Member> list = new ArrayList<>(team.getMembers());

        Collections.sort(list, new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o2.getSpeed() - o1.getSpeed();
            }
        });

        return list;
    }

    private static int randomLevelFromAvg(int avg) {
        int third = avg / 3;
        int result = randomBetween(avg - third, avg + third);

        if (result <= 0)
            result = 1;

        return result;
    }

    private static boolean hasPercentChance(int percent) {
        return randomBetween(0, 100) >= (100 - percent);
    }

    private static int randomBetween(int min, int max) {
        return _rand.nextInt((max - min) + 1) + min;
    }
}
