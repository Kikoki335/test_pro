package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 화면 좌하단의 HP HUD — 시안(02_normal_stage.png) 기준 배치.
// "HP" 텍스트 라벨 위에 가로 막대 게이지를 그리고, 게이지 색은 placeholder 단계에서는 초록 단색으로 둔다.
// (README §2.5 의 "빨강↔초록 동적 색상" 은 7주차 그래픽 교체 시 검토)
class PlayerHpHud(private val gctx: GameContext) : IGameObject {
    private val gauge = Gauge(GAUGE_THICKNESS, Color.GREEN, Color.argb(180, 0, 0, 0))
    private val label = LabelUtil(LABEL_TEXT_SIZE, Color.WHITE, Paint.Align.LEFT)

    private val gaugeWidth = gctx.metrics.width * 0.35f
    private val gaugeX = MARGIN_LEFT
    private val gaugeY = gctx.metrics.height - MARGIN_BOTTOM
    // "HP" 라벨은 게이지 바로 위에 배치한다.
    private val labelX = MARGIN_LEFT
    private val labelY = gaugeY - LABEL_OFFSET_FROM_GAUGE

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        val scene = gctx.scene as? MainScene ?: return
        val player = scene.player
        if (player.maxLife <= 0) return
        val progress = player.life.toFloat() / player.maxLife
        label.draw(canvas, "HP", labelX, labelY)
        gauge.draw(canvas, gaugeX, gaugeY, gaugeWidth, progress)
    }

    companion object {
        // Gauge.thickness 는 scale 적용 후 1.0 단위 stroke width.
        // 0.04 × (900 × 0.35 = 315) ≈ 12.6px
        private const val GAUGE_THICKNESS = 0.04f
        private const val MARGIN_LEFT = 30f
        private const val MARGIN_BOTTOM = 60f
        private const val LABEL_TEXT_SIZE = 36f
        private const val LABEL_OFFSET_FROM_GAUGE = 14f
    }
}
