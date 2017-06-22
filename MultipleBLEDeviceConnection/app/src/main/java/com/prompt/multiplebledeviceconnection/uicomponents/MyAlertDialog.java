package com.prompt.multiplebledeviceconnection.uicomponents;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.prompt.multiplebledeviceconnection.R;

public class MyAlertDialog extends Dialog {
    private Context context;
    private ImageView imageView;
    private TextView textView;
    private Button button;

    public MyAlertDialog(Context context) {
        super (context);
        this.context = context ;
        this.requestWindowFeature (Window.FEATURE_NO_TITLE);
        this.setContentView (R.layout.alert_dialog);
        this.getWindow ().setType (WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.imageView = (ImageView) this.findViewById (R.id.imgAlert);
        this.textView = (TextView) this.findViewById (R.id.textViewAlert);
        this.button = (Button) this.findViewById (R.id.buttonAlert);
        button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                MyAlertDialog.this.dismiss ();
            }
        });
    }

    public void setMessage(String message) {
        this.textView.setText (message);
    }

    public void setIcon(int icon) {
        this.imageView.setImageResource (icon);
        switch (icon) {
            case R.drawable.ic_alert:
                this.textView.setTextColor (context.getResources ().getColor (R.color.colorPrimaryDark));
                break ;
            case R.drawable.ic_success:
                this.textView.setTextColor (context.getResources ().getColor (R.color.colorPrimary));
                break ;
            default:
                this.textView.setTextColor (context.getResources ().getColor (R.color.colorPrimaryDark));
                break ;
        }
    }
}
