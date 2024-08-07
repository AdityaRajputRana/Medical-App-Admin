package com.example.medicalappadmin.PenDriver;

import android.util.Log;
import android.widget.Toast;

import com.example.medicalappadmin.PenDriver.Models.PageConfig;
import com.example.medicalappadmin.PenDriver.Models.Symbol;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SymbolController {
    private ArrayList<Symbol> symbols = getSymbols();
    float refHeight = 128f;
    float refWidth = 90f;

    float pageHeight = 7014f;
    float pageWidth = 4962f;

    float scaleFactor = Math.min(refHeight/pageHeight, refWidth/pageWidth);

    private ArrayList<Symbol> getSymbols() {
        ArrayList<Symbol> s = new ArrayList<>();
        s.add(new Symbol(0, "KEYPAD_0", 65f, 51f, 80.65f, 55.5f ));
        s.add(new Symbol(1, "KEYPAD_1", 65f, 31f, 72f, 35.5f ));
        s.add(new Symbol(2, "KEYPAD_2", 73.5f, 31f, 80.65f, 35.5f ));
        s.add(new Symbol(3, "KEYPAD_3", 83.3f, 31f, 90f, 35.5f ));
        s.add(new Symbol(4, "KEYPAD_4", 65f, 37.6f, 72f, 41.7f ));
        s.add(new Symbol(5, "KEYPAD_5", 73.5f, 37.6f, 80.65f, 41.7f ));
        s.add(new Symbol(6, "KEYPAD_6", 83.3f, 37.6f, 90f, 41.7f ));
        s.add(new Symbol(7, "KEYPAD_7", 65f, 44.24f, 72f, 48.15f ));
        s.add(new Symbol(8, "KEYPAD_8", 73.5f, 44.24f, 80.65f, 48.15f ));
        s.add(new Symbol(9, "KEYPAD_9", 83.3f, 44.24f, 90f, 48.15f ));
        s.add(new Symbol(10, "KEYPAD_DELETE", 83.3f, 51f, 90f, 55.5f ));

        s.add(new Symbol(21, "VOICE_START", 65f, 65.8f, 72f, 70.7f ));
        s.add(new Symbol(22, "VOICE_STOP", 73.5f, 65.8f, 80.65f, 70.7f ));
        s.add(new Symbol(23, "VOICE_SUBMIT", 83.3f, 65.8f, 90f, 70.7f ));

        s.add(new Symbol(31, "ATTACHMENT_PLAY_1", 65, 77, 81, 81 ));
        s.add(new Symbol(32, "ATTACHMENT_SHARE_2", 82, 77, 89, 81 ));
        s.add(new Symbol(33, "ATTACHMENT_PLAY_2", 65, 82, 81, 86 ));
        s.add(new Symbol(34, "ATTACHMENT_SHARE_2", 82, 82, 89, 86 ));
        s.add(new Symbol(38, "ATTACHMENT_PLAY_OTHERS", 65, 87, 81, 91 ));
        s.add(new Symbol(39, "ATTACHMENT_SHARE_OTHERS", 82, 87, 89, 91 ));

//        s.add(new Symbol(41, "ASSOCIATE_1", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(42, "ASSOCIATE_2", 50, 9, 62, 12.5f ));
//        s.add(new Symbol(49, "ASSOCIATE_OTHERS", 50, 9, 62, 12.5f ));

        s.add(new Symbol(51, "GENDER_MALE", 36.1f, 28.8f, 40.7f, 31.4f ));
        s.add(new Symbol(52, "GENDER_FEMALE", 40.7f, 28.8f, 45.3f, 31.4f ));

        s.add(new Symbol(61, "ATTACH_CAMERA", 65f, 108.8f, 72f, 113.7f ));
        s.add(new Symbol(62, "ATTACH_GALLERY", 73.5f, 108.8f, 80.65f, 113.7f ));
        s.add(new Symbol(63, "ATTACH_DOCUMENT", 83.3f, 108.8f, 90f, 113.7f ));


        s.add(new Symbol(100, "SUBMIT_CASE", 65f, 123.84f, 82.17f, 127.19f ));
//        s.add(new Symbol(101, "CLOSE_CASE", 50, 9, 62, 12.5f ));

        s.add(new Symbol(101, "LINK_FROM", 43, 124, 46, 127 ));
        s.add(new Symbol(102, "LINK_PAGE", 47, 124, 50, 127 ));

        return s;
    }

    public void parseSymbolConfig(File file){
        try  {
            Gson gson = new Gson();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String json = br.readLine();
            PageConfig pageConfig = gson.fromJson(json, PageConfig.class);
            ArrayList<Symbol> parsedSymbols = new ArrayList<>();
            for (PageConfig.PageConfigSymbol s: pageConfig.symbols){
                parsedSymbols.add(new Symbol(s.id, s.name, s.bounds.xmin, s.bounds.ymin, s.bounds.xmax, s.bounds.ymax));
            }
            symbols = parsedSymbols;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Symbol getApplicableSymbol(float x, float y){
        for (Symbol s: symbols){
            if (s.isApplicable(x, y, scaleFactor)){
                Log.i("PG_BTN_PRSS", s.getName());
                return s;
            }
        }

        return null;
    }
}
