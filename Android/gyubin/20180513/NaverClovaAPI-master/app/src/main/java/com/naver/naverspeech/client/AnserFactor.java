package com.naver.naverspeech.client;

/**
 * Created by student on 2018-01-19.
 */

public class AnserFactor {
    String question;
    String Answer;

    public AnserFactor(String question){
        this.question = question;
    }
    public String AnserFactortouser(){
        if (question.contains("성재")){
            Answer="유성재2013301044";
        }
        else if (question.contains("건영")){
            Answer="김건영2013301055";
        }
        else if (question.contains("기혁")){
            Answer="박기혁01515511544";
        }
        else
        {
            Answer="찾는 사람이 없습니다. 이름을 말씀해주세요";
        }
        return Answer;
    }
}
