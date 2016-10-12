<a href="https://travis-ci.org/kiruto/debug-bottle" title="Latest push build on default branch: created">
  <img src="https://travis-ci.org/kiruto/debug-bottle.svg?branch=1.0.1" alt="build:created">
</a>
<a href="https://mvnrepository.com/artifact/com.exyui.android/debug-bottle-runtime" name="status-images" title="Latest version on maven-central">
  <img src="https://img.shields.io/maven-central/v/com.exyui.android/debug-bottle-runtime.svg?maxAge=2592000" alt="version:maven-central">
</a>

[🇨🇳 中文](README-ZH.md) / [🇯🇵日本語](README-JP.md) / [🇬🇧 English](README.md)

# 🍼デバッグボットル
アンドロイド Java / Kotlin 開発者ツール

- [CHANGELOG](CHANGELOG.md)
- [TODO](TODO.md)

DEMOは今Google Playでダウンロードできます:

<a href="https://play.google.com/store/apps/details?id=me.chunyu.dev.yuriel.kotdebugtool"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge-border.png" width="300" /></a>

[<img src="screenshots/introduction.gif" width="225" height="400">](screenshots/raw/introduction.gif)
[<img src="screenshots/quick-toggles.png"/>](screenshots/raw/quick-toggles.png)
[<img src="screenshots/features-2.png"/>](screenshots/raw/features-2.png)

## 実装されている機能

