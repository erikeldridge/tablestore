package com.erikeldridge.tablestore.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.erikeldridge.tablestore.TableStore;

import java.util.Map;

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
                String userId = "1";
                store.put("users", userId, "name", "Ms. Foo");
                store.put("users", userId, "phone", "+1234567890");
                store.put("users", userId, "email", "1@example.com");
                final String phoneData = store.get("users", userId, "phone");
                final Map<String, String> personData = store.get("users", userId);
                store.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(activity.getString(R.string.output, phoneData,
                                personData.toString()));
                    }
                });
            }
        }).start();
    }
}
