package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 화면 상단 점수 표시 객체.
// 1주차에는 LabelUtil 텍스트로 표시하고, 7주차에 ImageNumber + 폰트 시트로 교체 예정.
//
// 점수 값 자체는 MainScene 이 들고 있고, ScoreLabel 은 매 프레임 그것을 읽어서 그린다.
// 이렇게 두면 CollisionChecker 등 점수를 갱신하는 쪽은 ScoreLabel 의 존재를 몰라도 된다.
class ScoreLabel(private val gctx: GameContext) : IGameObject {
    private val label = LabelUtil(TEXT_SIZE, Color.WHITE, Paint.Align.LEFT)

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        val scene = gctx.scene as? MainScene ?: return
        label.draw(canvas, "Score: ${scene.score}", MARGIN_X, MARGIN_Y)
    }

    companion object {
        private const val TEXT_SIZE = 60f
        private const val MARGIN_X = 30f
        private const val MARGIN_Y = 80f
    }
}
