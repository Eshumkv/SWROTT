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
        Stats stats = new Stats();

        stats.setSpeed(randomBetween(8, 20));
        stats.setAttack(randomBetween(8, 20));
        stats.setDefense(randomBetween(8, 20));

        stats.setHealthPoints(randomBetween(20, 40));
        stats.setExperience(0);
        stats.setExpToLevel(randomBetween(8, 20));
        stats.setLevel(level);

        return stats;
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
                log.add(String.format("%s -%dHP", o_combatant.getPerson().getName(), c_damage));

                if (o_combatant.getHealthPoints() <= 0) {
                    o_index++;
                    c_score++;

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
                log.add(String.format("%s -%dHP", c_combatant.getPerson().getName(), o_damage));

                if (c_combatant.getHealthPoints() <= 0) {
                    c_index++;
                    o_score++;

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
                log.add(String.format("%s -%dHP", c_combatant.getPerson().getName(), o_damage));

                if (c_combatant.getHealthPoints() <= 0) {
                    c_index++;
                    o_score++;

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
                log.add(String.format("%s -%dHP", o_combatant.getPerson().getName(), c_damage));

                if (o_combatant.getHealthPoints() <= 0) {
                    o_index++;
                    c_score++;

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

        if (cIsWinner) {
            result.setWinner(challenger.getId());
            result.setWinnerScore(c_score);

            result.setLoser(opponent.getId());
            result.setLoserScore(o_score);
        } else {
            result.setWinner(opponent.getId());
            result.setWinnerScore(o_score);

            result.setLoser(challenger.getId());
            result.setLoserScore(c_score);
        }

        return result;
    }

    private static HashMap<Long, Integer> addToExpMap(HashMap<Long, Integer> map, long key) {
        if (map.containsKey(key))
            map.put(key, map.get(key) + 1);
        else
            map.put(key, 1);

        return map;
    }

    private static int getDamage(Member fighter1, Member fighter2) {
        return 10;
    }

    private static boolean fighter1GoesFirst(Member fighter1, Member fighter2) {
        int f1Speed = getBaseSpeed(fighter1);
        int f2Speed = getBaseSpeed(fighter2);

        f1Speed += randomBetween(0, fighter1.getLevel());
        f2Speed += randomBetween(0, fighter2.getLevel());

        return f1Speed > f2Speed;
    }

    private static int getBaseSpeed(Member member) {
        return member.getSpeed();
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

    private static int randomBetween(int min, int max) {
        return _rand.nextInt((max - min) + 1) + min;
    }
}
