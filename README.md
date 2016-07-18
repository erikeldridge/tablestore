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

TableStore takes inspiration from other stores with multi-level keys, namely [Manhattan](https://blog.twitter.com/2014/manhattan-our-real-time-multi-tenant-distributed-database-for-twitter-scale), [CDB](http://cr.yp.to/cdb.html), [LevelDB](https://github.com/google/leveldb), [Firebase](https://firebase.google.com/docs/database/web/structure-data), [BigTable](https://en.wikipedia.org/wiki/Bigtable#Design), and [Guava's Table collection](https://github.com/google/guava/wiki/NewCollectionTypesExplained#table).

## Installation

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    compile 'com.github.erikeldridge:tablestore:{tag|sha|branch-SNAPSHOT}@aar'
}
```

## License

Unless otherwise noted, all content in this repo is licensed as follows:

Copyright 2016 Erik Eldridge

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.