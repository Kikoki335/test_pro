package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 상단 중앙의 게임 경과 시간 표시.
// 게임 시작 (Scene 진입) 시점부터 매 프레임 frameTime 을 누적해 mm:ss 형식으로 그려준다.
// 사양 (README §1) 상 1분(=01:00) 도달 시점이 보스 진입 가능 시점이 되며,
// 진입 선택 Scene 분기/UI 는 5주차 sub-task 에서 이 카운터 값을 읽어 처리한다.
// 시안(02_normal_stage.png) 의 둥근 사각형 + 주황 테두리/텍스트 + 어두운 fill 패턴을 따른다.
class BossTimerHud(private val gctx: GameContext) : IGameObject {
    private var elapsedSec = 0f
    private val boxRect = RectF(
        gctx.metrics.width / 2f - BOX_WIDTH / 2f,
        BOX_TOP,
        gctx.metrics.width / 2f + BOX_WIDTH / 2f,
        BOX_TOP + BOX_HEIGHT,
    )

    private val boxFillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.argb(160, 0, 0, 0)
        isAntiAlias = true
    }
    private val boxStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.rgb(255, 140, 60)
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color = Color.rgb(255, 170, 80)
        textSize = TEXT_SIZE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
    }
    // 텍스트의 baseline 을 박스 정중앙에 맞추기 위해 ascent/descent 를 미리 계산한다.
    private val textBaselineY: Float = run {
        val fm = textPaint.fontMetrics
        boxRect.centerY() - (fm.ascent + fm.descent) / 2f
    }

    override fun update(gctx: GameContext) {
        elapsedSec += gctx.frameTime
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(boxRect, CORNER_RADIUS, CORNER_RADIUS, boxFillPaint)
        canvas.drawRoundRect(boxRect, CORNER_RADIUS, CORNER_RADIUS, boxStrokePaint)
        val totalSec = elapsedSec.toInt()
        val min = totalSec / 60
        val sec = totalSec % 60
        val text = "%02d:%02d".format(min, sec)
        canvas.drawText(text, boxRect.centerX(), textBaselineY, textPaint)
    }

    companion object {
        private const val BOX_WIDTH = 200f
        private const val BOX_HEIGHT = 70f
        private const val BOX_TOP = 40f
        private const val CORNER_RADIUS = 18f
        private const val TEXT_SIZE = 50f
    }
}
