package io.github.takusan23.batteryapplaunchercomplication

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent

/** ホームボタンを押すだけのユーザー補助サービス */
class HomeButtonAccessibilityService : AccessibilityService() {

    /** ホームボタンを押してほしいことを受け取るブロードキャスト */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                // ホームボタンを押す
                DOWN_HOME_BUTTON -> performGlobalAction(GLOBAL_ACTION_HOME)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver(broadcastReceiver, IntentFilter().apply {
            addAction(DOWN_HOME_BUTTON)
        })
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