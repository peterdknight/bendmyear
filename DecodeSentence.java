package com.example.cisentences;

public class DecodeSentence {
    public static String getWave(String sentence){
        String waveName;
        int dotIndex;
        dotIndex = sentence.indexOf(".");
        waveName = sentence.substring(0, dotIndex  );
        return waveName;
    }
    public static String getSentence(String sentence){
        int spaces, firstSpace,endSpaces;
        String speechSentence,waveName,finalSentence;
        firstSpace = sentence.indexOf(" ");
        speechSentence = sentence.substring(firstSpace);
        //   System.out.println("speech part =" + speechSentence);

        endSpaces= speechSentence.indexOf(".");
      //  if (endSpaces != -1) {
            //  System.out.println("  . index = " + endSpaces);
         //   finalSentence = speechSentence.substring(1, endSpaces+1);
      //  }
       // else {
            finalSentence = speechSentence;
            return finalSentence;
    }
/*
public static void main(String[] args) {
String sentence = new String("TA333.WAV this is the sentence.");
DecodeSentence decode = new DecodeSentence();
String wave,playSentence;
wave = decode.getWave(sentence);
playSentence = decode.getSentence(sentence);
    System.out.println("wave = " + wave);
    System.out.println("sentence = " + playSentence);
}*/
}

