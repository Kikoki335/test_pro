package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 좌하단 HP 게이지 오른쪽에 "EXP: N" 표시.
// ExpOrb 와 같은 cyan 색으로 두어 시각적 연결을 만든다 — orb 가 흡수되며 누적되는 값임을 직관적으로.
// ScoreLabel 과 같은 "값은 Player, 표시는 HUD" 패턴 (LabelUtil 그대로).
class ExpLabel(private val gctx: GameContext) : IGameObject {
    private val label = LabelUtil(TEXT_SIZE, Color.rgb(34, 211, 238), Paint.Align.LEFT)

    // PlayerHpHud 의 좌하단 게이지 (margin 30, width = metrics.width × 0.35) 끝에서 살짝 띄움.
    private val drawX = HP_GAUGE_X + gctx.metrics.width * HP_GAUGE_WIDTH_RATIO + GAP_FROM_GAUGE
    // HP 게이지의 baseline (metrics.height - margin) 과 같은 Y — 게이지 라인과 텍스트 baseline 정렬.
    private val drawY = gctx.metrics.height - HP_MARGIN_BOTTOM

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        val scene = gctx.scene as? MainScene ?: return
        // LevelUpScene 이 stack top 이라 update/draw 가 호출 안 되는 동안에도, 그 아래 MainScene
        // 의 draw 가 한 번 더 호출되지는 않는다 (SceneStack 이 top 만 그리므로). 그래서 여기서 본
        // exp 값은 항상 최신.
        val player = scene.player
        label.draw(canvas, "Lv.${player.level}  EXP ${player.exp}/${player.maxExp}", drawX, drawY)
    }

    companion object {
        private const val TEXT_SIZE = 40f
        // PlayerHpHud 의 좌표/크기와 동기 — 거기 상수가 바뀌면 여기도 같이 손봐야 한다.
        private const val HP_GAUGE_X = 30f
        private const val HP_GAUGE_WIDTH_RATIO = 0.35f
        private const val HP_MARGIN_BOTTOM = 60f
        private const val GAP_FROM_GAUGE = 25f
    }
}
