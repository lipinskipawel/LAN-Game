package com.github.lipinskipawel.controller;

import java.util.Arrays;

final class DataObject {

    private String uniqIdentifier;
    private String questionFromModule;
    private String question;
    private String[] choices;
    private String[] answers;
    private long time;


    DataObject(final String uniqIdentifier,
               final String questionFromModule,
               final String question,
               final String[] choices,
               final String[] answers,
               final long time) {
        this.uniqIdentifier = uniqIdentifier;
        this.questionFromModule = questionFromModule;
        this.question = question;
        this.choices = Arrays.copyOf(choices, choices.length);
        this.answers = answers;
        this.time = time;
    }

    DataObject() {
    }

    public String getUniqIdentifier() {
        return uniqIdentifier;
    }

    public String getQuestionFromModule() {
        return questionFromModule;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getChoices() {
        return Arrays.copyOf(choices, choices.length);
    }

    public String[] getAnswer() {
        return Arrays.copyOf(answers, answers.length);
    }

    public long getTime() {
        return time;
    }
}
