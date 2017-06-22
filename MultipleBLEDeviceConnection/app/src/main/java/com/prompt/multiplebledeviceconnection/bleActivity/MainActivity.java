package com.prompt.multiplebledeviceconnection.bleActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.prompt.multiplebledeviceconnection.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnConfigureBLE, btnConnectBLE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnConfigureBLE = (Button) findViewById(R.id.btnConfigureBLE);
        btnConnectBLE = (Button) findViewById(R.id.btnConnectBLE);

        btnConnectBLE.setOnClickListener(this);
        btnConfigureBLE.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnConfigureBLE:
                Intent intent = new Intent(this,DeviceListActivity.class);
                startActivity(intent);
                break;
            case R.id.btnConnectBLE:
                break;
        }
    }
}
