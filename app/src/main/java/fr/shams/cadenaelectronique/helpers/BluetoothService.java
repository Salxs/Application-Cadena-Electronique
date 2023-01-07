package fr.shams.cadenaelectronique.helpers;

import android.Manifest;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.UUID;

public class BluetoothService extends IntentService {

    private BluetoothSocket mSocket;
    private OutputStream mOutStream;
    private InputStream mInStream;

    public BluetoothService() {
        super("BluetoothService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Connexion un appareil Bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Permission Check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bluetoothAdapter.getAddress());
        try {
            mSocket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            mSocket.connect();
            mOutStream = mSocket.getOutputStream();
            mInStream = mSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] data) throws IOException {
        mOutStream.write(data);
    }

    public byte[] receiveData() throws IOException{
        byte[] buffer = new byte[1024];
        int bytesRead = mInStream.read(buffer);
        return Arrays.copyOfRange(buffer, 0, bytesRead);
    }

    public void closeConnection() throws IOException {
        mSocket.close();
    }
}
