/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package weather;


public class WeatherResponse {
    private String product;
    private String init;
    private Datasery[] dataseries;

    public String getProduct() {
        return product;
    }

    public void setProduct(String value) {
        this.product = value;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String value) {
        this.init = value;
    }

    public Datasery[] getDataseries() {
        return dataseries;
    }

    public void setDataseries(Datasery[] value) {
        this.dataseries = value;
    }


}

    