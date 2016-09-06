[🇨🇳 中文](README-ZH.md) / [🇬🇧 English](README.md)
# 🍼Debug Bottle
An Android debug / develop tools written using Kotlin language.

### What can I do with Debug Bottle?
- Simple OkHttp Sniffer
- 3D preview an Activity with Scalpel
- Simple shared preferences editor
- Open strict mode any time
- Find leaks by using Leak Canary
- Find UI Blocks by using Block Canary
- Mock Activity or function entries in developing

### How do I use it?

### Setting up

#### 1. Configure your Gradle project
Edit and add dependencies in your app module:

```gradle
dependencies {
    debugCompile project(':components')
    releaseCompile project(':noop')
    compile "com.android.support:appcompat-v7:23.2.0+"
    compile "com.android.support:support-v4:23.2.0+"
}
```

#### 2. Edit Manifest
App Debug Bottle main Activity in your Manifest:
```xml
<activity
    android:name="me.chunyu.yuriel.kotdebugtool.components.__DTDrawerActivity"
    android:theme="@style/Theme.AppCompat.Light"/>
```

#### 3. Inject Debug Bottle into your Application
First, you may implement Block Canary Context:
```java
public class AppBlockCanaryContext extends BlockCanaryContext {...}
```

Now you could inject Debug Bottle in your Application like:
```java
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Installer.install(this)
            .setBlockCanary(AppBlockCanaryContext(this))
            .setOkHttpClient(httpClient)
            .setInjector("your.package.injector.ContentInjector")
            .setPackageName("your.package")
            .run();
    }
}
```

### Links

* [Leak Canary](https://github.com/square/leakcanary)
* [Android Performance Monitor](https://github.com/markzhai/AndroidPerformanceMonitor)
* [Scalpel](https://github.com/JakeWharton/scalpel)

### License

###### Debug Bottle
```
Copyright 2016 Yuriel (http://exyui.com).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

###### Android Performance Monitor
```
Copyright (C) 2016 MarkZhai (http://zhaiyifan.cn).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
###### Scalpel
```
Copyright 2014 Jake Wharton

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
