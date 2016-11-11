//package com.exyui.android.debugbottle.components.floating
//
//import android.content.Intent
//import android.os.IBinder
//import com.exyui.android.debugbottle.components.RunningFeatureMgr
//
///**
// * Created by yuriel on 9/19/16.
// */
//@Deprecated(
//        message = "use __TestingRunnerBubble instead",
//        replaceWith = ReplaceWith("__TestingRunnerBubble", "com.exyui.android.debugbottle.components.bubbles.services.__TestingRunnerBubble")
//)
//internal class FloatingTestRunnerService: DTBaseFloatingService() {
//    override val floatingViewMgr: DTDragFloatingViewMgr = FloatingTestRunnerViewMgr
//    override fun onBind(intent: Intent?): IBinder? = null
//
//    override fun createView() {
//        super.createView()
//        RunningFeatureMgr.add(RunningFeatureMgr.STRESS_TEST_RUNNER)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        RunningFeatureMgr.remove(RunningFeatureMgr.STRESS_TEST_RUNNER)
//    }
//}