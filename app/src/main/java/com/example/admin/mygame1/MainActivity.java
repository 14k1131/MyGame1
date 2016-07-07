package com.example.admin.mygame1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private Move move;

    private Button button1,button2;         //ボタン用変数
    private TextView text1;            //テキスト用変数
    private int clickcount=0;        //表示用 カウンター


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        //layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        move = new Move(this);
        move.startSensor();
/*
        //で作成した変数と、XMLで作ったボタン(button1)を紐付け
        button1=(Button)findViewById(R.id.button1);
        //button1のクリックを通知する機能を実装
        button1.setOnClickListener(this);

        //作成した変数と、XMLで作ったボタン(button2)を紐付け
        button2=(Button)findViewById(R.id.button2);
        //button1のクリックを通知する機能を実装
        button2.setOnClickListener(this);

*/
       // 作成した変数と、XMLで作ったテキストを紐付け
        text1  =(TextView)findViewById(R.id.textView1);
        layout.addView(button1);
        layout.addView(button2);
        layout.addView(text1);


        layout.addView(move);



    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //音楽を停止する
        move.stopSound();
    }


    //ボタンがクリックされたら呼ばれる関数
        public void onClick(View v) {
        if(v==button1){        //ボタン1の場合
            if(clickcount%2==0){
                text1.setText("成功");        //text1を変更
            }else{
                text1.setText(" ");            //text1を消す
            }
            clickcount++;
        }else if(v==button2){
            move.stopSound();
            Intent intent = new Intent(this, SubActivity.class);
            startActivityForResult(intent, 0);
        }

    }
}
