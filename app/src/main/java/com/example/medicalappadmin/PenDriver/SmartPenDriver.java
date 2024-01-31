package com.example.medicalappadmin.PenDriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.afpensdk.pen.DPenCtrl;
import com.afpensdk.pen.penmsg.IAFPenDotListener;
import com.afpensdk.pen.penmsg.IAFPenMsgListener;
import com.afpensdk.pen.penmsg.IAFPenOfflineDataListener;
import com.afpensdk.pen.penmsg.JsonTag;
import com.afpensdk.pen.penmsg.PenGripStyle;
import com.afpensdk.pen.penmsg.PenMsg;
import com.afpensdk.pen.penmsg.PenMsgType;
import com.afpensdk.structure.AFDot;
import com.afpensdk.structure.DotType;
import com.example.medicalappadmin.PenDriver.LiveData.DrawLiveDataBuffer;
import com.example.medicalappadmin.PenDriver.Models.NoteModel;
import com.example.medicalappadmin.PenDriver.Models.SmartPen;
import com.example.medicalappadmin.PenDriver.Models.Symbol;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



public class SmartPenDriver implements IAFPenMsgListener, IAFPenDotListener, IAFPenOfflineDataListener {

    private static SmartPenDriver smartPenDriver;
    private SmartPenListener smartPenListener;
    private AppCompatActivity activity;
    private SymbolController symbolController;
    private SmartPen selectedSmartPen;
    private ArrayList<SmartPen> smartPens = new ArrayList<>();
    private HashSet<String> searchedPens = new HashSet<>();


    private SharedPreferences mPref;





    public static void observeBuffer(@NonNull LifecycleOwner owner, @NonNull Observer<ArrayList<DrawLiveDataBuffer.DrawAction>> observer){
        DrawLiveDataBuffer.observeBuffer(owner, observer);
    }
    public static void removeObserver(Observer observer){
        DrawLiveDataBuffer.removeObserver(observer);
    }




    private static MutableLiveData<ArrayList<SmartPen>> smartPensLiveData = new MutableLiveData<>(new ArrayList<>());
    public static void observeSmartPens(@NonNull LifecycleOwner owner, @NonNull Observer<ArrayList<SmartPen>> observer){
        smartPensLiveData.observe(owner, observer);
    }

