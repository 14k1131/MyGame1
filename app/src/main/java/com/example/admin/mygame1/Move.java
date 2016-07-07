package com.example.admin.mygame1;

/**
 * Created by admin on 2016/06/18.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//加速度センサー
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//BGM
import android.media.MediaPlayer;

//画像
import android.content.res.Resources;
import android.view.View;
import android.graphics.*;

import java.util.concurrent.atomic.AtomicBoolean;


    public class Move extends SurfaceView implements SurfaceHolder.Callback {

        private static final float ACCEL_WEIGHT = 3f;

        private static final int DRAW_INTERVAL = 1000/60;
        private static final float TEXT_SIZE = 40f;

        private final Paint paint = new Paint();
        private final Paint textPaint = new Paint();

        private final Bitmap ballBitmap;
        private final Bitmap backGround;
        private final Bitmap b;
        private final Bitmap Ball;




        //ボールの座標
        private float ballX = 50;
        private float ballY = 50;
        private float x ;
        private float y ;



        //音楽読み込み
        MediaPlayer bgm = MediaPlayer.create(getContext(), R.raw.bgm1);


        //色々読み込み
        public Move(Context context){
            super(context);

            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(TEXT_SIZE);

            //画像読み込み

            ballBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ball);
            b = BitmapFactory.decodeResource(getResources(),R.drawable.stage1);
            //サイズ変更
            backGround = Bitmap.createScaledBitmap(b,1196, 574, true);
            Ball = Bitmap.createScaledBitmap( ballBitmap,100, 100, true);


            getHolder().addCallback(this);


            //音楽
            bgm.setLooping(true);
            bgm.start();


        }

        private DrawThread drawThread;

        private class DrawThread extends Thread{
            private final AtomicBoolean isFinished = new AtomicBoolean();

            public void finish(){
                isFinished.set(true);
            }

            @Override
            public void run(){
                SurfaceHolder holder = getHolder();
                while(!isFinished.get()){
                    if(holder.isCreating()){
                        continue;
                    }
                    Canvas canvas = holder.lockCanvas();
                    if(canvas == null){
                        continue;
                    }

                    //描画
                    drawPicture(canvas);
                    //画面に反映
                    holder.unlockCanvasAndPost(canvas);

                    synchronized(this){
                        try{
                            wait(DRAW_INTERVAL);
                        }
                        catch(InterruptedException e){

                        }
                    }
                }
            }
        }

        public void startDrawThread(){
            stopDrawThread();

            drawThread = new DrawThread();
            drawThread.start();
        }
        public boolean stopDrawThread() {
            if (drawThread == null) {
                return false;
            }

            drawThread.finish();
            drawThread = null;
            return true;
        }

        //安定
        private static final float ALPHA = 0.8f;

        private float[] sensorValues;
        private final SensorEventListener sensorEventListener = new SensorEventListener(){
            @Override
            //加速度の値を受け取る
            public void onSensorChanged(SensorEvent event){
              //  sensorValues = event.values;
                //sensorValues[0]:Ｘ 1:Y 2:Z
                if(sensorValues == null){
                    sensorValues = new float[3];
                    sensorValues[0] = event.values[0];
                    sensorValues[1] = event.values[1];
                    sensorValues[2] = event.values[2];
                    return;
                }

                sensorValues[0] = sensorValues[0] * ALPHA + event.values[0] * (1f - ALPHA);
                sensorValues[1] = sensorValues[1] * ALPHA + event.values[1] * (1f - ALPHA);
                sensorValues[2] = sensorValues[2] * ALPHA + event.values[2] * (1f - ALPHA);

                //ボールの位置を変える
                ballX -= sensorValues[0] * ACCEL_WEIGHT;
                ballY += sensorValues[1] * ACCEL_WEIGHT;
                //座標を横向きに合わせる
                x = ballX * (float) Math.cos(Math.toRadians(-90.0)) + ballY * (-1 * (float) Math.sin(Math.toRadians(-90.0)));
                y = ballX * (float) Math.sin(Math.toRadians(-90.0)) + ballY * (float) Math.cos(Math.toRadians(-90.0));


            }



            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            public void OnAccuracyChanged(Sensor sensor, int accuracy){

            }
        };

        //センサーの起動
        public void startSensor(){
            sensorValues = null;
            SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorEventListener, accelerometer,SensorManager.SENSOR_DELAY_GAME);

        }

        public void stopSensor(){
            SensorManager sensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(sensorEventListener);

        }


        @Override
        public void surfaceCreated(SurfaceHolder holder){
            startDrawThread();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        // @Override
        public void surfaceChange(SurfaceHolder holder, int format, int width,int height){

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder){
            stopDrawThread();
        }

        //画面への描画類
        public void drawPicture(Canvas canvas) {


            canvas.drawColor(Color.BLACK);
            //描画位置の指定
            canvas.drawBitmap(backGround, 0, 0, paint);
            canvas.drawBitmap(Ball, x, y, paint);

            //色を取得した場所の目印
            textPaint.setColor(Color.BLUE);
            canvas.drawText("☆", 10, 380, textPaint);

            //背景画像の色を取得

            //(1)薄緑
           //int c = backGround.getPixel(10,300);

            //(2)
            int c = backGround.getPixel(10,380);

            textPaint.setColor(c);
            canvas.drawText("C =" + c, 700, 200, textPaint);



            //文字列の表示
            textPaint.setColor(Color.RED);
            canvas.drawText("W =" + getWidth(),600,150,textPaint);
            canvas.drawText("H =" + getHeight(),600,300,textPaint);
            if(sensorValues != null){
                canvas.drawText("sensor[0] =" + sensorValues[0],10,150,textPaint);
                canvas.drawText("sensor[1] =" + sensorValues[1],10,200,textPaint);
                canvas.drawText("sensor[2] =" + sensorValues[2],10,250,textPaint);
            }

        }


        public void stopSound() {
            bgm.stop();
        }

    }

