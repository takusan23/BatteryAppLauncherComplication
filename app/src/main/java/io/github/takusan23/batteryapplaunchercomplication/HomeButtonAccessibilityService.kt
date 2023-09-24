package io.github.takusan23.batteryapplaunchercomplication

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat

/** ホームボタンを押すだけのユーザー補助サービス */
class HomeButtonAccessibilityService : AccessibilityService() {

    /** ホームボタンを押してほしいことを受け取るブロードキャスト */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                // ホームボタンを押す
                DOWN_HOME_BUTTON -> {
                    // xml で android:canPerformGestures を指定すると、
                    // りゅうず を回したときの感触フィードバック が貰えなくなるため、
                    // 実行時に canPerformGestures を指定する
                    // ただ、↑のメソッドが隠されているためリフレクションで呼び出す
                    val setCapabilities = AccessibilityServiceInfo::class.java
                        .methods
                        .first { it.name == "setCapabilities" }
                    setCapabilities.invoke(serviceInfo, AccessibilityServiceInfo.CAPABILITY_CAN_PERFORM_GESTURES)
                    // 再セットする
                    serviceInfo = serviceInfo
                    // ホームボタンを押す
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    // そして最後に戻す
                    setCapabilities.invoke(serviceInfo, 0)
                    serviceInfo = serviceInfo
                }
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        ContextCompat.registerReceiver(this, broadcastReceiver, IntentFilter().apply {
            addAction(DOWN_HOME_BUTTON)
        }, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // do nothing
    }

    override fun onInterrupt() {
        // do nothing
    }

    override fun onUnbind(intent: Intent?): Boolean {
        unregisterReceiver(broadcastReceiver)
        return super.onUnbind(intent)
    }

    companion object {
        /** ブロードキャストのIntentのAction */
        const val DOWN_HOME_BUTTON = "io.github.takusan23.batteryapplaunchercomplication.DOWN_HOME_BUTTON"
    }
}