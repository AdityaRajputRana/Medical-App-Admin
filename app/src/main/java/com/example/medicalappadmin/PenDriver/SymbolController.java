package com.example.medicalappadmin.PenDriver;

import com.example.medicalappadmin.PenDriver.Models.Symbol;

import java.util.ArrayList;

public class SymbolController {
    private ArrayList<Symbol> symbols = getSymbols();

    private ArrayList<Symbol> getSymbols() {
        ArrayList<Symbol> s = new ArrayList<>();
        s.add(new Symbol(0, "Submit", 50, 9, 62, 12.5f ));
        return s;
    }

    public Symbol getApplicableSymbol(float x, float y){
        for (Symbol s: symbols){
            if (s.isApplicable(x, y)){
                return s;
            }
        }

        return null;
    }
}
