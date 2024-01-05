package com.example.medicalappadmin.PenDriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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

import kr.neolab.sdk.ink.structure.Dot;
import kr.neolab.sdk.ink.structure.DotType;
import kr.neolab.sdk.ink.structure.Stroke;
import kr.neolab.sdk.metadata.IMetadataCtrl;
import kr.neolab.sdk.metadata.IMetadataListener;
import kr.neolab.sdk.metadata.MetadataCtrl;
import kr.neolab.sdk.pen.IPenCtrl;
import kr.neolab.sdk.pen.PenCtrl;
import kr.neolab.sdk.pen.bluetooth.BTLEAdt;
import kr.neolab.sdk.pen.bluetooth.lib.ProtocolNotSupportedException;
import kr.neolab.sdk.pen.penmsg.IOfflineDataListener;
import kr.neolab.sdk.pen.penmsg.IPenDotListener;
import kr.neolab.sdk.pen.penmsg.IPenMsgListener;
import kr.neolab.sdk.pen.penmsg.JsonTag;
import kr.neolab.sdk.pen.penmsg.PenMsg;
import kr.neolab.sdk.pen.penmsg.PenMsgType;
import kr.neolab.sdk.util.NLog;

public class SmartPenDriver implements IPenMsgListener, IPenDotListener {

    private static SmartPenDriver smartPenDriver;
    private SmartPenListener smartPenListener;
    private AppCompatActivity activity;
    private ConnectionsHandler connectionsHandler;
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

        ConnectionsHandler.PenConnectionsListener mConListener = new ConnectionsHandler.PenConnectionsListener() {
            @Override
            public void onSmartPens(ArrayList<SmartPen> pens) {
                smartPens = new ArrayList<>();
                smartPens.addAll(pens);
                smartPensLiveData.setValue(smartPens);
                if (connectionsListener != null) connectionsListener.onSmartPens(smartPens);
            }

            @Override
            public void onSmartPen(SmartPen smartPen) {
                Log.i("Connections", "onSmartPen Called");
                if (searchedPens.contains(smartPen.getMacAddress())){
                    return;
                }
                searchedPens.add(smartPen.getMacAddress());
                smartPens.add(smartPen);
                smartPensLiveData.setValue(smartPens);
                if (connectionsListener!= null) connectionsListener.onSmartPen(smartPen);
            }
        };


