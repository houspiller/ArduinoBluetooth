package br.com.schelb.fernando.arduinobluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ControleArduino extends AppCompatActivity {

    Button btLigar, btDesligar, btDesconectarBT;
    String address = null;
    BluetoothAdapter adapterBT = null;
    BluetoothSocket btSocket = null;
    private boolean isBTConectado = false;
    static final UUID meuUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progresso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle_arduino);

        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        btLigar = (Button)findViewById(R.id.bt_ligar);
        btDesligar = (Button)findViewById(R.id.bt_desligar);
        btDesconectarBT = (Button)findViewById(R.id.bt_off_conexao_bt);

        new ConexaoBT().execute();

        btLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ligar();
            }
        });

        btDesligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desligar();
            }
        });

        btDesconectarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desconectar();
            }
        });
    }

    private void desconectar() {
        if (btSocket!=null)
        {
            try {
                btSocket.close();
            }
            catch (IOException e){
                Toast.makeText(this,"Erro!!",Toast.LENGTH_SHORT).show();
            }
        }
        finish();

    }

    private void ligar()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("LIGAR#".toString().getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(this,"Erro!!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void desligar()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("DESLIGAR#".toString().getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(this,"Erro!!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ConexaoBT extends AsyncTask<Void,Void,Void> {

        private boolean conectadoSucesso = true;

        @Override
        protected void onPreExecute(){

            progresso =  ProgressDialog.show(ControleArduino.this,"Conectando...","Por favor, espere...");

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                if(btSocket == null || !isBTConectado){

                    adapterBT = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = adapterBT.getRemoteDevice(address);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(meuUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }

            }catch (Exception e){
                conectadoSucesso = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void resultado){
            super.onPostExecute(resultado);

            if(!conectadoSucesso){
                Toast.makeText(getBaseContext(),"Não consegui me conectar com o dispositivo selecionado",Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(getBaseContext(),"Conectado!",Toast.LENGTH_SHORT).show();
                isBTConectado = true;
            }
            progresso.dismiss();
        }

    }
}

