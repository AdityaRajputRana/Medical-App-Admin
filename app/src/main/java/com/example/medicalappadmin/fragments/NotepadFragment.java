package com.example.medicalappadmin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.medicalappadmin.R;
import com.example.medicalappadmin.canvas.NotepadView;

public class NotepadFragment extends Fragment {

    public NotepadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notepad, container, false);
        NotepadView paint = view.findViewById(R.id.notepadView);
        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                paint.addCoordinate(289,282);
//                paint.addCoordinate(289,283);
//                paint.addCoordinate(289,284);
//                paint.addCoordinate(289,285);
//                paint.addCoordinate(289,286);
//                paint.addCoordinate(289,287);
//                paint.addCoordinate(289,288);
//                paint.addCoordinate(289,289);
            }
        });
        return view;
    }
}