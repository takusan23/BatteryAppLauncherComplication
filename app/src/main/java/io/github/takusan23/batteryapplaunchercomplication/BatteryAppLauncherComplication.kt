package io.github.takusan23.batteryapplaunchercomplication

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.BatteryManager
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService

/** 電池残量を表示して、押したらアプリ一覧を開く コンプリケーション */
class BatteryAppLauncherComplication : SuspendingComplicationDataSourceService() {

    /** プレビューの際に呼び出される */
    override fun getPreviewData(type: ComplicationType): ComplicationData {
        // RangedValueComplicationData は 丸いプログレスバーみたいなやつ
        return RangedValueComplicationData.Builder(
            value = 75f,
            min = 0f,
            max = 100f,
            contentDescription = createPlainTextComplication("電池残量とアプリランチャー")
        ).also { build ->
            build.setText(createPlainTextComplication("75%"))
            build.setMonochromaticImage(createMonochromeIcon(R.drawable.icon_battery_4_bar))
        }.build()
    }

    /** 実際のウォッチフェイスから呼び出される */
    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        request.complicationType
        // 実際の電池残量を取得する
        val batteryLevel = getBatteryLevel().toFloat()
        return RangedValueComplicationData.Builder(
            value = batteryLevel,
            min = 0f,
            max = 100f,
            contentDescription = createPlainTextComplication("電池残量とアプリランチャー")
        ).also { build ->
            build.setText(createPlainTextComplication("${batteryLevel.toInt()}%"))
            build.setMonochromaticImage(createMonochromeIcon(R.drawable.icon_battery_4_bar))
            // コンプリケーションを押したときの PendingIntent 。ホームボタンを押すだけのユーザー補助サービスへブロードキャストを送信している
            build.setTapAction(PendingIntent.getBroadcast(this, 4545, Intent(HomeButtonAccessibilityService.DOWN_HOME_BUTTON), PendingIntent.FLAG_IMMUTABLE))
        }.build()
    }

    /** [String]から[PlainComplicationText]を作る */
    private fun createPlainTextComplication(string: String) = PlainComplicationText.Builder(string).build()

    /** アイコンのリソースIDから[MonochromaticImage]を作る */
    private fun createMonochromeIcon(iconRes: Int) = MonochromaticImage.Builder(
        image = Icon.createWithResource(this, iconRes)
    ).build()

    /** 電池残量を取得 */
    private fun getBatteryLevel(): Int {
        val powerManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return powerManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

}