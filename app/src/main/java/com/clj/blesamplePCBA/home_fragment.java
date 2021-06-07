package com.clj.blesamplePCBA;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.blesamplePCBA.adapter.DeviceAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import android.os.Handler;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_fragment#newInstance} factory method to
 * create an instance of this fragment.\
 *
 */
public class home_fragment extends Fragment {
    private int mInterval = 1000;
    private Handler mHandler;
    List<BleDevice> connectedDevices;
    //private String[] sensorData;


    public final static UUID UUID_SERVICE = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    public final static UUID UUID_CHARACTERISTIC = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private DeviceAdapter connectedDeviceAdapter;
    private List<BluetoothGattService> serviceList;
    private List<BluetoothGattCharacteristic> characteristicList;
    private RelativeLayout addDevicesReminder;
    Map<String, Integer> mymap;
    Map<Integer, String> mymapName;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home_fragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_fragment newInstance(String param1, String param2) {
        home_fragment fragment = new home_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        getData();

        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_home, container, false); //pass the correct layout name for the fragment

        String temp = "––";
        String RH = "––";
        String R1 ="––";
        String R2 = "––";
        String R3 = "––";
        String R4 = "––";

        TextView t6 = (TextView) view.findViewById(R.id.tempx);
        t6.setText(temp);
        TextView t1 = (TextView) view.findViewById(R.id.RHx);
        t1.setText(RH);
        TextView t2 = (TextView) view.findViewById(R.id.R1x);
        t2.setText(R1);
        TextView t3=(TextView) view.findViewById(R.id.R2x);
        t3.setText(R2);
        TextView t4=(TextView) view.findViewById(R.id.R3x);
        t4.setText(R3);
        TextView t5=(TextView) view.findViewById(R.id.R4x);
        t5.setText(R4);
        getData();
        // Inflate the layout for this fragment
        return view;

    }
    public void getData() {
        connectedDeviceAdapter = ((MainActivity)getActivity()).getDeviceAdapter();
        for (int ii = 0; ii < connectedDeviceAdapter.getCount();ii++) {
            final BleDevice tempDevice = connectedDeviceAdapter.getItem(ii); // Get BLE devices from connected list
            if (tempDevice.getGraphStatus() == 1) { // Filter by graph status
                serviceList = BleManager.getInstance().getBluetoothGattServices(tempDevice);
                for (BluetoothGattService gattService:serviceList) { // Parse all services
                    characteristicList = BleManager.getInstance().getBluetoothGattCharacteristics(gattService);
                    if (gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString())) { // Filter by UUID
                        for (BluetoothGattCharacteristic gattCharacteristic : characteristicList) { // Parse all characteristics
                            if (gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_CHARACTERISTIC.toString())) { // Filter by UUID
                                BleManager.getInstance().read(
                                        tempDevice,
                                        gattService.getUuid().toString(),
                                        gattCharacteristic.getUuid().toString(),
                                        new BleReadCallback() { // Get broadcast
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onReadSuccess(final byte[] data) {

                                                    /*ArrayList<ArrayList<String>> newSheet = new ArrayList<ArrayList<String>>();
                                                    ArrayList<String> headers = new ArrayList<String>();
                                                    headers.add("Date");
                                                    headers.add("Time");
                                                    headers.add("Temperature(°C)");
                                                    headers.add("RelativeHumidity(%)");
                                                    headers.add("Concentration(ppm)");
                                                    headers.add("R1(Ω)");
                                                    headers.add("dR1(Ω/s)");
                                                    headers.add("R2(Ω)");
                                                    headers.add("dR2(Ω/s)");
                                                    headers.add("R3(Ω)");
                                                    headers.add("dR3(Ω/s)");
                                                    headers.add("R4(Ω)");
                                                    headers.add("dR4(Ω/s)");
                                                    newSheet.add(headers);
                                                */

                                                String str = new String(data, StandardCharsets.UTF_8);
                                                String[] sensorData = unpackageBLEPacket(str);
                                                //updateHome(sensorData);
                                                //info = sensorData;
                                                //showToast(Float.toString(xValueTemp) + "s @ " + sensorData[3] + "ohm");
//                                                    dataPacket = dataPacket + "A" + (String)deviceName + "/";
//                                                    dataPacket = dataPacket + "B" + (String)relativeTime + "/";
//                                                    dataPacket = dataPacket + "C" + (String)temperature + "/";
//                                                    dataPacket = dataPacket + "D" + (String)humidity + "/";
//                                                    dataPacket = dataPacket + "E" + (String)ppm + "/";
//                                                    dataPacket = dataPacket + "F" + (String)resistance[0] + "/";
//                                                    dataPacket = dataPacket + "G" + (String)deltaResistance[0] + "/";
//                                                    dataPacket = dataPacket + "H" + (String)resistance[1] + "/";
//                                                    dataPacket = dataPacket + "I" + (String)deltaResistance[1] + "/";
//                                                    dataPacket = dataPacket + "J" + (String)resistance[2] + "/";
//                                                    dataPacket = dataPacket + "K" + (String)deltaResistance[2] + "/";
//                                                    dataPacket = dataPacket + "L" + (String)resistance[3] + "/";
//                                                    dataPacket = dataPacket + "M" + (String)deltaResistance[3];
                                                int dataChecker = 0;
                                                try { // Check to see that inputs are valid numbers, otherwise skip the entire packet or else will crash
                                                    Float.valueOf(sensorData[2]);
                                                    Float.valueOf(sensorData[3]);
                                                    Float.valueOf(sensorData[4]);
                                                    Float.valueOf(sensorData[5]);
                                                    Float.valueOf(sensorData[6]);
                                                    Float.valueOf(sensorData[7]);
                                                    Float.valueOf(sensorData[8]);
                                                    Float.valueOf(sensorData[9]);
                                                    Float.valueOf(sensorData[10]);
                                                    Float.valueOf(sensorData[11]);
                                                    Float.valueOf(sensorData[12]);
                                                    dataChecker = 1;
                                                }
                                                catch (Exception e) {
                                                }
                                                if (dataChecker == 1) { // If floats all converted properly and exist, then add to chart display

                                                    String temp = sensorData[2];
                                                    String RH = sensorData[3];
                                                    String R1 =  sensorData[5];
                                                    String R2 = sensorData[7];
                                                    String R3 = sensorData[9];
                                                    String R4 = sensorData[11];
                                                    String UV = sensorData[13];

                                                    TextView t=getView().findViewById(R.id.tempx);
                                                    t.setText(temp);

                                                    TextView t1=getView().findViewById(R.id.RHx);
                                                    t1.setText(RH);
                                                    TextView t2=getView().findViewById(R.id.R1x);
                                                    t2.setText(R1);
                                                    TextView t3=getView().findViewById(R.id.R2x);
                                                    t3.setText(R2);
                                                    TextView t4=getView().findViewById(R.id.R3x);
                                                    t4.setText(R3);
                                                    TextView t5=getView().findViewById(R.id.R4x);
                                                    t5.setText(R4);

                                                    Button button = (Button) getView().findViewById(R.id.UVbutton);
                                                    TextView x=getView().findViewById(R.id.indicator);
                                                    x.setText(UV);

                                                    String ON = "ON";
                                                    String OFF = "OFF";

                                                    if(UV.equals("0")){
                                                        x.setText(OFF);
                                                        button.setText(ON);
                                                    }
                                                    else if(UV.equals("1")){
                                                        x.setText(ON);
                                                        button.setText(OFF);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onReadFailure(final BleException exception) {
                                                showToast("Bluetooth unstable.  Move close to device");
                                            }
                                        }
                                );
                            }
                        }
                    }
                }
            }
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }


    private void showToast (String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showToast (String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String[] unpackageBLEPacket(String packetData)
    {
        String[] sensorData;
        try {
            sensorData = packetData.split("/");
            for (int i = 0; i < sensorData.length; i++) {
                sensorData[i] = sensorData[i].substring(1);
            }
        }
        catch (Exception ee) {
            String[] errorHolder = new String[2];
            errorHolder[0] = "Array";
            errorHolder[1] = "Failed";
            sensorData = errorHolder;
        }
        return sensorData;
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
                getData(); //this function can change value of mInterval.

                // 100% guarantee that this always happens, even if
                // your update method throws an exception
            mHandler.postDelayed(mStatusChecker, mInterval);

        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

   /* public void write(BleDevice bleDevice,
                      String uuid_service,
                      String uuid_write,
                      byte[] data,
                      BleWriteCallback callback) {
        write(bleDevice, uuid_service, uuid_write, data, true, callback);
    }*/



    public void changeUVLed(View view) {
        connectedDeviceAdapter = ((MainActivity)getActivity()).getDeviceAdapter();
        for (int ii = 0; ii < connectedDeviceAdapter.getCount();ii++) {
            final BleDevice tempDevice = connectedDeviceAdapter.getItem(ii); // Get BLE devices from connected list
            if (tempDevice.getGraphStatus() == 1) { // Filter by graph status
                serviceList = BleManager.getInstance().getBluetoothGattServices(tempDevice);
                for (BluetoothGattService gattService:serviceList) { // Parse all services
                    characteristicList = BleManager.getInstance().getBluetoothGattCharacteristics(gattService);
                    if (gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString())) { // Filter by UUID
                        for (BluetoothGattCharacteristic gattCharacteristic : characteristicList) { // Parse all characteristics
                            if (gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_CHARACTERISTIC.toString())) { // Filter by UUID

                                TextView x=getView().findViewById(R.id.indicator);

                                String ON="UV=ON";
                                String value=null;
                                byte[] bytes=value.getBytes(StandardCharsets.UTF_8);
                                String OFF="UV=OFF";

                                String indicator = x.getText().toString();


                                if(indicator.equals("ON")){
                                    //BleManager.getInstance().write(String.valueOf("UV=ON").getBytes());
                                    value=ON;
                                    bytes = value.getBytes(StandardCharsets.UTF_8);

                                }

                                if(indicator.equals("OFF")){
                                    value=OFF;
                                    bytes = value.getBytes(StandardCharsets.UTF_8);
                                    //BleManager.getInstance().write(String.valueOf("UV=OFF").getBytes());
                                }

                                BleManager.getInstance().write(bytes, tempDevice, gattService.getUuid().toString(), gattCharacteristic.getUuid().toString(),
                                        new BleWriteCallback() { // Get broadcast
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                                showToast("Bluetooth stable.");
                                            }
                                            @Override
                                            public void onWriteFailure(BleException exception) {
                                                showToast("Bluetooth unstable.  Move close to device");
                                            }
                                        }
                                );
                            }
                        }
                    }
                }
            }
        }

    }

}