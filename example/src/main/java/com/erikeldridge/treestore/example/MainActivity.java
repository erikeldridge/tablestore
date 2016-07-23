package com.erikeldridge.treestore.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.erikeldridge.treestore.TreeStore;

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
                TreeStore store = TreeStore.open(activity);

                // simple key-value
                store.put("users/1/name", "Ms. Foo");
                final Map<String, String> nameData = store.get("users/1/name"); // {"users/1/name":"Ms. Foo"}
                store.delete("users/1/name");

                // simple range query
                store.put("users/1/phone", "+1234567890");
                store.put("users/1/email", "1@example.com");
                final Map<String, String> userData = store.get("users/1"); // {"users/1/name":"Ms. Foo", "users/1/email":"1@example.com"...}
                store.delete("users/1");

                // advanced range query
                store.put("messages/1/text", ":)");
                store.put("messages/2/text", ":(");
                final Map<String, String> messagesData = store.get("messages", "asc", 10); // first 10 messages

                // ludicrous mode
                int count = store.db.delete(TreeStore.TABLE, String.format(
                        "%s like ? || '%%' and cast(strftime('%%s', 'now') as integer) - %s > 60", // delete messages older than a minute
                        TreeStore.COLUMN_PATH, TreeStore.COLUMN_UPDATED),
                        new String[]{"messages"});

                store.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(activity.getString(R.string.output, nameData,
                                userData.toString(), messagesData.toString()));
                    }
                });
            }
        }).start();
    }
}