- [便利なHTTPスニファ](#アプリのネットワークトラフィックを記録)
- [実装されるアクティビティはいつでも３D化できます](#アクティビティを3D化する)
- [SharedPreferencesをランタイム時に簡単的に編集できます](#SharedPreferencesを編集する)
- [ランタイム時にStrictモードをオン・オブにするのを可能とさせてます](#Strictモードで開発)
- [アプリがクラッシュするとログが保存されます](#クラッシュログ)
- [早くメモリリークを発見できて、そして回避できます](#leak-canaryを使用する)
- [UIスレッドのフリーズを起こすコードを見つけます](#uiフリーズを排除する)
- [いずれのアクティビティを簡単にアクセスできます](#アクティビティをいつでも起動する)

#### アプリのネットワークトラフィックを記録
この機能は、デバッグボットルがHTTPリクエストとリスポンスをログファイルに書き込む。記録されたログファイルはいつでも見えます。

[<img src="screenshots/network-sniffer-1.png"/>](screenshots/raw/network-sniffer-1.png)
[<img src="screenshots/network-sniffer-2.png"/>](screenshots/raw/network-sniffer-2.png)

#### アクティビティを3D化する
View のデバッグ用途として、画面をグリグリすると 3DCG みたいに View のヒエラルキーを見ることができるようになるツールです。

[<img src="screenshots/scalpel-view.png"/>](screenshots/raw/network-sniffer-2.png)
[<img src="screenshots/scalpel-view.gif" width="225" height="400" />](screenshots/raw/scalpel-view.gif)

#### SharedPreferencesを編集する
アプリが使ってるすべてのSharedPreferencesをランタイム時に編集する。

[<img src="screenshots/shared-preferences-editor-1.png"/>](screenshots/raw/network-sniffer-2.png)
[<img src="screenshots/shared-preferences-editor-2.png"/>](screenshots/raw/network-sniffer-2.png)

#### Strictモードで開発
StrictModeはアプリケーションの動作をもっさりさせる原因となる、 ディスクやネットワークへのアクセスを検知するための仕組みです。使う方とかを[オフィシャルサイト](https://developer.android.com/reference/android/os/StrictMode.html)で見えます。

#### クラッシュログ
アプリがクラッシュするとログが保存され、デバッグボトルからログを取得して解析することができます。

[<img src="screenshots/crash.gif" width="225" height="400">](screenshots/raw/crash.gif)

#### Leak Canaryを使用する
Squareのメモリリークを検出するライブラリ Leak Canary を通じて、メモリリークを調査できます。デバッグボトルはすでにLeak Canaryを含んでいるから、その機能は使えます。もっと詳しくは[こちらで](https://github.com/square/leakcanary/wiki/FAQ)。

#### UIフリーズを排除する
一旦UIフリーズが発生となると、デバッグボトルはノティファイで通知して、同じ時にログファイルを作成します。開発者はログファイルで、フリーズの原因を知り、排除するのは可能となります。

[<img src="screenshots/ui-blocks.png"/>](screenshots/raw/network-sniffer-2.png)
[<img src="screenshots/block-canary-demo.gif" width="225" height="400" />](screenshots/raw/block-canary-demo.gif)

#### アクティビティをいつでも起動する
デバッグボトルはシンプルなエントリーを提供し、アクティビティやRunnableはそのエントリーを通じて起動できます。エントリーは３つあります：
* すべてのアクティビティエントリー
* カストマイズインテントエントリー
* カストマイズRunnableエントリー

[<img src="screenshots/all-activities.png"/>](screenshots/raw/network-sniffer-2.png)
[<img src="screenshots/run-activity-with-intent.gif" width="225" height="400" />](screenshots/raw/run-activity-with-intent.gif)

## 構築するには

#### 1. Gradleプロジェクトファイルを構成する
まずはスナップショットリポジトリを配置します。
```gradle
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```
アプリのプライマリモジュールによるGradleファイルに依頼環境を導入します。

```gradle
dependencies {
    debugCompile 'com.exyui.android:debug-bottle-runtime:1.0.0EAP-beta'

    // Javaの場合はこうして構築します
    releaseCompile 'com.exyui.android:debug-bottle-noop-java:1.0.0EAP-beta'
    testCompile 'com.exyui.android:debug-bottle-noop-java:1.0.0EAP-beta'

    // Kotlinの場合はこうして構築します
    releaseCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.0EAP-beta'
    testCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.0EAP-beta'

    compile 'com.android.support:appcompat-v7:23.2.0+'
    compile 'com.android.support:support-v4:23.2.0+'
}
```

Specially, Debug Bottle not only support API 23+, but also 22. To support API 22, please add dependencies like this:
```gradle
dependencies {
    debugCompile 'com.exyui.android:debug-bottle-runtime:1.0.0EAP-support22-beta'

    // Javaの場合はこうして構築します
    releaseCompile 'com.exyui.android:debug-bottle-noop-java:1.0.0EAP-support22-beta'
    testCompile 'com.exyui.android:debug-bottle-noop-java:1.0.0EAP-support22-beta'

    // Kotlinの場合はこうして構築します
    releaseCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.0EAP-support22-beta'
    testCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.0EAP-support22-beta'

    compile 'com.android.support:appcompat-v7:22+'
}
```

To support API 23, add dependencies like this:
```gradle
dependencies {
    debugCompile 'com.exyui.android:debug-bottle-runtime:1.0.0EAP-support23-beta'

    // Javaの場合はこうして構築します
    releaseCompile 'com.exyui.android:debug-bottle-noop-java:1.0.0EAP-support23-beta'
    testCompile 'com.exyui.android:debug-bottle-noop-java:1.0.0EAP-support23-beta'

    // Kotlinの場合はこうして構築します
    releaseCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.0EAP-support23-beta'
    testCompile 'com.exyui.android:debug-bottle-noop-kotlin:1.0.0EAP-support23-beta'

    compile 'com.android.support:appcompat-v7:23+'
}
```

#### 2. Manifestに加入
Manifestにデバッグボトルのプライマリアクティビティを加入します。
```xml
<activity
    android:name="com.exyui.android.debugbottle.components.DTDrawerActivity"
    android:theme="@style/Theme.AppCompat.Light"
    android:label="ほげほげツール"/>
```
「ほげほげツール」はこのツールの名前、欲しいネームを付けてよろしい、ただし空きでは行けません。

#### 3. Applicationにデバッグボトルを注入
まずは「BlockCanaryContext」を実装します。
```java
public class AppBlockCanaryContext extends BlockCanaryContext {...}
```

そしてデバッグボトルをApplicationに注入します。
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
Kotlinを使ってる場合は、以下の方法で注入します。
```kotlin
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        DTInstaller.install(this)
            .setBlockCanary(AppBlockCanaryContext(this))
            .setOkHttpClient(httpClient)
            .setInjector("your.package.injector.ContentInjector")
            .setPackageName("your.package")
            .enable()
            .run()
    }
}
```

## リンク先
* [Leak Canary](https://github.com/square/leakcanary)
* [Android Performance Monitor](https://github.com/markzhai/AndroidPerformanceMonitor)
* [Scalpel](https://github.com/JakeWharton/scalpel)

## License

```
Debug Bottle

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


Debug Bottle required features are based on or derives from projects below:
- Apache License 2.0
  - [Android Performance Monitor](https://raw.githubusercontent.com/markzhai/AndroidPerformanceMonitor/master/LICENSE)
  - [Leak Canary](https://raw.githubusercontent.com/square/leakcanary/master/LICENSE.txt)
  - [Scalpel](https://raw.githubusercontent.com/JakeWharton/scalpel/master/LICENSE.txt)
  - [Bubbles for Android](https://raw.githubusercontent.com/txusballesteros/bubbles-for-android/master/LICENSE)
  - [Takt](https://raw.githubusercontent.com/wasabeef/Takt/master/LICENSE)
