package com.erikeldridge.treestore.example;

import android.app.Activity;
import android.database.Cursor;
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

                // simple range query
                store.put("users/1/phone", "+1234567890");
                store.put("users/1/email", "1@example.com");
                final Map<String, String> userData = store.get("users/1"); // {"users/1/name":"Ms. Foo", "users/1/email":"1@example.com"...}
                store.delete("users/1");

                // advanced key-value
                store.put("messages/1/text", ":)", new TTL(1, TimeUnit.HOURS));
                store.put("messages/2/text", ":(", new TTL(1, TimeUnit.HOURS));
                store.clean(); // remove expired entries

                // advanced range query
                final Map<String, String> messagesData = store.get("messages", "asc", 10); // first 10 messages

                // ludicrous mode
                Cursor cursor = store.db.rawQuery(String.format(
                        "select %s,%s from %s where %s like '%%' || ? || '%%'",
                        TreeStore.COLUMN_PATH, TreeStore.COLUMN_VALUE, TreeStore.TABLE,
                        TreeStore.COLUMN_VALUE), new String[]{":)"});
                final Map<String, String> happyMessages = TreeStore.toMap(cursor);
                cursor.close();

                store.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(activity.getString(R.string.output, nameData,
                                userData.toString(), messagesData.toString(), happyMessages.toString()));
                    }
                });
            }
        }).start();
    }
}
