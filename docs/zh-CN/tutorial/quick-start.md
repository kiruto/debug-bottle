[目录](../README.md)>快速使用

# 快速使用

## 添加依赖
如果你的工程使用Gradle构建，可以按照本页方法添加Debug Bottle依赖。以1.0.4版本为例：

```gradle
dependencies {
    // Debug Bottle的主体内容，仅在Debug时被编译进去
    debugCompile 'com.exyui.android:debug-bottle-runtime:1.0.4'
    
    // 使用不同support包，选择正确的依赖
    // debugCompile 'com.exyui.android:debug-bottle-runtime:1.0.4-support22'
    // debugCompile 'com.exyui.android:debug-bottle-runtime:1.0.4-support23'

    // 如果工程使用的是Java语言，添加以下依赖
    releaseCompile 'com.exyui.android:debug-bottle-noop-java:1.0.4'
    testCompile 'com.exyui.android:debug-bottle-noop-java:1.0.4'

    // 如果工程实用的是Kotlin，添加以下依赖
    releaseCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.4'
    testCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.4'

    // 加入你的support包支持
    compile 'com.android.support:appcompat-v7:23.2.0+'
    // 或使用v22版
    // compile 'com.android.support:appcompat-v7:22+'
}
```

## 编辑Manifest

添加这些内容到Manifest
```xml
<activity
    android:name="com.exyui.android.debugbottle.components.DTDrawerActivity"
    android:theme="@style/Theme.AppCompat.Light"
    android:label="The Name You Like"/>
```
这是Debug Bottle的主要入口。label属性表示图标名称，可以随意修改。若没有label属性，则Debug Bottle图标名称和你的App图标名称一致。

## 嵌入App

首先，应实现BlockCanaryContext接口：
```java
// 实现各种上下文，包括应用标示符，用户uid，网络类型，卡慢判断阙值，Log保存位置等
public class AppBlockCanaryContext extends BlockCanaryContext {
    //...
}
```
需要实现的方法可以参照注释：[BlockCanaryContext](../../../blockcanary/src/main/kotlin/com/exyui/android/debugbottle/ui/BlockCanaryContext.kt)
实现示例：[AppBlockCanaryContext](../../../demo/src/main/kotlin/me/chunyu/dev/yuriel/kotdebugtool/AppBlockCanaryContext.kt)

现在可以将Debug Bottle注入到App中：
```java
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        DTInstaller.install(this)
            .setBlockCanary(new AppBlockCanaryContext(this))
            .setOkHttpClient(httpClient)
            .setInjector("your.package.injector.ContentInjector")
            .setPackageName("your.package")
            .enable()
            .run();
    }
}
```
现在完成了Debug Bottle的嵌入。