/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package weather;

/**
 *
 * @author bensj
 */

public class Datasery {
    private long date;
    private String weather;
    private Temp2M temp2m;
    private long wind10m_max;

    public long getDate() {
        return date;
    }

    public void setDate(long value) {
        this.date = value;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String value) {
        this.weather = value;
    }

    public Temp2M getTemp2M() {
        return temp2m;
    }

    public void setTemp2M(Temp2M value) {
        this.temp2m = value;
    }

    public long getWind10MMax() {
        return wind10m_max;
    }

    public void setWind10MMax(long value) {
        this.wind10m_max = value;
    }
}

    

