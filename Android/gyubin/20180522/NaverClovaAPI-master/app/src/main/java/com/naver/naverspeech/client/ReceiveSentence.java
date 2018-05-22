package com.naver.naverspeech.client;

/**
 * Created by student on 2018-01-19.
 */

public class ReceiveSentence {
    StringBuilder sentence;
    private String [] results;
    private  String RMSG;
    public ReceiveSentence(StringBuilder stringBuilder){
        this.sentence = stringBuilder;
    }
    public String ResendMSG(){
        results = (sentence.toString()).split(";");
        AnserFactor anserFactor = new AnserFactor(results[0]);
        RMSG = anserFactor.AnserFactortouser();
        return RMSG;
    }

}
