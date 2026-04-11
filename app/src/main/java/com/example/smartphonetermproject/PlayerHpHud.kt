package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 화면 하단 가운데에 Player HP 를 표시하는 HUD 객체.
// Player 자체에 그려도 되지만, 캐릭터를 따라 움직이지 않고 화면 고정 위치에 두기 위해 별도 IGameObject 로 분리한다.
//
// 색은 placeholder 단계라 빨강 단색으로 시작.
// README §2.5 의 "빨강 → 초록" 동적 색상은 7주차 그래픽 교체 시 검토.
class PlayerHpHud(private val gctx: GameContext) : IGameObject {
    private val gauge = Gauge(GAUGE_THICKNESS, Color.RED, Color.argb(180, 0, 0, 0))

    private val gaugeWidth = gctx.metrics.width * 0.6f
    private val gaugeX = (gctx.metrics.width - gaugeWidth) / 2f
    private val gaugeY = gctx.metrics.height - MARGIN_BOTTOM

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        val scene = gctx.scene as? MainScene ?: return
        val player = scene.player
        if (player.maxLife <= 0) return
        val progress = player.life.toFloat() / player.maxLife
        gauge.draw(canvas, gaugeX, gaugeY, gaugeWidth, progress)
    }

    companion object {
        // Gauge.thickness 는 scale 적용 후 1.0 단위 좌표계의 stroke width 다.
        // 즉 실제 화면 두께 = thickness × scale (= gaugeWidth) 가 된다.
        // 0.025 × 540 ≈ 13.5px 두께를 노린 값.
        private const val GAUGE_THICKNESS = 0.025f
        private const val MARGIN_BOTTOM = 60f
    }
}