        connectionsHandler.getSmartPenList(activity, mConListener);
    }

    @Override
    public void onReceiveMessage(String s, PenMsg penMsg) {
        Log.i("pen-msg-type", String.valueOf(penMsg.penMsgType));
        Log.i("pen-msg-head", String.valueOf(s));
        Log.i("pen-msg-body", new Gson().toJson(penMsg));
        switch ( penMsg.penMsgType )
        {
            case PenMsgType.PEN_CONNECTION_SUCCESS:
                smartPenListener.message("Smart Pen", "Connected - "+iPenCtrl.getConnectingDevice() );
                activity.getSharedPreferences("PEN_CONFIG", Context.MODE_PRIVATE)
                        .edit()
                        .putString("SavePenMac", selectedSmartPen.getId())
                        .putBoolean("isPenSaved", true)
                        .apply();
                selectedSmartPen.setConnected(true);
                break;

            case PenMsgType.PEN_AUTHORIZED:
                smartPenListener.message("Smart Pen", "Authorized - "+iPenCtrl.getConnectedDevice() );
                selectedSmartPen.setAuthorized(true);

                JSONObject obj = penMsg.getContentByJSONObject();

                SharedPreferences.Editor edit = mPref.edit();

                try
                {
                    selectedSmartPen.setPassword(obj.getString( JsonTag.STRING_PEN_PASSWORD ));
                    String macaddress = obj.getString( JsonTag.STRING_PEN_MAC_ADDRESS);
                    edit.putString(Const.Setting.KEY_PASSWORD, selectedSmartPen.getPassword() );
                    edit.putString(Const.Setting.KEY_MAC_ADDRESS,  macaddress );
                    edit.commit();
                }
                catch ( JSONException e )
                {
                    e.printStackTrace();
                }


                iPenCtrl.reqAddUsingNoteAll(); //todo: later req for specific page sizes only
                iPenCtrl.reqOfflineDataList(); //todo: later req for specific page sizes only

                smartPenListener.onConnection(true);
                break;

            case PenMsgType.PEN_DISCONNECTED:
                selectedSmartPen.setConnected(false);
                selectedSmartPen.setAuthorized(false);
                smartPenListener.disconnected();
                break;

            case PenMsgType.PASSWORD_REQUEST:
            {
                JSONObject job = penMsg.getContentByJSONObject();
                try
                {
                    int count = job.getInt( Const.JsonTag.INT_PASSWORD_RETRY_COUNT );
                    smartPenListener.message("Message", "Smart pen requested password. retry count="+ String.valueOf(count));
                }
                catch ( JSONException e )
                {
                    e.printStackTrace();
                }
            }
            break;

            case PenMsgType.PEN_STATUS:
            {
                JSONObject job = penMsg.getContentByJSONObject();
                if ( job == null )
                {
                    return;
                }
                NLog.d( job.toString() );


                SharedPreferences.Editor editor = mPref.edit();
                try
                {
                    String stat_version = job.getString( Const.JsonTag.STRING_PROTOCOL_VERSION );

//					int stat_timezone = job.getInt( Const.JsonTag.INT_TIMEZONE_OFFSET );
                    long stat_timetick = job.getLong( Const.JsonTag.LONG_TIMETICK );
                    int stat_forcemax = job.getInt( Const.JsonTag.INT_MAX_FORCE );
                    int stat_battery = job.getInt( Const.JsonTag.INT_BATTERY_STATUS );
                    int stat_usedmem = job.getInt( Const.JsonTag.INT_MEMORY_STATUS );

//					int stat_pencolor = job.getInt( Const.JsonTag.INT_PEN_COLOR );

                    boolean stat_autopower = job.getBoolean( Const.JsonTag.BOOL_AUTO_POWER_ON );
                    boolean pencap_on =false;
                    try
                    {
                        pencap_on= job.getBoolean( Const.JsonTag.BOOL_PEN_CAP_ON );
                    }catch ( Exception e )
                    {

                    }
//					boolean stat_accel = job.getBoolean( Const.JsonTag.BOOL_ACCELERATION_SENSOR );
                    boolean stat_hovermode =false;
                    try
                    {
                        stat_hovermode= job.getBoolean( Const.JsonTag.BOOL_HOVER );
                    }catch ( Exception e )
                    {
                    }
                    boolean stat_offlinesave = false;
                    try
                    {
                        stat_offlinesave= job.getBoolean( Const.JsonTag.BOOL_OFFLINE_DATA_SAVE );
                    }catch ( Exception e )
                    {
                    }


                    boolean stat_beep = job.getBoolean( Const.JsonTag.BOOL_BEEP );

                    int stat_autopower_time = job.getInt( Const.JsonTag.INT_AUTO_POWER_OFF_TIME );
                    int stat_sensitivity = job.getInt( Const.JsonTag.INT_PEN_SENSITIVITY );

//					editor.putBoolean( Const.Setting.KEY_ACCELERATION_SENSOR, stat_accel );
                    editor.putString( Const.Setting.KEY_AUTO_POWER_OFF_TIME, "" + stat_autopower_time );
                    editor.putBoolean( Const.Setting.KEY_AUTO_POWER_ON, stat_autopower );
                    editor.putBoolean( Const.Setting.KEY_BEEP, stat_beep );
//					editor.putString( Const.Setting.KEY_PEN_COLOR, ""+stat_pencolor );
                    editor.putString( Const.Setting.KEY_SENSITIVITY, ""+stat_sensitivity );
                    editor.putBoolean( Const.Setting.KEY_HOVER_MODE, stat_hovermode );
                    editor.putBoolean( Const.Setting.KEY_PEN_CAP_ON, pencap_on );
                    editor.putBoolean( Const.Setting.KEY_OFFLINE_DATA_SAVE, stat_offlinesave );

                    editor.commit();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
            break;


            case PenMsgType.OFFLINE_DATA_NOTE_LIST:

                try
                {
                    JSONArray list = new JSONArray( penMsg.getContent() );

                    for ( int i = 0; i < list.length(); i++ )
                    {
                        JSONObject jobj = list.getJSONObject( i );

                        int sectionId = jobj.getInt( Const.JsonTag.INT_SECTION_ID );
                        int ownerId = jobj.getInt( Const.JsonTag.INT_OWNER_ID );
                        int noteId = jobj.getInt( Const.JsonTag.INT_NOTE_ID );
                        NLog.d( "offline(" + ( i + 1 ) + ") note => sectionId : " + sectionId + ", ownerId : " + ownerId + ", noteId : " + noteId );

                        // Requesting Individual Pages
                        iPenCtrl.reqOfflineDataPageList(sectionId, ownerId, noteId);
//                        iPenCtrl.reqOfflineData(sectionId,  ownerId, noteId );
                    }

                }
                catch ( JSONException e )
                {
                    e.printStackTrace();
                } catch (ProtocolNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                break;

            case PenMsgType.OFFLINE_DATA_PAGE_LIST:

                try
                {
                    JSONArray list = new JSONArray( penMsg.getContent() );

                    int prvSec = -1;
                    int prvOwn = -1;
                    int prvNote = -1;
                    ArrayList<Integer> pageIds = new ArrayList<>( );

                    for ( int i = 0; i < list.length(); i++ )
                    {
                        JSONObject jobj = list.getJSONObject( i );

                        int sectionId = jobj.getInt( Const.JsonTag.INT_SECTION_ID );
                        int ownerId = jobj.getInt( Const.JsonTag.INT_OWNER_ID );
                        int noteId = jobj.getInt( Const.JsonTag.INT_NOTE_ID );
                        int pageId = jobj.getInt( Const.JsonTag.INT_PAGE_ID );
                        Log.i("offline-page-req", "offline(" + ( i + 1 ) + ") note => sectionId : " + sectionId + ", ownerId : " + ownerId + ", noteId : " + noteId + ", pageId : " + pageId );

                        pageIds.add( pageId );
                        // 오프라인 데이터 리스트 페이지 단위로 받기
                        if( prvSec != sectionId || prvOwn != ownerId || prvNote != noteId )
                        {
//                            iPenCtrl.reqOfflineData( sectionId, ownerId, noteId, page );
                            offlineNotes.add(new NoteModel(sectionId, ownerId, noteId, pageIds));
                            pageIds.clear();
                        }

                    }

                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                    if (smartPenListener != null){
                        smartPenListener.error("Error during offline data processing - organizing notes - " + e.getMessage());
                    }
                }

                break;

        }
    }

    @Override
    public void onReceiveDot(String s, Dot dot) {
        Log.i("NPROJ- dot", new Gson().toJson(dot));
        int actionType = -1;
        if (DotType.isPenActionDown(dot.dotType))
            actionType = 1;
        else if (DotType.isPenActionUp(dot.dotType)) {
            actionType = 2;
            Symbol sm = symbolController.getApplicableSymbol(dot.x, dot.y);
            if (sm != null){
                if (smartPenListener != null){
                    smartPenListener.onPaperButtonPress(sm.getId(), sm.getName());
                }
            }
        }
        else if (DotType.isPenActionMove(dot.dotType))
            actionType = 3;

        DrawLiveDataBuffer.insertAction(new DrawLiveDataBuffer.DrawAction(
                dot.x, dot.y, dot.pageId, actionType, false
        ));
    }

    public enum CONNECT_MESSAGE{
        CONFIG_ERROR,
        CONFIG_SUCCESS,
        REQUESTING_PERMS
    }

    //NEO Variables
    private IPenCtrl iPenCtrl;


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

        connectionsHandler = new ConnectionsHandler();
        symbolController = new SymbolController();



        //NEOCODE - Start
        iPenCtrl = PenCtrl.getInstance();
        iPenCtrl.setContext(activity.getApplicationContext());
        iPenCtrl.registerBroadcastBTDuplicate();
        iPenCtrl.setListener(this);
        iPenCtrl.setDotListener(this);

        mPref = activity.getSharedPreferences("SMART_PEN_PREFS", Context.MODE_PRIVATE);

        if (PermissionsHelper.haveRequiredPermissions(activity)){
            return  CONNECT_MESSAGE.CONFIG_SUCCESS;
        } else {
            PermissionsHelper.requestPermission(activity, isPermitted -> {
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
        iPenCtrl.unregisterBroadcastBTDuplicate();
        iPenCtrl.disconnect();
    }

    boolean connectToPenAfterPermission = false;

    public void connectToPen(SmartPen smartPen){
        connectionsHandler.stopScanning(activity);
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
        String mac_address = smartPen.getId().toLowerCase();
        try {
                boolean leResult = iPenCtrl.setLeMode( smartPen.isLe() );
                if( leResult )
                {
                    if( smartPen.getUuidVer() == BTLEAdt.UUID_VER.VER_5 )
                        iPenCtrl.connect( smartPen.getSppAddress(), smartPen.getLeAddress(), smartPen.getUuidVer(), Const.APP_TYPE_FOR_PEN, Const.REQ_PROTOCOL_VER );
                    else
                        iPenCtrl.connect( smartPen.getSppAddress(), smartPen.getLeAddress() );
                }
                else
                {
                        iPenCtrl.connect( smartPen.getSppAddress() );
                }
        } catch (Exception e){
            e.printStackTrace();
            smartPenListener.error("Connection Error: " + e.getMessage());
        }
    }




    //--------------------OFFLINE DATA HANDLING SECTION-------------------------------
    private ArrayList<NoteModel> offlineNotes = new ArrayList<>();
    public boolean isOfflineDataAvailable(){
        return offlineNotes.size() > 0;
    }
    public void transferOfflineData(){
        if (offlineNotes.size() <= 0|| iPenCtrl == null){
            return;
        }
        try {
            for (NoteModel note : offlineNotes) {
                iPenCtrl.setOffLineDataListener(new IOfflineDataListener() {
                    @Override
                    public void onReceiveOfflineStrokes(Object o, String s, Stroke[] strokes, int i, int i1, int i2, kr.neolab.sdk.metadata.structure.Symbol[] symbols) {
                        for (Stroke stroke: strokes){
                            for (Dot d: stroke.getDots()){
                                //Todo: Handle Button Presses Offline
                                onReceiveDot(s, d);
                            }
                        }
                    }
                });
                iPenCtrl.reqOfflineData(note.sectionId, note.ownerId, note.noteId, note.getPagesArray());
            }
            offlineNotes.clear();
        } catch (Exception e){
            e.printStackTrace();
            if (smartPenListener != null){
                smartPenListener.error("Error during offline data processing - transferring data - " + e.getMessage());
            }
        }
    }
}
