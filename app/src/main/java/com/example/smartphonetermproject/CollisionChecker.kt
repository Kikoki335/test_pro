package com.example.smartphonetermproject

import android.app.Activity
import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// CollisionChecker 는 화면에 그려지지 않는 "심판" 역할의 GameObject 이다.
// CONTROLLER layer 에 올려 두면 매 프레임 update 만 받고 draw 는 빈 함수로 둔다.
//
// 한 update 안에서 다음 세 종류의 충돌을 검사한다:
//   1. Player ↔ Enemy        — 부딪히면 Player.life 가 enemy.hitDamage 만큼 감소,
//                              Enemy.startDying (dying 상태 진입 + SPLIT 분열) 호출.
//                              dying enemy 는 collisionRect 가 빈 사각형이라 자동으로 검사 skip.
//                              (SUICIDE = 2, RANGED/SPLIT/SPLIT_MINION = 1 — Enemy.Type.hitDamage)
//   2. Bullet ↔ Enemy        — Bullet.startHitting (hit vfx 표시 후 self-remove), Enemy.life 감소.
//                              0 되면 Enemy.startDying + 점수 가산.
//   3. EnemyBullet ↔ Player  — EnemyBullet.startHitting (hit vfx 표시 후 self-remove), Player.life 감소.
//
// 모든 vfx (hit + die) 는 주체가 자기 draw 에서 직접 그린다 (DragonFlight Player.kt 의 laser_spark
// 패턴). hit vfx 는 hitting 상태로 남은 Bullet/EnemyBullet 본인이, die vfx 는 dying 상태로 남은
// Enemy 본인이 그림. CollisionChecker 는 vfx 를 spawn 하지 않고 layer 제거도 호출하지 않는다.
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
            // 충돌했으면 이 enemy 는 bullet 검사를 건너뛰고 다음 enemy 로 넘어간다.
            // Bullet 처치든 자폭 충돌이든 같은 startDying 경로 — die vfx + (SPLIT 일 때) 분열을
            // dying 진입 시점에 모두 처리. layer 제거는 Enemy 가 dyingTime 끝나면 스스로 한다.
            if (player.collidesWith(enemy)) {
                player.decreaseLife(enemy.hitDamage)
                enemy.startDying(scene)
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
                    bullet.startHitting()
                    enemy.decreaseLife(Bullet.DAMAGE)
                    if (enemy.dead) {
                        // SPLIT 분열도 startDying 안에서 처리 — 부모의 x, y 가 살아 있는 시점이라
                        // 그 위치 그대로 minion 좌·우 사선 spawn.
                        enemy.startDying(scene)
                        scene.addScore(enemy.score)
                    }
                }
            }
        }

        // EnemyBullet ↔ Player 검사. Player 가 죽으면 즉시 GameOver 트리거.
        // ENEMY 루프와 분리해 둔다 — 적 본체 충돌은 "적과의 직접 접촉", 탄 충돌은 "원거리 피격" 으로
        // 책임이 다르고, ENEMY 루프 안에서 처리하면 enemy 가 사라진 뒤 그 enemy 의 탄까지
        // 같은 프레임에 다 정리해야 하는 결합이 생긴다.
        scene.world.forEachReversedAt(MainScene.Layer.ENEMY_BULLET) { ebObject ->
            val enemyBullet = ebObject as? EnemyBullet ?: return@forEachReversedAt
            if (enemyBullet.collidesWith(player)) {
                enemyBullet.startHitting()
                player.decreaseLife(EnemyBullet.DAMAGE)
                if (player.dead) {
                    triggerGameOver()
                    return
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
}
