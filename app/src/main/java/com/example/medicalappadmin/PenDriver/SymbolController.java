package com.example.medicalappadmin.PenDriver;

import android.util.Log;

import com.example.medicalappadmin.PenDriver.Models.Symbol;

import java.util.ArrayList;

public class SymbolController {
    private ArrayList<Symbol> symbols = getSymbols();

    private ArrayList<Symbol> getSymbols() {
        ArrayList<Symbol> s = new ArrayList<>();
//        s.add(new Symbol(0, "KEYPAD_0", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(1, "KEYPAD_1", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(2, "KEYPAD_2", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(3, "KEYPAD_3", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(4, "KEYPAD_4", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(5, "KEYPAD_5", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(6, "KEYPAD_6", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(7, "KEYPAD_7", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(8, "KEYPAD_8", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(9, "KEYPAD_9", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(10, "KEYPAD_DELETE", 50, 9, 62, 12.5f ));

//        s.add(new Symbol(21, "VOICE_START", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(22, "VOICE_STOP", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(23, "VOICE_SUBMIT", 50, 9, 62, 12.5f ));

//        s.add(new Symbol(31, "ATTACHMENT_PLAY_1", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(32, "ATTACHMENT_SHARE_2", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(33, "ATTACHMENT_PLAY_2", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(34, "ATTACHMENT_SHARE_2", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(38, "ATTACHMENT_PLAY_OTHERS", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(39, "ATTACHMENT_SHARE_OTHERS", 50, 9, 62, 12.5f ));

//        s.add(new Symbol(41, "ASSOCIATE_1", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(42, "ASSOCIATE_2", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(49, "ASSOCIATE_OTHERS", 50, 9, 62, 12.5f ));

        s.add(new Symbol(51, "GENDER_MALE", 56.5f, 22.2f, 60f, 24.8f ));
        s.add(new Symbol(52, "GENDER_FEMALE", 60f, 22.2f, 63.5f, 24.8f ));

//        s.add(new Symbol(100, "SUBMIT_CASE", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(101, "CLOSE_CASE", 50, 9, 62, 12.5f ));




        return s;
    }

    public Symbol getApplicableSymbol(float x, float y){
        for (Symbol s: symbols){
            if (s.isApplicable(x, y)){
                Log.i("PG_BTN_PRSS", s.getName());
                return s;
            }
        }

        return null;
    }
}
