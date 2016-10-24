# 简介
在Android开发过程中，会遇到各种小问题，熟练的开发者有自己解决小问题的套路。为了节约解决小问题的成本，我开发了Debug Bottle：一套小问题的解决方案。
## 什么是Debug Bottle
Debug Bottle是一套非侵入式的App开发插件。开发者只需简单添加依赖后，便可以使用它来调试自己的App。开发者可以在Gradle工程中添加Debug，Test和Release的依赖，仅Debug包中使用Bottle带来的功能，对Test和Release包没有任何影响。Debug Bottle仅在运行时提供调试功能，不对编译时产生影响。
## Debug Bottle能解决哪些问题
Debug Bottle希望能帮助开发者解决以下问题：

1. 调试网络接口时能帮助捕获Http及Https数据包；
2. 当程序崩溃时能捕捉崩溃日志，自动保存和分享；
2. 检查哪些地方存在内存泄漏；
2. App运行卡顿时能帮助看出卡在哪行，哪些代码效率阻塞主线程；
3. 只在当前App中，随时打开和关闭严格模式；
3. 实时显示刷新帧数；
4. 直观看出当前Activity布局的层级效果；
5. 直接进入任何一个Activity，当前置Activity没有准备好，或者入口尚未完成时，也能打开想要的Activity；
6. 直接修改任意SharedPreferences；
7. 当不确定自己代码是否有问题时，实验和执行任何一个函数；
8. 当不确定自己Activity是否有问题时，可以Hack当前的代码；
9. 定制小工具，可以在任何界面中快捷执行；

以上均是我开发中常遇到的问题，目前使用Debug Bottle能够做到解决问题。今后会加入的功能，可以参考TODO.md。
## Debug Bottle的使用场景
目前Debug Bottle主要用在开发阶段，在开发和压力测试中反馈Bug。
- 团队协作时排查问题
- 独立开发时将“开发Debug代码”和“正式功能代码”分开，解耦
- 基本功能开发完成后，进行压力测试并拿到测试结果进行分析，修补
- 人工测试发现Bug时生成日志并分享给开发者

## 使用Debug Bottle的前置条件
Debug Bottle完全开源，基于Apache 2.0开源协议。使用Debug Bottle要求你的项目是由Gradle构建的，且Target Sdk Version在22或以上，使用appcompat-v7支持的项目。

## Debug Bottle的开源许可
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
