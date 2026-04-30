package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 화면 하단 debug HUD — Player 의 stat 보상 누적 수치 (attackMul / fireRateMul / bulletCount) 를
// 표시해 LevelUpScene 에서 카드 고른 효과가 즉시 시각으로 확인되게 한다.
//
// 스타일은 framework GameView 의 drawDebugInfo 와 통일 — textSize 40, MONOSPACE, 흰색.
// 위치만 좌상단 → 하단 (PlayerHpHud / ExpLabel 줄 아래) 으로 이동 (사용자 결정 — 좌상단 fps/grid
// 영역과 시각적으로 분리되어 보상 정보임을 명확히).
class DebugStatLabel(private val gctx: GameContext) : IGameObject {
    private val label = LabelUtil(TEXT_SIZE, Color.WHITE, Paint.Align.LEFT, Typeface.MONOSPACE)

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        val scene = gctx.scene as? MainScene ?: return
        val player = scene.player
        val text = "ATK x%.2f RATE x%.2f COUNT %d".format(
            player.attackMul,
            player.fireRateMul,
            player.bulletCount,
        )
        label.draw(canvas, text, MARGIN_X, drawY)
    }

    private val drawY = gctx.metrics.height - BOTTOM_MARGIN

    companion object {
        // framework debug 와 같은 textSize / typeface 유지. 위치만 하단.
        private const val TEXT_SIZE = 40f
        // PlayerHpHud / ExpLabel 좌측 margin (30) 과 통일.
        private const val MARGIN_X = 30f
        // PlayerHpHud 의 gauge baseline (height - 60) 보다 더 아래에 한 줄. height - 14 = 1586,
        // textSize 40 의 글자 ascender ~30 → 글자 위쪽 ~1556, 게이지 끝 ~1546 와 10px 거리.
        private const val BOTTOM_MARGIN = 14f
    }
}
