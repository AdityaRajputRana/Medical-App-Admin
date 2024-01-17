package com.example.medicalappadmin.PenDriver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalappadmin.PenDriver.Models.SmartPen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.neolab.sdk.pen.PenCtrl;
import kr.neolab.sdk.pen.bluetooth.BTLEAdt;
import kr.neolab.sdk.util.NLog;
import kr.neolab.sdk.util.UuidUtil;

public class ConnectionsHandler {

    private boolean isScanning = false;
    private ScanCallback mScanCallback;
    private BluetoothLeScanner mLeScanner;

    @SuppressLint("MissingPermission")
    public void stopScanning(Context context) {
        if (!isScanning && !PermissionsHelper.haveRequiredPermissions(context)){
            return;
        }
        mLeScanner.stopScan(mScanCallback);
        isScanning = false;
    }

    private class DeviceInfo {
        String sppAddress = "";
        String leAddress = "";
        String deviceName = "";
        boolean isLe = false;
        BTLEAdt.UUID_VER uuidVer;
        int colorCode = 0;
        int productCode = 0;
        int companyCode = 0;

    }

    public interface PenConnectionsListener {
        void onSmartPens(ArrayList<SmartPen> smartPens);
        void onSmartPen(SmartPen smartPen);
    }

    //Todo: 1. Currently only handling LE connections and not saving paired pen for automatic connection
    @SuppressLint("MissingPermission")
    public void getSmartPenList(Context context, PenConnectionsListener listener) {
        if (isScanning){
            return;
        }
        isScanning = true;
        Log.i("ConnectionHelper: ", "Getting SmartPen List");
        if (!PermissionsHelper.haveRequiredPermissions(context)){
            PermissionsHelper.requestPermission((AppCompatActivity) context, isPermitted -> {
                if (isPermitted)
                    getSmartPenList(context, listener);
            });
            return;
        }
        if (Build.VERSION.SDK_INT < 21) {
            //Todo: standard search
        } else {
            //Le Search
            BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            mLeScanner = mBtAdapter.getBluetoothLeScanner();

            ArrayList<SmartPen> smartPens = new ArrayList<>();
            HashMap<String, Boolean> deviceMap = new HashMap<String, Boolean>();


            //Receiver
            BroadcastReceiver mReceiver = new BroadcastReceiver() {
                @SuppressLint("MissingPermission")
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i("ConnectionHelper:", "OnReceiver: Worker Thread");
                    String action = intent.getAction();

                    // When discovery finds a device
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        // Get rssi value
                        short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                            DeviceInfo info = new DeviceInfo();
                            info.sppAddress = device.getAddress();
                            info.leAddress = "";
                            info.deviceName = device.getName();
                            info.uuidVer = BTLEAdt.UUID_VER.VER_2;
                            info.colorCode = -1;


                            NLog.d("ACTION_FOUND SPP : " + device.getName() + " address : " + device.getAddress() + ", COD:" + device.getBluetoothClass());

                            SmartPen smartPen = new SmartPen(device.getAddress(), device.getName(), device.getAddress());
                            smartPen.setLe(false);
                            smartPen.setLeAddress(info.leAddress);
                            smartPen.setSppAddress(info.sppAddress);
                            smartPen.setUuidVer(info.uuidVer);
                            info.colorCode = info.colorCode;
                            listener.onSmartPen(smartPen);
                            smartPens.add(smartPen);
                        }
                        // When discovery is finished, change the Activity title
                    }
                    else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                    {
                        listener.onSmartPens(smartPens);
                        isScanning = false;
                    }
                }
            };

            mScanCallback = new ScanCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Handler looper = new Handler(Looper.getMainLooper());
                    Thread worker = new Thread(()->{
                        super.onScanResult(callbackType, result);
                        BluetoothDevice device = result.getDevice();
                        if ( device != null )
                        {
                            String sppAddress = UuidUtil.changeAddressFromLeToSpp(result.getScanRecord().getBytes());
                            @SuppressLint("MissingPermission") String msg = device.getName() + "\n" +"[RSSI : " + result.getRssi() + "dBm]" + sppAddress;
                            if( !deviceMap.containsKey( sppAddress ) )
                            {

                                PenCtrl.getInstance().setLeMode( true );
                                if( PenCtrl.getInstance().isAvailableDevice( result.getScanRecord().getBytes() ) )
                                {
                                    DeviceInfo info = new DeviceInfo();
                                    info.sppAddress = sppAddress;
                                    info.leAddress = device.getAddress();
                                    info.deviceName = device.getName();
                                    info.uuidVer = BTLEAdt.UUID_VER.VER_2;
                                    info.colorCode = -1;

                                    List<ParcelUuid> parcelUuids = result.getScanRecord().getServiceUuids();
                                    for(ParcelUuid uuid:parcelUuids)
                                    {
                                        if( uuid.toString().equals(Const.ServiceUuidV5.toString()))
                                        {
                                            info.uuidVer = BTLEAdt.UUID_VER.VER_5;
                                            info.colorCode = UuidUtil.getColorCodeFromUUID(result.getScanRecord().getBytes());
                                            info.companyCode = UuidUtil.getCompanyCodeFromUUID(result.getScanRecord().getBytes());
                                            info.productCode = UuidUtil.getProductCodeFromUUID(result.getScanRecord().getBytes());
                                            break;
                                        }
                                        else if(uuid.toString().equals(Const.ServiceUuidV2.toString()))
                                        {
                                            info.uuidVer = BTLEAdt.UUID_VER.VER_2;
                                            info.colorCode = UuidUtil.getColorCodeFromUUID(result.getScanRecord().getBytes());
                                            info.companyCode = UuidUtil.getCompanyCodeFromUUID(result.getScanRecord().getBytes());
                                            info.productCode = UuidUtil.getProductCodeFromUUID(result.getScanRecord().getBytes());
                                            break;

                                        }

                                    }

                                    SmartPen smartPen = new SmartPen(device.getAddress(), device.getName() + "(Av device)", device.getAddress());
                                    smartPen.setSppAddress(info.sppAddress);
                                    smartPen.setLeAddress(info.leAddress);
                                    smartPen.setUuidVer(info.uuidVer);
                                    smartPen.setLe(true);
                                    smartPen.setColorCode(info.colorCode);
                                    smartPen.setProductCode(info.productCode);
                                    deviceMap.put( sppAddress, true );
                                    Log.i("Connections", "found device");
                                    looper.post(()->listener.onSmartPen(smartPen));
                                    smartPens.add(smartPen);
                                }
                            }
                        }
                    });
                    worker.start();
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    for ( ScanResult scanResult : results ) {
                        Log.i("Connection Helper - Results", scanResult.toString());
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.i("Connection Helper Failed", "Error Code : " + errorCode);
                }
            };




            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(mReceiver, filter);

            ScanSettings mScanSetting = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            List<ScanFilter>  mScanFilters = new ArrayList<>();

            mLeScanner.startScan(mScanFilters, mScanSetting, mScanCallback);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isScanning) {
                        mLeScanner.stopScan(mScanCallback);
                        isScanning = false;
                        listener.onSmartPens(smartPens);
                    }
                }
            }, 60000);
        }
    }

}
