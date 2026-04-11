package com.example.smartphonetermproject

import android.app.Activity
import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// CollisionChecker 는 화면에 그려지지 않는 "심판" 역할의 GameObject 이다.
// CONTROLLER layer 에 올려 두면 매 프레임 update 만 받고 draw 는 빈 함수로 둔다.
//
// 한 update 안에서 다음 두 종류의 충돌을 검사한다:
//   1. Player ↔ Enemy   — 부딪히면 Player.life 1 감소, Enemy 제거
//   2. Bullet ↔ Enemy   — 부딪히면 Bullet 제거, Enemy.life 감소, 0 되면 Enemy 제거 + 점수 가산
//
// DragonFlight 4/9 패턴을 따라 ENEMY 외부 루프 시작점에서 player 와 먼저 비교하고,
// 충돌 시 그 enemy 는 bullet 검사를 건너뛰고 다음 enemy 로 넘어간다.
class CollisionChecker(private val gctx: GameContext) : IGameObject {
    private var gameOverTriggered = false

    override fun update(gctx: GameContext) {
        if (gameOverTriggered) return

        val scene = gctx.scene as? MainScene ?: return
        val player = scene.player

        // 바깥쪽 ENEMY, 안쪽 BULLET 모두 forEachReversedAt 으로 뒤에서 앞으로 돈다.
        // 이렇게 하면 충돌한 객체를 즉시 world.remove 해도 아직 방문 안 한 앞쪽 인덱스는 안전하다.
        scene.world.forEachReversedAt(MainScene.Layer.ENEMY) { enemyObject ->
            val enemy = enemyObject as? Enemy ?: return@forEachReversedAt

            // Player ↔ Enemy 부터 검사.
            // 충돌했으면 이 enemy 는 bullet 검사 없이 즉시 제거하고 다음 enemy 로 넘어간다.
            if (player.collidesWith(enemy)) {
                player.decreaseLife(PLAYER_HIT_DAMAGE)
                scene.world.remove(enemy, MainScene.Layer.ENEMY)
                if (player.dead) {
                    triggerGameOver()
                    return  // 더 이상의 충돌 처리 의미 없음
                }
                return@forEachReversedAt
            }

            // Bullet ↔ Enemy 검사.
            scene.world.forEachReversedAt(MainScene.Layer.BULLET) { bulletObject ->
                val bullet = bulletObject as? Bullet ?: return@forEachReversedAt

                if (bullet.collidesWith(enemy)) {
                    scene.world.remove(bullet, MainScene.Layer.BULLET)
                    enemy.decreaseLife(Bullet.DAMAGE)
                    if (enemy.dead) {
                        scene.world.remove(enemy, MainScene.Layer.ENEMY)
                        scene.addScore(enemy.score)
                    }
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        // collisionRect 디버그 표시는 World.draw() 가 IBoxCollidable 객체를 한 번 더 훑으며 처리한다.
    }

    // 1주차에는 Result Scene 이 아직 없으므로(3주차 작업), 지금은 Activity 자체를 finish 한다.
    // 3주차 #4 에서 sceneStack.change(ResultScene(...)) 로 교체할 자리다.
    private fun triggerGameOver() {
        gameOverTriggered = true
        (gctx.view.context as? Activity)?.finish()
    }

    companion object {
        private const val PLAYER_HIT_DAMAGE = 1
    }
}
