package fr.shams.cadenaelectronique.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Set;

import fr.shams.cadenaelectronique.R;
import fr.shams.cadenaelectronique.helpers.BluetoothService;

public class MainActivity extends AppCompatActivity {


    //Déclaration des attributs pour les widgets
    private CheckBox enable_bt, visible_bt;
    private ImageView search_bt;
    private TextView name_bt;
    private ListView mListView;
    private Button mButton;
    //Déclaration des atributs pour la gestion du Bluetooth
    private BluetoothService mBluetoothService;
    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothService.BluetoothBinder binder = (BluetoothService.BluetoothBinder) iBinder;
            mBluetoothService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    //Attributs utilisés pour la méthode RegisterForActivityResult
    ActivityResultLauncher<Intent> mIntent;
    ActivityResultLauncher<Intent> mIntentVisible;

    //Déclaration des attributs Privée
    private BluetoothAdapter BA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind to the Bluetooth service
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        //Affectation des widgets au différentes variables
        enable_bt = findViewById(R.id.enable_bt);
        visible_bt = findViewById(R.id.visible_bt);
        search_bt = findViewById(R.id.search_bt);
        name_bt = findViewById(R.id.name_bt);
        mListView = findViewById(R.id.list_view);
        mButton = findViewById(R.id.button_bt);


        BA = BluetoothAdapter.getDefaultAdapter();
        name_bt.setText(getLocalBluetoothName());

        if (BA == null) {
            Toast.makeText(this, getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT).show();
            finish();
        }
        if (BA.isEnabled()) {
            enable_bt.setChecked(true);
        }

        //Portion de code ayant pour rôle de contrôler l'interface Bluetooth
        enable_bt.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
                Toast.makeText(MainActivity.this, getString(R.string.eteint), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mIntent.launch(intentOn);
            }
        });
        mIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 0) {
                        Toast.makeText(MainActivity.this, getString(R.string.allumer), Toast.LENGTH_SHORT).show();
                    }
                });

        visible_bt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                mIntentVisible.launch(getVisible);
            }
        });
        mIntentVisible = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 0) {
                        Toast.makeText(MainActivity.this, getString(R.string.visible), Toast.LENGTH_SHORT).show();
                    }
                });
        search_bt.setOnClickListener(view -> list());

        //Portion de code permettant l'éxécution de l'activité suivante à l'aide d'un bouton
        mButton.setOnClickListener(view -> {
            Intent menuActivity = new Intent(MainActivity.this, MenuActivity.class );
            startActivity(menuActivity);
        });

    }

    //Fonction permettant d'associer les différents appareils à l'interface Bluetooth
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
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();
        for (BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
        }

        Toast.makeText(this, getString(R.string.showing_device), Toast.LENGTH_SHORT).show();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        mListView.setAdapter(adapter);
    }

    //Fonction permettant la récupération des noms/adresses des appareils Bluetooth
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