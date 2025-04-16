package com.dvincisc349.ihatethisshit;

public class GoalItem {
    private String text;
    private boolean checked;
    private String challengeName;
    private int generationNumber;

    public GoalItem(String text, boolean checked, String challengeName, int generationNumber) {
        this.text = text;
        this.checked = checked;
        this.challengeName = challengeName;
        this.generationNumber = generationNumber;
    }

    public String getText() {
        return text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }
}
