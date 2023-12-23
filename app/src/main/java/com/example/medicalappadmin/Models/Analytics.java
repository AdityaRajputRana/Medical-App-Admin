package com.example.medicalappadmin.Models;

public class Analytics {
    Analytic total;
    Analytic todaySoFar;

    public Analytic getTotal() {
        return total;
    }

    public void setTotal(Analytic total) {
        this.total = total;
    }

    public Analytic getTodaySoFar() {
        return todaySoFar;
    }

    public void setTodaySoFar(Analytic todaySoFar) {
        this.todaySoFar = todaySoFar;
    }

    public Analytics() {
    }
}
