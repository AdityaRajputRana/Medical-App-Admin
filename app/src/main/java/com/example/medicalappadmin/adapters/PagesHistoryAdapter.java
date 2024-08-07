package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.canvas.NotepadView;
import com.example.medicalappadmin.rest.response.ViewCaseRP;

import java.util.ArrayList;

public class PagesHistoryAdapter extends RecyclerView.Adapter<PagesHistoryAdapter.MyViewHolder> {
    ViewCaseRP viewCaseRP;
    Context context;
    PageListener listener;
    boolean keepHeightFixed = false;




    public PagesHistoryAdapter(ViewCaseRP viewCaseRP, Context context, PageListener listener) {
        this.viewCaseRP = viewCaseRP;
        this.context = context;
        this.listener = listener;
    }

    public PagesHistoryAdapter(ViewCaseRP viewCaseRP, Context context, PageListener listener, boolean keepHeightFixed) {
        this.viewCaseRP = viewCaseRP;
        this.context = context;
        this.listener = listener;
        this.keepHeightFixed = keepHeightFixed;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.pages_item;
        if (keepHeightFixed) layoutId = R.layout.pages_item_for_fixed_height;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.notepadView.clearDrawing(viewCaseRP.getPageNumbers().get(position), true, false);
        holder.pageCount.setText("Page " + viewCaseRP.getPageNumbers().get(position));
        holder.llPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPageClicked(viewCaseRP.getPages(),holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return viewCaseRP.getPageNumbers().size();
    }

    public interface PageListener {
        default void onPageClicked(ArrayList<Page> pages, int currentposition) {
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        NotepadView notepadView;
        TextView pageCount;
        LinearLayout llPage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.notepadView = itemView.findViewById(R.id.rcvNotepadView);
            this.pageCount = itemView.findViewById(R.id.tvPageCount);
            this.llPage = itemView.findViewById(R.id.llPage);
        }
    }
}
