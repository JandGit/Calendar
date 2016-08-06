package com.lau.calendar;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lau.calendar.model.UserActBean;

public class AddActActivity extends AppCompatActivity {

    TextView et_title, et_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_act);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("添加日程");
        }

        et_title = (TextView) findViewById(R.id.et_title);
        et_time = (TextView) findViewById(R.id.et_time);

    }

    public void confirmAdding(View view){
        String title = null, time = null;
        title = et_title.getText().toString();
        time = et_time.getText().toString();
        if(title.isEmpty() || time.isEmpty()){
            Toast.makeText(this, "日期与标题均不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        UserActBean act = new UserActBean();
        act.setTitle(title);
        act.setTime(time);
        Intent intent = new Intent();
        intent.putExtra("data", act);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home == item.getItemId()){
            setResult(RESULT_CANCELED);
            finish();
        }
        return true;
    }
}
