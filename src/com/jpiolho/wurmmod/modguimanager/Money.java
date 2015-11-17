/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

/**
 *
 * @author JPiolho
 */
public class Money {
    private static final int COPPER = 100;
    private static final int SILVER = 10000;
    private static final int GOLD =   1000000;
    private int money;
    
    public Money() {
        this.money = 0;
    }
    
    public Money(int money) {
        this.money = money;
    }
    
    public int getMoney() {
        return this.money;
    }
    
    public int getIron() {
        return money % 100;
    }
    
    public int getCopper() {
        return Math.floorDiv(money - (getGold() * GOLD) - (getSilver() * SILVER),100);
    }
    
    public int getSilver() {
        return Math.floorDiv(money - (getGold() * GOLD),10000);
    }
    
    public int getGold() {
        return Math.floorDiv(money,1000000);
    }
    
    public void setMoney(int money) {
        this.money = money;
    }
    
    public void setMoney(int iron,int copper,int silver,int gold) {
        this.money = iron + (copper * COPPER) + (silver * SILVER) + (gold * GOLD);
    }
    
    public void setIron(int iron) {
        this.money = iron + (getCopper() * COPPER) + (getSilver() * SILVER) + (getGold() * GOLD);
    }
    
    public void setCopper(int copper) {
        this.money = getIron() + (copper * COPPER) + (getSilver() * SILVER) + (getGold() * GOLD);
    }
    
    public void setSilver(int silver) {
        this.money = getIron() + (getCopper() * COPPER) + (silver * SILVER) + (getGold() * GOLD);
    }
    
    public void setGold(int gold) {
        this.money = getIron() + (getCopper() * COPPER) + (getSilver() * SILVER) + (gold * GOLD);
    }
}
