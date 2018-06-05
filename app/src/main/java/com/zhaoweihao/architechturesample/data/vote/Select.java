package com.zhaoweihao.architechturesample.data.vote;

import java.io.Serializable;

public class Select implements Serializable{

    private Integer id;

    private String title;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;

    private Integer numA = 0;
    private Integer numB = 0;
    private Integer numC = 0;
    private Integer numD = 0;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }
}
