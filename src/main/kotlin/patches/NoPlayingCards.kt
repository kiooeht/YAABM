package com.evacipated.cardcrawl.mod.yaabm.patches

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn
import com.megacrit.cardcrawl.cards.AbstractCard

@SpirePatch2(
    clz = AbstractCard::class,
    method = "hasEnoughEnergy"
)
object NoPlayingCards {
    @JvmStatic
    fun Prefix(__instance: AbstractCard): SpireReturn<Boolean> {
        return SpireReturn.Return(__instance.isInAutoplay)
    }
}
