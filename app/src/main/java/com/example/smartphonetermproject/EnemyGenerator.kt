package com.example.smartphonetermproject

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.random.Random

class EnemyGenerator(
    private val gctx: GameContext,
) : IGameObject {
    private var enemyTime = GEN_INTERVAL

    override fun update(gctx: GameContext) {
        enemyTime -= gctx.frameTime
        if (enemyTime > 0f) return
        enemyTime = GEN_INTERVAL
        spawn()
    }

    private fun spawn() {
        val scene = gctx.scene as? MainScene ?: return
        // 검증 단계용 임시 single-type spawn. SUICIDE/RANGED/SPLIT 중 한 줄만 바꿔 단독 검증.
        // random 복원은 3주차 #5 에서.
        val type = Enemy.Type.SPLIT
        val margin = type.width / 2f
        val x = margin + Random.nextFloat() * (gctx.metrics.width - 2 * margin)
        val enemy = Enemy.get(gctx, x, type)
        scene.world.add(enemy, MainScene.Layer.ENEMY)
    }

    override fun draw(canvas: Canvas) {
        // EnemyGenerator 는 화면에 직접 보이는 오브젝트가 아니라
        // "언제 적을 만들지"만 판단하는 담당자이므로 그릴 것은 없다.
    }

    companion object {
        const val GEN_INTERVAL = 1.0f
    }
}
