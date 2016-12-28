package be.thomasmore.rott.data;

import java.io.Serializable;

/**
 * Created by koenv on 26-12-2016.
 */
public class FightOutcomeDeath implements Serializable {

    private long fighterId;
    private long teamId;

    public FightOutcomeDeath(long fighter, long team) {
        this.fighterId = fighter;
        this.teamId = team;
    }

    public long getFighterId() {
        return fighterId;
    }

    public void setFighterId(long fighterId) {
        this.fighterId = fighterId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
}
