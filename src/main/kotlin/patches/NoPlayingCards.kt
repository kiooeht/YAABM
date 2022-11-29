package com.evacipated.cardcrawl.mod.yaabm.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.cards.AbstractCard

@SpirePatch2(
    clz = AbstractCard::class,
    method = "hasEnoughEnergy"
)
object NoPlayingCards {
    internal var allowedEnergy: Int = -1

    @JvmStatic
    fun Prefix(__instance: AbstractCard): SpireReturn<Boolean> {
        if (allowedEnergy >= __instance.costForTurn) {
            return SpireReturn.Continue()
        }
        return SpireReturn.Return(__instance.isInAutoplay)
    }
}
