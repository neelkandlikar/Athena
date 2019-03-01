package com.neelk.fbla2018;

import java.util.HashMap;

/*
 * Copyright © 2019 Neel Kandlikar. All rights reserved.
 */


public class Question {

    private String question;
    private String category;
    private char correctAnswer;
    private HashMap<Character, String> options;
    private boolean bonusQuestion;

    public Question(String question, String category, boolean bonusQuestion) {
        options = new HashMap<>();
        this.question = question;
        this.category = category;
        this.bonusQuestion = bonusQuestion;
    }

    public void addOption(char option, String answer) {
        options.put(option, answer);
    }

    public void setCorrectAnswer(char correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public char getCorrectAnswer() {
        return correctAnswer;
    }

    public HashMap<Character, String> getOptions() {
        return options;
    }

    public void setOptions(HashMap<Character, String> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "Question=" + question + ", " + "Options= " + options + ", " + "correctAnswer=" + correctAnswer + ", " + "isBonusQuestion=" + bonusQuestion;
    }

    public boolean isBonusQuestion() {
        return bonusQuestion;
    }
}
