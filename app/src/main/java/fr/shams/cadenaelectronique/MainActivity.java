package fr.shams.cadenaelectronique;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    CheckBox enable_bt, visible_bt;
    ImageView search_bt;
    TextView name_bt;
    ListView mListView;

    ActivityResultLauncher<Intent> mIntent;
    ActivityResultLauncher<Intent> mIntentVisible;

    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enable_bt = findViewById(R.id.enable_bt);
        visible_bt = findViewById(R.id.visible_bt);
        search_bt = findViewById(R.id.search_bt);
        name_bt = findViewById(R.id.name_bt);
        mListView = findViewById(R.id.list_view);

        BA = BluetoothAdapter.getDefaultAdapter();

        name_bt.setText(getLocalBluetoothName());

        if (BA == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (BA.isEnabled()) {
            enable_bt.setChecked(true);
        }

        enable_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    BA.disable();
                    Toast.makeText(MainActivity.this, "Turned off", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    mIntent.launch(intentOn);
                }
            }
        });
        mIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 0) {
                        Toast.makeText(MainActivity.this, "Turned on", Toast.LENGTH_SHORT).show();
                    }
                });

        visible_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    mIntentVisible.launch(getVisible);
                }
            }
        });
        mIntentVisible = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 0) {
                        Toast.makeText(MainActivity.this, "Visible for 2 minutes", Toast.LENGTH_SHORT).show();
                    }
                });
        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list();
            }
        });
    }

    private void list() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();
        for (BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
        }

        Toast.makeText(this, "Showing devices", Toast.LENGTH_SHORT).show();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        mListView.setAdapter(adapter);
    }

    public String getLocalBluetoothName() {
        if (BA == null) {
            BA = BluetoothAdapter.getDefaultAdapter();
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        String name = BA.getName();
        if(name == null){
            name = BA.getAddress();
        }
        return name;
    }
}