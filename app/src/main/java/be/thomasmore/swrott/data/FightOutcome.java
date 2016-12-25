package be.thomasmore.swrott.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by koenv on 24-12-2016.
 */
public class FightOutcome implements Serializable {

    private long winner;
    private long loser;

    private int winnerScore;
    private int loserScore;

    private HashMap<Long, Integer> experience;

    private List<String> log;

    public long getWinner() {
        return winner;
    }

    public void setWinner(long winner) {
        this.winner = winner;
    }

    public long getLoser() {
        return loser;
    }

    public void setLoser(long loser) {
        this.loser = loser;
    }

    public int getWinnerScore() {
        return winnerScore;
    }

    public void setWinnerScore(int winnerScore) {
        this.winnerScore = winnerScore;
    }

    public int getLoserScore() {
        return loserScore;
    }

    public void setLoserScore(int loserScore) {
        this.loserScore = loserScore;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public HashMap<Long, Integer> getExperience() {
        return experience;
    }

    public void setExperience(HashMap<Long, Integer> experience) {
        this.experience = experience;
    }
}
