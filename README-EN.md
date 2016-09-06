[🇨🇳 中文](README.md) / [🇬🇧 English](README－EN.md)
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
App Debug Bottle main Activity in your Manifest
```xml
<activity
    android:name="me.chunyu.yuriel.kotdebugtool.components.__DTDrawerActivity"
    android:theme="@style/__DemoAppActionBarTheme"/>
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
* [Block Canary](https://github.com/markzhai/AndroidPerformanceMonitor)
* [Scalpel](https://github.com/JakeWharton/scalpel)