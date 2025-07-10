/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package weather;

/**
 *
 * @author bensj
 */

public class Temp2M {
    private long max;
    private long min;

    public long getMax() {
        return max;
    }

    public void setMax(long value) {
        this.max = value;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long value) {
        this.min = value;
    }

    @Override
    public String toString() {
        return "Max: " + max + "°C, Min: " + min + "°C"; // Proper string representation of temperatures
    }
}


