package com.example.smartphonetermproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 레벨업 시 MainScene 위에 push 되는 overlay Scene.
// SceneStack 이 stack top 만 update/draw 하므로 push 되는 즉시 MainScene 의 게임 시간/입력이 멈춘다.
//
// 카드 3장은 MainScene.cardPool.pickThree() 가 추출. stat 카드는 텍스트만, WeaponCard 는 무기 sprite +
// 등급별 stroke 색 (희귀 파랑 / 영웅 보라). RewardCard.apply 가 보상 적용, MainScene.cardPool.consume
// 이 풀 갱신 (무기 카드만 제거, stat 카드는 그대로).
class LevelUpScene(
    gctx: GameContext,
    private val mainScene: MainScene,
    private val cards: List<RewardCard>,
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

    // 무기 카드 sprite 캐시 — 같은 Scene 안에서 여러 카드가 같은 sprite 공유 가능.
    private val weaponBitmaps = mutableMapOf<Int, Bitmap>()
    private val weaponSpriteRect = RectF()

    override fun update(gctx: GameContext) {
        // overlay 라 자체 update 할 게 없다.
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(0)
        canvas.drawRect(0f, 0f, gctx.metrics.width.toFloat(), gctx.metrics.height.toFloat(), backgroundPaint)

        canvas.drawText("Level Up!", titleX, titleY, titlePaint)

        for ((i, rect) in cardRects.withIndex()) {
            // 인덱스 ≥ cards.size 인 빈 슬롯은 안전 가드 — pickThree 가 항상 3장 보장하므로 일반적으로
            // 안 들어옴.
            val card = cards.getOrNull(i) ?: continue

            // 카드 fill — 모든 카드 같은 어두운 색.
            canvas.drawRoundRect(rect, CARD_CORNER, CARD_CORNER, cardFillPaint)
            // stroke 색은 카드 종류별 — 무기는 등급 색, stat 은 cyan.
            cardStrokePaint.color = when (card) {
                is WeaponCard -> card.grade.cardColor
                else -> STAT_STROKE_COLOR
            }
            canvas.drawRoundRect(rect, CARD_CORNER, CARD_CORNER, cardStrokePaint)

            if (card is WeaponCard) {
                drawWeaponCard(canvas, rect, card)
            } else {
                // stat 카드 — 텍스트 두 줄 (제목 위, 효과 아래).
                canvas.drawText(card.title, rect.centerX(), rect.centerY() - CARD_TEXT_SIZE / 2f, cardTextPaint)
                canvas.drawText(card.effect, rect.centerX(), rect.centerY() + CARD_TEXT_SIZE, cardTextPaint)
            }
        }
    }

    private fun drawWeaponCard(canvas: Canvas, rect: RectF, card: WeaponCard) {
        // 카드 위쪽 60% 영역에 무기 sprite, 아래쪽 40% 에 텍스트 두 줄.
        val bmp = weaponBitmaps.getOrPut(card.weapon.cardSpriteResId) {
            gctx.res.getBitmap(card.weapon.cardSpriteResId)
        }
        val spriteSize = CARD_WIDTH * 0.65f
        val spriteCenterY = rect.top + CARD_HEIGHT * 0.30f
        weaponSpriteRect.set(
            rect.centerX() - spriteSize / 2f,
            spriteCenterY - spriteSize / 2f,
            rect.centerX() + spriteSize / 2f,
            spriteCenterY + spriteSize / 2f,
        )
        canvas.drawBitmap(bmp, null, weaponSpriteRect, null)

        // 텍스트 — 등급+무기 이름 (희귀 샷건 / 영웅 레이저 ...) + "장착".
        val titleY = rect.top + CARD_HEIGHT * 0.72f
        val effectY = rect.top + CARD_HEIGHT * 0.88f
        canvas.drawText(card.title, rect.centerX(), titleY, cardTextPaint)
        canvas.drawText(card.effect, rect.centerX(), effectY, cardTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_UP) return true
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        for ((i, rect) in cardRects.withIndex()) {
            if (rect.contains(pt.x, pt.y)) {
                onCardSelected(i)
                return true
            }
        }
        return true
    }

    private fun onCardSelected(idx: Int) {
        val card = cards.getOrNull(idx) ?: return
        card.apply(mainScene.player)
        mainScene.cardPool.consume(card)
        mainScene.player.levelUp()
        pop()
    }

    companion object {
        private const val TITLE_TEXT_SIZE = 90f
        private const val CARD_WIDTH = 230f
        private const val CARD_HEIGHT = 320f
        private const val CARD_GAP = 30f
        private const val CARD_CORNER = 24f
        private const val CARD_TEXT_SIZE = 48f
        private val STAT_STROKE_COLOR = Color.rgb(34, 211, 238)
    }
}
