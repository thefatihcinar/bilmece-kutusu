package com.example.user.bilmece;

/**
 * Created by user on 12/8/2019.
 */

public class Bilmece {

    private String soru; // Question i.e. the Riddle
    private String yanit; // Answer of the Riddle

    public Bilmece(String incomingBilmece, String incomingAnswer){

        this.soru = incomingBilmece;
        this.yanit = incomingAnswer;

    }

    public Bilmece(){

    }


    public String getSoru(){
        return this.soru;
    }

    public String getYanit(){
        return this.yanit;
    }

    public void setSoru(String newSoru){

        this.soru = newSoru;
        return;
    }

    public void setYanit(String newYanit){
        this.yanit = newYanit;
    }
}
