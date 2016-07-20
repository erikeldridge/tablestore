package com.erikeldridge.tablestore.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.erikeldridge.tablestore.TableStore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView view = (TextView) findViewById(R.id.output);
        assert(view != null);
        final Activity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                TableStore store = TableStore.open(activity);
                store.put("users/1/name", "Ms. Foo");
                store.put("users/1/phone", "+1234567890");
                store.put("users/1/email", "1@example.com", 1, TimeUnit.MINUTES);
                final Map<String, String> phoneData = store.get("users/1/phone");
                final Map<String, String> userData = store.get("users/1");
                store.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(activity.getString(R.string.output, phoneData,
                                userData.toString()));
                    }
                });
            }
        }).start();
    }
}
