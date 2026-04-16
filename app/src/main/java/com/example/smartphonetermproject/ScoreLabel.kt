package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 화면 좌상단 점수 표시 객체.
//
// "실제 값" 과 "화면에 보이는 값" 을 분리해 점수가 갑자기 늘어나도 한 번에 안 바뀌고
// 조금씩 따라가며 증가하는 애니메이션을 만든다 (a2dg `ImageNumber.update` 의 lerp 규칙 그대로).
//   - 실제 값  : `MainScene.score` (CollisionChecker 가 즉시 갱신)
//   - 표시 값  : 자체 `displayScore` (update() 마다 점진 보간)
//
// 텍스트 그리기는 a2dg `LabelUtil` (Paint.drawText) 그대로. 7주차 #5 에서 폰트 시트 + ImageNumber 로
// 교체될 예정이지만 그때도 displayValue 패턴은 그대로 유지된다.
class ScoreLabel(private val gctx: GameContext) : IGameObject {
    private val label = LabelUtil(TEXT_SIZE, Color.WHITE, Paint.Align.LEFT)
    private var displayScore = 0

    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return
        val target = scene.score
        val diff = target - displayScore
        if (diff == 0) return
        // 차이가 작으면 1 씩, 크면 10분의 1 씩 따라잡는다 — 큰 점수 변화도 너무 오래 걸리지 않게.
        displayScore += when {
            diff in -9..-1 -> -1
            diff in 1..9 -> 1
            else -> diff / 10
        }
    }

    override fun draw(canvas: Canvas) {
        label.draw(canvas, "Score: $displayScore", MARGIN_X, MARGIN_Y)
    }

    companion object {
        private const val TEXT_SIZE = 60f
        private const val MARGIN_X = 30f
        private const val MARGIN_Y = 80f
    }
}
