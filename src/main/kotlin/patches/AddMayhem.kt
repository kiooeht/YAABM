package com.evacipated.cardcrawl.mod.yaabm.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.megacrit.cardcrawl.actions.AbstractGameAction
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.powers.MayhemPower

@SpirePatch2(
    clz = AbstractPlayer::class,
    method = "applyPreCombatLogic"
)
object AddMayhem {
    @JvmStatic
    fun Prefix(__instance: AbstractPlayer) {
        val p = __instance
        top(ApplyPowerAction(p, p, MayhemPower(p, p.masterHandSize)))
    }

    private fun top(action: AbstractGameAction) =
        AbstractDungeon.actionManager.addToTop(action)
}
