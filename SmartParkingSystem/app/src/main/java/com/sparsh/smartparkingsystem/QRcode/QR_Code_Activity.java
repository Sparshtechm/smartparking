package com.sparsh.smartparkingsystem.QRcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity;

public class QR_Code_Activity extends AppCompatActivity {

    ImageView iv_qr_code, iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr_code);

        iv_qr_code = (ImageView)findViewById(R.id.iv_qr_code);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QR_Code_Activity.this, DashboardActivity.class));
                finish();
            }
        });
        generate_qr_code();
    }


    public void generate_qr_code(){

        // EditText qrInput = (EditText) findViewById(R.id.qrInput);
        String qrInputText = "Sparsh Saxena";//qrInput.getText().toString();
        // Log.v(LOG_TAG, qrInputText);

        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;

        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
           // ImageView myImage = (ImageView) findViewById(R.id.imageView1);
            iv_qr_code.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
