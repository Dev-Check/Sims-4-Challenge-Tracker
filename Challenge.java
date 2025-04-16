package com.dvincisc349.ihatethisshit;

import java.util.List;

import java.util.List;

public class Challenge {
    private String name;
    private List<Generation> generations;

    // Getters and setters

    // Represents each generation in the challenge
    public static class Generation {
        private String aspiration;
        private List<String> career;
        private int generationNumber;
        private List<Goal> goals;
        private String title;
        private List<String> traits;


    }

    // Represents each goal (array of objects in the JSON)
    public static class Goal {
        private boolean completed;
        private String text;


    }
}
