package com.wsh.tinker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wsh.tinker.model.User;

public class MainActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        user = new User();
        user.setName("tinker热修复，已经修复好点击崩溃这个bug");

*/

        final Button bt = (Button) findViewById(R.id.bt);
        //  bt.setText(getUserName());

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.setText(getUserName());
            }
        });


    }


    private String getUserName() {
        return user.getName();
    }
}
