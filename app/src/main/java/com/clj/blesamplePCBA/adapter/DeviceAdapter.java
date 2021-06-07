package com.clj.blesamplePCBA.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.blesamplePCBA.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

// This class is used to define all scanning find operations for BLE devices
public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private List<BleDevice> bleDeviceList;
    private List<BleDevice> blePairedDeviceList;

    public DeviceAdapter(Context context) {
        this.context = context;
        bleDeviceList = new ArrayList<>();
        blePairedDeviceList = new ArrayList<>();
    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);

    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    @Override
    public int getCount() {
        return bleDeviceList.size();
    }

    @Override
    public BleDevice getItem(int position) {
        if (position > bleDeviceList.size())
            return null;
        return bleDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.adapter_device, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.img_blue = (ImageView) convertView.findViewById(R.id.img_blue);
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
            holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);
            holder.layout_idle = (LinearLayout) convertView.findViewById(R.id.layout_idle);
            holder.layout_connected = (LinearLayout) convertView.findViewById(R.id.layout_connected);
            holder.btn_disconnect = (Button) convertView.findViewById(R.id.btn_disconnect);
            holder.btn_connect = (Button) convertView.findViewById(R.id.btn_connect);
            holder.btn_detail = (Button) convertView.findViewById(R.id.btn_detail);
            holder.btn_graph = (Button) convertView.findViewById(R.id.btn_graph);
        }

        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {
            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
            int getGraphStatus = bleDevice.getGraphStatus();
            String name = bleDevice.getName();
            String mac = bleDevice.getMac();
            int rssi = bleDevice.getRssi();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            if (isConnected) {

                holder.txt_name.setTextColor(Color.rgb(51, 91, 210));
                holder.txt_mac.setTextColor(Color.rgb(51, 91, 210));
                holder.layout_idle.setVisibility(View.GONE);
                holder.layout_connected.setVisibility(View.VISIBLE);
                if (getGraphStatus == 1) {
                    holder.btn_graph.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    holder.img_blue.setImageResource(R.drawable.ic_baseline_bluetooth_searching_24);
                    holder.txt_mac.setText("Graphing On");
                }
                else {
                    holder.btn_graph.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    holder.img_blue.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24);
                    holder.txt_mac.setText("Device Paired");
                }
            }
            else {
                holder.img_blue.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                holder.txt_name.setTextColor(Color.rgb(115,115,115));
                holder.txt_mac.setTextColor(Color.rgb(115,115,115));
                holder.layout_idle.setVisibility(View.VISIBLE);
                holder.layout_connected.setVisibility(View.GONE);
                holder.txt_mac.setText("Device Found");
            }
        }

        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConnect(bleDevice);
                }
            }
        });

        holder.btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDisConnect(bleDevice);
                }
            }
        });

        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDetail(bleDevice);
                }
            }
        });

        holder.btn_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onGraph(bleDevice);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView img_blue;
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
        LinearLayout layout_idle;
        LinearLayout layout_connected;
        Button btn_disconnect;
        Button btn_connect;
        Button btn_detail;
        Button btn_graph;
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);

        void onGraph(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }

}
