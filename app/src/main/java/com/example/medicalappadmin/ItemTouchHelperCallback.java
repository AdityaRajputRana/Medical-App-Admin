package com.example.medicalappadmin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.EmptyRP;


public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final RecyclerView recyclerView;
    private final CaseHistoryRP response;
    private final Context context;
    String TAG = "Merge helper";

    private int selectedToPosition = RecyclerView.NO_POSITION;

    private boolean isMerging = false;

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        Log.i(TAG, "Action State changed: " + String.valueOf(actionState));
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && selectedToPosition != RecyclerView.NO_POSITION) {
            mergeItems();
            selectedToPosition = RecyclerView.NO_POSITION;
        }
    }

    public ItemTouchHelperCallback(RecyclerView recyclerView, CaseHistoryRP response, Context context) {
        Log.i(TAG, "ItemTouchHelperCallback:  Initialised");
        this.recyclerView = recyclerView;
        this.response = response;
        this.context = context;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // Enable drag up and down
        int swipeFlags = 0; // Disable swipe
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    int selectedFromPosition = RecyclerView.NO_POSITION;

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.i(TAG, "on move called");

        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        if (shouldMergeItems(fromPosition, toPosition)) {
            selectedFromPosition = fromPosition;
            selectedToPosition = toPosition;
            return true;
        } else {
            selectedToPosition = RecyclerView.NO_POSITION;
            selectedFromPosition = RecyclerView.NO_POSITION;
        }

        Log.i(TAG, "on move false");

        return false;
    }

    private boolean shouldMergeItems(int fromPosition, int toPosition) {

        if (isMerging)
            return false;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }

        View fromView = layoutManager.findViewByPosition(fromPosition);
        View toView = layoutManager.findViewByPosition(toPosition);

        if (fromView == null || toView == null) {
            return false;
        }
        Rect fromBounds = new Rect();
        recyclerView.getLayoutManager().findViewByPosition(fromPosition).getGlobalVisibleRect(fromBounds);

        Rect toBounds = new Rect();
        recyclerView.getLayoutManager().findViewByPosition(toPosition).getGlobalVisibleRect(toBounds);
        boolean canMerge = Rect.intersects(fromBounds, toBounds);

        Log.i(TAG, "should merge called " + fromPosition + " to : " +  toPosition + ": "+ canMerge );

        return canMerge;
    }

    private void mergeItems() {

        int toPosition = selectedToPosition;
        int fromPosition = selectedFromPosition;
        Log.i(TAG, "on merge called " + fromPosition + " to : " +  toPosition);


        String fromId = response.getCases().get(fromPosition).get_id();
        String toId = response.getCases().get(toPosition).get_id();

        isMerging = true;

        APIMethods.mergeCasesHistory(context, fromId, toId, new APIResponseListener<EmptyRP>() {
            @Override
            public void success(EmptyRP res) {
                Log.i(TAG, "merge successful");

                response.getCases().get(toPosition).setPageCount(response.getCases().get(toPosition).getPageCount()
                        + response.getCases().get(fromPosition).getPageCount());
                recyclerView.getAdapter().notifyItemChanged(toPosition);
                Toast.makeText(context, "Merge successful", Toast.LENGTH_SHORT).show();

//                response.getCases().remove(fromPosition);
//                recyclerView.getAdapter().notifyItemRemoved(fromPosition);

                isMerging = false;
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
                Methods.showError((Activity) context,message,true);
                isMerging = false;
            }
        });


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Not used in this example, as we are focusing on drag-and-drop
    }
}