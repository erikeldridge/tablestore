# TableStore

An SQLite-backed key -> values store for Android.

## Example

```java
TableStore store = TableStore.open(activity);
String userId = "1";
store.put("users", userId, "name", "Ms. Foo");
store.put("users", userId, "phone", "+1234567890");
store.put("users", userId, "email", "1@example.com");
final String phone = store.get("users", userId, "phone"); // "+1234567890"
final Map<String, String> user = store.get("users", userId); // {name: "Ms. Foo", phone: "+1234567890", ...}
store.close();
```

See [MainActivity.java](example/src/main/java/com/erikeldridge/tablestore/example/MainActivity.java) for example integration.

## Details

TableStore is just a database table and some convenience functions. The power comes from the multi-level key pattern. TableStore implements this pattern via a table with columns for type, id, attr, value:

type|id|attr|value
---|---|---|---
users|1|name|Ms. Foo

The primary key is composed of type, id, and attr, enabling fast queries for an attribute. Less specific keys enable range queries.

TableStore is easy to set up, use, and extend because it's just standard SQLite under the hood.

TableStore takes inspiration from other stores with multi-level keys, namely [Manhattan](https://blog.twitter.com/2014/manhattan-our-real-time-multi-tenant-distributed-database-for-twitter-scale), [CDB](http://cr.yp.to/cdb.html), [SSTable](https://www.igvita.com/2012/02/06/sstable-and-log-structured-storage-leveldb/)/[BigTable](https://en.wikipedia.org/wiki/Bigtable#Design)/[LevelDB](https://github.com/google/leveldb)/[Firebase](https://firebase.google.com/docs/database/web/structure-data), and [Guava's Table collection](https://github.com/google/guava/wiki/NewCollectionTypesExplained#table).

## Installation

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
dependencies {
    compile 'com.github.erikeldridge:tablestore:x.y.z@aar' // see https://github.com/erikeldridge/tablestore/releases
}
```

## License

The MIT License (MIT)

Copyright (c) 2016 Erik Eldridge

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.