/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

/**
 *
 * @author Fritsch
 */
public class Setting {

    private String name;
    private int curValue;
    private final int maxValue;
    private final int minValue;

    public int getCurValue() {
        return curValue;
    }

    public String getName() {
        return name;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    
    public void setCurValue(int curValue) {
        this.curValue = Math.min(Math.max(curValue, minValue), maxValue);
    }

    public Setting(String name, int minValue, int maxValue, int curValue) {
        this.name = name;
        this.curValue = curValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }
    
    

}
