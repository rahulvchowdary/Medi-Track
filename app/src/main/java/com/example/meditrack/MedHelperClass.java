package com.example.meditrack;

import java.util.ArrayList;

public class MedHelperClass {

    String medName;
    String medTimes;
    String medStock;
    ArrayList<String> medAlarms;

    public MedHelperClass() {
    }

    public MedHelperClass(String medName, String medTimes, String medStock, ArrayList<String> medAlarms) {
        this.medName = medName;
        this.medTimes = medTimes;
        this.medStock = medStock;
        this.medAlarms = medAlarms;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedTimes() {
        return medTimes;
    }

    public void setMedTimes(String medTimes) {
        this.medTimes = medTimes;
    }

    public String getMedStock() {
        return medStock;
    }

    public void setMedStock(String medStock) {
        this.medStock = medStock;
    }

    public ArrayList<String> getMedAlarms() {
        return medAlarms;
    }

    public void setMedAlarms(ArrayList<String> medAlarms) {
        this.medAlarms = medAlarms;
    }
}