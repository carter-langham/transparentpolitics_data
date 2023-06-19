package ellis;

import java.io.Serializable;

/**
 * Version 1.0 June-6-2023
 */
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;
    private int sessionId;
    private int stateId;
    private int yearStart;
    private int yearEnd;
    private int prefile;
    private int sineDie;
    private int prior;
    private int special;
    private String sessionTag;
    private String sessionTitle;
    private String sessionName;


    // getters and setters

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public int getYearStart() {
        return yearStart;
    }

    public void setYearStart(int yearStart) {
        this.yearStart = yearStart;
    }

    public int getYearEnd() {
        return yearEnd;
    }

    public void setYearEnd(int yearEnd) {
        this.yearEnd = yearEnd;
    }

    public int getPrefile() {
        return prefile;
    }

    public void setPrefile(int prefile) {
        this.prefile = prefile;
    }

    public int getSineDie() {
        return sineDie;
    }

    public void setSineDie(int sineDie) {
        this.sineDie = sineDie;
    }

    public int getPrior() {
        return prior;
    }

    public void setPrior(int prior) {
        this.prior = prior;
    }

    public int getSpecial() {
        return special;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public String getSessionTag() {
        return sessionTag;
    }

    public void setSessionTag(String sessionTag) {
        this.sessionTag = sessionTag;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
}

