/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagefilter.model;

/**
 * Settings with X options
 * @author Fritsch
 */
public abstract class SettingWithXOptions extends Setting{

    
    public SettingWithXOptions(String name, int minValue, int maxValue, int curValue) {
        super(name, minValue, maxValue, curValue);
    }
    
    public abstract String[] getOptionNames();
}
