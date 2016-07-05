package br.com.schelb.fernando.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btPareados;
    ListView lvDevicesPareados;

    private BluetoothAdapter adapterBT = null;
    private Set<BluetoothDevice> setDevicesPareados;

    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapterBT = BluetoothAdapter.getDefaultAdapter();

        btPareados = (Button) findViewById(R.id.bt_pareados);
        lvDevicesPareados = (ListView) findViewById(R.id.lv_devices_pareados);

        if(adapterBT == null){

            Toast.makeText(this,"Aparelho Bluetooth não está habilitado", Toast.LENGTH_SHORT).show();

        }else {
            if(!adapterBT.isEnabled()){
                //habilita O BLUETOOTH
                Intent ativaBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(ativaBT,1);
            }
        }


        btPareados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listarDevicesPareados();
            }
        });

    }

    private void listarDevicesPareados() {

        setDevicesPareados = adapterBT.getBondedDevices();
        ArrayList listaPareados = new ArrayList();

        if (setDevicesPareados.size() > 0){

            for(BluetoothDevice bt : setDevicesPareados)
            {
                listaPareados.add(bt.getName() + "\n" + bt.getAddress());
            }
        }else {
            Toast.makeText(this,"Não existe aparelho Bluetooth pareado",Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listaPareados);

        lvDevicesPareados.setAdapter(arrayAdapter);
        lvDevicesPareados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Pega o Mac Adress apartir do texto retornado da lista
                String informacoes = ((TextView) view).getText().toString();
                String macAddress = informacoes.substring(informacoes.length() - 17);

                Intent intent = new Intent(MainActivity.this, ControleArduino.class);

                intent.putExtra(EXTRA_ADDRESS, macAddress); //this will be received at ledControl (class) Activity
                startActivity(intent);
            }
        });
    }
}