    public void getSmartPenList(ConnectionsHandler.PenConnectionsListener connectionsListener){

        if (smartPens != null){
            for (SmartPen smartPen: smartPens){
                connectionsListener.onSmartPen(smartPen);
            }
        }

        Context context = activity;
        try {
            iPenCtrl.btStartForPeripheralsList(context);
        } catch (Exception e){
            Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    boolean isOfflineDataAvailable = false;

    @Override
    public void onReceiveMessage(PenMsg penMsg) {
        Log.i("pen-msg-type", String.valueOf(penMsg.penMsgType));
        Log.i("pen-msg-body", new Gson().toJson(penMsg));
        switch ( penMsg.penMsgType ) {
            case PenMsgType.PEN_CUR_MEMOFFSET:
                try {
                    if (penMsg.getContentByJSONObject().getInt(JsonTag.INT_DOTS_MEMORY_OFFSET) != 0){
                        isOfflineDataAvailable = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;


            case PenMsgType.PEN_CONNECTION_SUCCESS:
                smartPenListener.message("Smart Pen", "Connected - " + iPenCtrl.getConnectedDevice());
                activity.getSharedPreferences("PEN_CONFIG", Context.MODE_PRIVATE)
                        .edit()
                        .putString("SavePenMac", selectedSmartPen.getMacAddress())
                        .putBoolean("isPenSaved", true)
                        .apply();
                selectedSmartPen.setConnected(true);
                smartPenListener.onConnection(true);

                iPenCtrl.requestOfflineDataInfo();

                break;

            case PenMsgType.PEN_DISCONNECTED:
                selectedSmartPen.setConnected(false);
                selectedSmartPen.setAuthorized(false);
                smartPenListener.disconnected();
                break;

            case PenMsgType.FIND_DEVICE:
                JSONObject obj = penMsg.getContentByJSONObject();

                try {
                    String macAddress = obj.getString(JsonTag.STRING_PEN_MAC_ADDRESS);
                    String penName;

                    if(obj.has(JsonTag.STRING_DEVICE_NAME))
                        penName = obj.getString(JsonTag.STRING_DEVICE_NAME);
                    else
                        penName = "NULL";

                    int rssi;
                    if(obj.has(JsonTag.STRING_DEVICE_RSSI))
                        rssi = obj.getInt(JsonTag.STRING_DEVICE_RSSI);
                    else
                        rssi = -100;


                    SmartPen smartPen = new SmartPen(rssi, penName, macAddress);
                    addSmartPenToList(smartPen);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void addSmartPenToList(SmartPen smartPen) {
        if (searchedPens.contains(smartPen.getMacAddress())){
            return;
        }
        searchedPens.add(smartPen.getMacAddress());
        smartPens.add(smartPen);
        smartPensLiveData.setValue(smartPens);
    }

    int prevActionType = -1;
    @Override
    public void onReceiveDot(AFDot dot) {
        Log.i("NPROJ- dot", new Gson().toJson(dot));
        int actionType = -1;
        if (DotType.isPenActionDown(dot.type))
            actionType = 1;
        else if (DotType.isPenActionUp(dot.type)) {
            actionType = 2;
            Symbol sm = symbolController.getApplicableSymbol(dot.X, dot.Y);
            if (sm != null){
                if (smartPenListener != null){
                    smartPenListener.onPaperButtonPress(sm.getId(), sm.getName());
                }
            }
        }

        if (prevActionType == DotType.PEN_ACTION_DOWN.getValue() && dot.type == prevActionType){
            actionType = 3;
        }

        prevActionType = dot.type;
        DrawLiveDataBuffer.insertAction(new DrawLiveDataBuffer.DrawAction(
                dot.X, dot.Y, dot.page, actionType, false
        ));
    }

    @Override
    public void onReceiveAngle(boolean b, PenGripStyle penGripStyle) {
        if (smartPenListener != null){
            smartPenListener.message("Angle", String.valueOf(penGripStyle) + " - " + String.valueOf(b));
        }
    }

    public enum CONNECT_MESSAGE{
        CONFIG_ERROR,
        CONFIG_SUCCESS,
        REQUESTING_PERMS
    }

    //NEO Variables
    private DPenCtrl iPenCtrl;


    private SmartPenDriver(AppCompatActivity activity){
        if (activity instanceof  SmartPenListener){
            smartPenListener = (SmartPenListener) activity;
        }
        this.activity = activity;
    }

    public static SmartPenDriver getInstance(AppCompatActivity activity){
        if (smartPenDriver == null) {
            smartPenDriver = new SmartPenDriver(activity);
        }
        PermissionsHelper.setLauncher(activity);
        return smartPenDriver;
    }

    public void setListener(SmartPenListener listener){
        smartPenListener = listener;
    }

    public CONNECT_MESSAGE initialize(){
        if (smartPenListener == null || activity == null){
            return CONNECT_MESSAGE.CONFIG_ERROR;
        }

        symbolController = new SymbolController();


        iPenCtrl = DPenCtrl.getInstance();
        iPenCtrl.setContext(activity);
        iPenCtrl.setListener(this);
        iPenCtrl.setDotListener(this);
        iPenCtrl.setOffLineDataListener(this);

        mPref = activity.getSharedPreferences("SMART_PEN_PREFS", Context.MODE_PRIVATE);

        if (PermissionsHelper.haveRequiredPermissions(activity)){
            return  CONNECT_MESSAGE.CONFIG_SUCCESS;
        } else {
            PermissionsHelper.requestPermission(activity, isPermitted -> {
                Log.i("Connections", "SmartPenListenerVal:"+String.valueOf(smartPenListener));
                    if (smartPenListener != null){
                        smartPenListener.onPermissionsResult(isPermitted);
                        if (!isPermitted){
                            smartPenListener.onPermissionsDenied();
                        }
                    }
            });
            return  CONNECT_MESSAGE.REQUESTING_PERMS;
        }



    }

    public void destroyConnection(){
//        iPenCtrl.unregisterBroadcastBTDuplicate();
        iPenCtrl.disconnect();
    }

    boolean connectToPenAfterPermission = false;

    public void connectToPen(SmartPen smartPen){
        if (iPenCtrl.getConnectedDevice() != null){
            smartPenListener.message("Event", "Already Connected");
        }
        if (!PermissionsHelper.haveRequiredPermissions(activity)){
            PermissionsHelper.requestPermission(activity, isPermitted -> {
                if (isPermitted){
                    connectToPen(smartPen);
                } else {
                    smartPenListener.onPermissionsDenied();
                }
            });
        }

        selectedSmartPen = smartPen;

        //NEO Code
        String mac_address = smartPen.getMacAddress();
        iPenCtrl.connect(smartPen.getMacAddress());
    }




    //--------------------OFFLINE DATA HANDLING SECTION-------------------------------
    public boolean isOfflineDataAvailable(){
        return iPenCtrl.getOfflineAvailableCnt() > 0 || isOfflineDataAvailable;
    }
    public void transferOfflineData(){
        if (!isOfflineDataAvailable() || iPenCtrl == null){
            return;
        }

        iPenCtrl.requestAllOfflineData();
    }
    @Override
    public void offlineDataDidReceivePenData(List<AFDot> list, JSONObject jsonObject) {
        //Todo: parse atonce as path for pages, buttons different
        for (AFDot dot: list){
            onReceiveDot(dot);
        }
        iPenCtrl.requestDeleteOfflineData();
        isOfflineDataAvailable = false;
    }
}
