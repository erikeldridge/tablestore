# TreeStore

A key-value store for Android with trivial set up and support for hierarchical queries.

## Example

```java
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
```

See [MainActivity.java](example/src/main/java/com/erikeldridge/treestore/example/MainActivity.java) for example integration.

## Problem statement

Android provides a number of [storage options](https://developer.android.com/guide/topics/data/data-storage.html), but nothing between simple key-value pairs in Shared Preferences and full relational data in SQLite.

## Solution

TreeStore provides **a simple get/put/delete interface** for persisting key-value pairs.

Because we often work with data classes, which flatten nicely as hierarchical keys, we can get a lot of utility from **hierarchical queries**. TreeStore provides this via materialized paths. Queries are fast because paths are primary keys in the database.

For example:

```
class User {
  public final String id;
  public final String name;
}
...
User user = new User(1, "Ms. Foo")
...
store.put("users/"+user.id+"/name", user.name);
...
store.get("users/"+user.id); // {"users/1/name":"Ms. Foo"}
```

Finally, TreeStore is easy to set up, use, and extend because it's just **standard SQLite** under the hood.

TreeStore takes inspiration from stores with multi-level keys like [CDB](http://cr.yp.to/cdb.html) and [SSTable](https://www.igvita.com/2012/02/06/sstable-and-log-structured-storage-leveldb/)/[BigTable](https://en.wikipedia.org/wiki/Bigtable#Design)/[LevelDB](https://github.com/google/leveldb)/[Firebase](https://firebase.google.com/docs/database/web/structure-data), data structures like [Guava's Table collection](https://github.com/google/guava/wiki/NewCollectionTypesExplained#table), and tree storage tools like [django-treebeard](https://tabo.pe/projects/django-treebeard/docs/4.0.1/index.html).

## Installation

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
dependencies {
    compile 'com.github.erikeldridge:treestore:x.y.z@aar' // see https://github.com/erikeldridge/treestore/releases
}
```

## License

The MIT License (MIT)

Copyright (c) 2016 Erik Eldridge

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.