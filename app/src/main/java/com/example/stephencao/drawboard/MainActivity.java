package com.example.stephencao.drawboard;

import android.content.Intent;
import android.graphics.*;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.io.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView imageView;
    private Canvas canvas;
    private Paint paint;
    private boolean boldFlag = false;
    private Bitmap bitmapCopy;
    private Button resetBtn, saveBtn,boldBtn;
    private Spinner spinner;
    private String[] colors ={"red","black","yellow","blue","green"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        imageView.setOnTouchListener(new View.OnTouchListener() {
            int startX = 0;
            int startY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN: {//触摸
                        Log.i("xiaohema", "start");
                        //获取当前画线的开始位置
                        startX = (int) event.getX(); // 当前点的x坐标
                        startY = (int) event.getY(); // 当前点的y坐标
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {//移动
                        Log.i("xiaohema", "move");
                        int stopX = (int) event.getX();
                        int stopY = (int) event.getY();
                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        imageView.setImageBitmap(bitmapCopy);
                        startX = stopX;
                        startY = stopY;
                        break;
                    }
                    case MotionEvent.ACTION_UP: {//抬起手指
                        Log.i("xiaohema", "leave");
                        break;

                    }
                }
                return true;
            }
        });

    }

    private void initView() {
        spinner = new Spinner(getApplicationContext());
        spinner = findViewById(R.id.spinner_list_items);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.spinner_items,colors);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(arrayAdapter);
        imageView = findViewById(R.id.image_view);
        final Bitmap bitmapSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.board);
        bitmapCopy = generateImageCopy(bitmapSrc);
        imageView.setImageBitmap(bitmapCopy);
        resetBtn = findViewById(R.id.reset_image_view_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapCopy = generateImageCopy(bitmapSrc);
                imageView.setImageBitmap(bitmapCopy);
            }
        });
        saveBtn = findViewById(R.id.save_image_view_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "drawing.jpg");
                try {
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                    bitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                    bufferedOutputStream.close();
                    //发一条广播，告诉gallery sd卡重新加载了
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
                    intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
                    sendBroadcast(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        boldBtn = findViewById(R.id.text_bold_btn);
        boldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boldFlag)
                {
                    paint.setStrokeWidth(10f);
                    boldBtn.setText("unbold");
                    boldFlag = true;
                }
                else{
                    paint.setStrokeWidth(1f);
                    boldBtn.setText("bold");
                    boldFlag = false;
                }


            }
        });
    }

    private Bitmap generateImageCopy(Bitmap bitmapSrc) {
        Bitmap bitmapCopy = Bitmap.createBitmap(bitmapSrc.getWidth(), bitmapSrc.getHeight(), bitmapSrc.getConfig());
        canvas = new Canvas(bitmapCopy);
        paint = new Paint();
        canvas.drawBitmap(bitmapSrc, new Matrix(), paint);
        return bitmapCopy;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:{
                paint.setColor(Color.RED);
                break;
            }
            case 1:{
                paint.setColor(Color.BLACK);
                break;
            }
            case 2:{
                paint.setColor(Color.YELLOW);
                break;
            }
            case 3:{
                paint.setColor(Color.BLUE);
                break;
            }
            case 4:{
                paint.setColor(Color.GREEN);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
