package com.evacipated.cardcrawl.mod.yaabm.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.megacrit.cardcrawl.actions.GameActionManager
import com.megacrit.cardcrawl.dungeons.AbstractDungeon

@SpirePatch2(
    clz = GameActionManager::class,
    method = "update"
)
object AutoEndTurn {
    internal var ENABLED = false

    @JvmStatic
    fun Postfix(__instance: GameActionManager) {
        if (ENABLED && __instance.phase == GameActionManager.Phase.WAITING_ON_USER &&
            !AbstractDungeon.player.endTurnQueued && !AbstractDungeon.actionManager.turnHasEnded &&
            AbstractDungeon.actionManager.actions.isEmpty() && AbstractDungeon.actionManager.cardQueue.isEmpty()) {
            AbstractDungeon.overlayMenu.endTurnButton.disable(true)
        }
    }
}
