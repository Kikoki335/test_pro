package com.example.smartphonetermproject

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// CollisionChecker 는 화면에 그려지지 않는 "심판" 역할의 GameObject 이다.
// CONTROLLER layer 에 올려 두면 매 프레임 update 만 받고 draw 는 빈 함수로 둔다.
//
// 1주차 #5 시점에는 Bullet ↔ Enemy 만 검사한다.
// Player ↔ Enemy 충돌은 #6 에서 같은 패턴으로 추가 예정.
class CollisionChecker(private val gctx: GameContext) : IGameObject {
    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return

        // 바깥쪽 ENEMY, 안쪽 BULLET 모두 forEachReversedAt 으로 뒤에서 앞으로 돈다.
        // 이렇게 하면 충돌한 객체를 즉시 world.remove 해도 아직 방문 안 한 앞쪽 인덱스는 안전하다.
        scene.world.forEachReversedAt(MainScene.Layer.ENEMY) { enemyObject ->
            val enemy = enemyObject as? Enemy ?: return@forEachReversedAt

            scene.world.forEachReversedAt(MainScene.Layer.BULLET) { bulletObject ->
                val bullet = bulletObject as? Bullet ?: return@forEachReversedAt

                if (bullet.collidesWith(enemy)) {
                    scene.world.remove(bullet, MainScene.Layer.BULLET)
                    enemy.decreaseLife(Bullet.DAMAGE)
                    if (enemy.dead) {
                        scene.world.remove(enemy, MainScene.Layer.ENEMY)
                        // 점수 가산은 #6 에서 추가 예정.
                    }
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        // collisionRect 디버그 표시는 World.draw() 가 IBoxCollidable 객체를 한 번 더 훑으며 처리한다.
        // (a2dg/.../World.kt 의 drawsDebugInfo 분기)
    }
}
