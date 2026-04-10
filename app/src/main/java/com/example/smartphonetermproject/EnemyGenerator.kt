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
        // SPLIT_MINION 은 SPLIT 본체가 죽을 때 분열로만 등장하므로 일반 spawn 풀에서 제외.
        // 나머지 SUICIDE / RANGED / SPLIT 중 균등 random.
        val type = SPAWNABLE_TYPES.random()
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
        // entries 를 매 spawn 마다 다시 필터링하지 않도록 한 번만 계산해 둔다.
        private val SPAWNABLE_TYPES = Enemy.Type.entries.filter { it != Enemy.Type.SPLIT_MINION }
    }
}
