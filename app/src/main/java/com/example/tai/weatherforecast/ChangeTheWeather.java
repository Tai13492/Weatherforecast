package com.example.tai.weatherforecast;

/**
 * Created by Tai on 11/19/2017.
 */

public class ChangeTheWeather {
    String id;
    public ChangeTheWeather(String id){
        this.id = id;
    }
    public String idToWeather(){
        String [] s = this.id.split("");
        String weather = null;
        switch(s[0]){
            case "2": weather = "thunderstorm"; break;
            case "3": weather = "rain"; break;
            case "5": weather = "rain"; break;
            case "6": weather = "snow"; break;
            case "7": weather = "misty"; break;
            case "8": if(s[2].equals("0")){
                weather = "clear"; break;
            }else{
                weather = "cloudy"; break;
            }
            case "9": if(s[1].equals("0")){
                weather = "extreme"; break;
            }else{
                weather = "misty"; break;
            }
            default: weather = "cloudy"; break;
        }
        return weather;
    }
}
