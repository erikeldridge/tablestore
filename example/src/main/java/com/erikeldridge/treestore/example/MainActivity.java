package com.erikeldridge.treestore.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.erikeldridge.treestore.TreeStore;
import com.erikeldridge.treestore.TreeStore.TTL;

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
                TreeStore store = TreeStore.open(activity);

                // simple key-value
                store.put("users/1/name", "Ms. Foo");
                final Map<String, String> nameData = store.get("users/1/name"); // {"users/1/name":"Ms. Foo"}
                store.delete("users/1/name");

                // range query
                store.put("users/1/phone", "+1234567890");
                store.put("users/1/email", "1@example.com");
                final Map<String, String> userData = store.get("users/1"); // {"users/1/name":"Ms. Foo", "users/1/email":"1@example.com"...}

                // advanced key-value
                store.put("users/1/location", "Chicago", new TTL(1, TimeUnit.HOURS));
                store.clean(); // remove expired entries

                // advanced range query
                final Map<String, String> usersData = store.get("users", "asc", 10); // first 10 user entries

                store.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(activity.getString(R.string.output, nameData,
                                userData.toString(), usersData.toString()));
                    }
                });
            }
        }).start();
    }
}
