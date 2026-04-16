package com.example.smartphonetermproject

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// 보스 스테이지.
// 2주차 #5 시점에서는 노말 스테이지(MainScene)와 게임 로직(Player/Bullet/Enemy/충돌/HUD/타이머)이 모두 동일하고
// 오직 배경만 boss_bg 로 다르다. MainScene 을 상속해 같은 동작을 그대로 쓰고, super 생성자에서 배경 리소스만 바꾼다.
//
// 6주차 sub-task (보스 클래스, 패턴, HP gauge, 진입 연출 등) 가 들어올 때 이 클래스에 override 로 추가될 자리.
class BossScene(gctx: GameContext) : MainScene(
    gctx = gctx,
    backgroundResId = R.mipmap.boss_bg,
    isBossStage = true,
)
