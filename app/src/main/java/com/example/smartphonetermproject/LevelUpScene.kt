package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 레벨업 시 MainScene 위에 push 되는 overlay Scene.
// SceneStack 이 stack top 만 update/draw 하므로 push 되는 즉시 MainScene 의 게임 시간/입력이 멈춘다.
// (Pause Scene 과 같은 패턴 — Scene.kt 의 onPause/onResume 흐름)
//
// 카드 3장 placeholder — 4주차 #2 단계에서는 단순히 "Level Up!" 카드 3장을 띄우고, 어느 카드를
// 선택해도 player.levelUp() 만 호출 후 pop 한다. 실제 보상 종류 (무기/스탯) 는 4주차 #3~#5 에서
// WeaponRegistry / Stat 보상 카드를 만들 때 카드 풀로 추가된다.
class LevelUpScene(
    gctx: GameContext,
    // MainScene 참조를 받아 두는 이유 — pop 후에도 게임이 그대로 진행되어야 하므로 별도 Scene 으로
    // 교체하지 않고 같은 MainScene 의 player 에게 보상만 적용한다. gctx.scene 은 push 시점에 self
    // (LevelUpScene) 가 되므로 거기서 player 를 읽으면 안 됨.
    private val mainScene: MainScene,
) : Scene(gctx) {

    private val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.argb(160, 0, 0, 0)
    }
    private val cardFillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.rgb(28, 36, 56)
        isAntiAlias = true
    }
    private val cardStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.rgb(34, 211, 238)
        isAntiAlias = true
    }
    private val titlePaint = Paint().apply {
        color = Color.rgb(34, 211, 238)
        textSize = TITLE_TEXT_SIZE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
    }
    private val cardTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = CARD_TEXT_SIZE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val titleX = gctx.metrics.width / 2f
    private val titleY = gctx.metrics.height * 0.30f

    private val cardRects: List<RectF> = run {
        val totalWidth = CARD_WIDTH * 3 + CARD_GAP * 2
        val startX = (gctx.metrics.width - totalWidth) / 2f
        val cardY = gctx.metrics.height * 0.42f
        (0..2).map { i ->
            val left = startX + i * (CARD_WIDTH + CARD_GAP)
            RectF(left, cardY, left + CARD_WIDTH, cardY + CARD_HEIGHT)
        }
    }

    // 카드별 (제목, 효과 설명) 텍스트. 인덱스 = onCardSelected 의 분기 인덱스.
    private val cardLabels = listOf(
        "공격력" to "x2",
        "공속" to "+30%",
        "치명타" to "+50%",
    )

    override fun update(gctx: GameContext) {
        // overlay 라 자체 update 할 게 없다. SceneStack 이 stack top 만 update 하므로 그 아래 MainScene
        // 의 update 도 자동으로 멈춘다 (게임 정지 = push 의 부수 효과).
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(0)
        // MainScene 의 마지막 화면 위에 그릴 수 있도록 stack 이 어떻게 그리는지 확인 — SceneStack 이
        // 한 Scene 만 그리므로 (top), 그 아래 MainScene 은 안 보인다. 그래서 우리가 직접 반투명 검정만
        // 채우고 그 위에 카드를 그리는 구조로 둔다 (DragonFlight 도 Pause overlay 시 같은 패턴).
        canvas.drawRect(0f, 0f, gctx.metrics.width.toFloat(), gctx.metrics.height.toFloat(), backgroundPaint)

        canvas.drawText("Level Up!", titleX, titleY, titlePaint)

        for ((i, rect) in cardRects.withIndex()) {
            canvas.drawRoundRect(rect, CARD_CORNER, CARD_CORNER, cardFillPaint)
            canvas.drawRoundRect(rect, CARD_CORNER, CARD_CORNER, cardStrokePaint)
            val (title, effect) = cardLabels[i]
            // 제목은 카드 위쪽 1/3, 효과는 아래쪽 2/3 위치 — 두 줄 시각 분리.
            canvas.drawText(title, rect.centerX(), rect.centerY() - CARD_TEXT_SIZE / 2f, cardTextPaint)
            canvas.drawText(effect, rect.centerX(), rect.centerY() + CARD_TEXT_SIZE, cardTextPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // ACTION_UP 만 카드 선택으로 인정 — 누른 채 드래그하다 빠져 나가면 선택 취소.
        if (event.actionMasked != MotionEvent.ACTION_UP) return true
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        for ((i, rect) in cardRects.withIndex()) {
            if (rect.contains(pt.x, pt.y)) {
                onCardSelected(i)
                return true
            }
        }
        // 카드 밖 터치는 무시 — 이 화면에서 사용자가 카드를 선택해야 게임이 재개되도록.
        return true
    }

    private fun onCardSelected(idx: Int) {
        val player = mainScene.player
        when (idx) {
            0 -> player.attackMul *= ATK_BOOST
            1 -> player.fireRateMul *= RATE_BOOST
            2 -> player.critRate = (player.critRate + CRIT_BOOST).coerceAtMost(1f)
        }
        player.levelUp()
        pop()
    }

    companion object {
        private const val TITLE_TEXT_SIZE = 90f
        private const val CARD_WIDTH = 230f
        private const val CARD_HEIGHT = 320f
        private const val CARD_GAP = 30f
        private const val CARD_CORNER = 24f
        private const val CARD_TEXT_SIZE = 56f
        // 카드 보상 적용 배수 — 사용자 결정으로 눈에 띄게 큰 폭. 한 번 받으면 효과 즉시 체감.
        // 누적 시 4번 ×2 = 16배, 4번 ×1.3 = 약 2.86배. 치명타는 50% 씩 누적 (1.0 캡 = 2회면 max).
        private const val ATK_BOOST = 2.0f
        private const val RATE_BOOST = 1.3f
        private const val CRIT_BOOST = 0.5f
    }
}
