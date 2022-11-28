package com.evacipated.cardcrawl.mod.yaabm.patches

import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.actions.AbstractGameAction
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction
import com.megacrit.cardcrawl.actions.utility.UnlimboAction
import com.megacrit.cardcrawl.actions.utility.WaitAction
import com.megacrit.cardcrawl.cards.AbstractCard
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect
import javassist.CtBehavior

@SpirePatches2(
    SpirePatch2(
        clz = ShowCardAndAddToHandEffect::class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = [AbstractCard::class, Float::class, Float::class]
    ),
    SpirePatch2(
        clz = ShowCardAndAddToHandEffect::class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = [AbstractCard::class]
    )
)
object AutoplayNewCards {
    @JvmStatic
    @SpireInsertPatch(
        locator = Locator::class
    )
    fun Insert(__instance: ShowCardAndAddToHandEffect, ___card: AbstractCard): SpireReturn<Void> {
        __instance.isDone = true
        AbstractDungeon.player.hand.group.remove(___card)
        AbstractDungeon.getCurrRoom().souls.remove(___card)
        AbstractDungeon.player.limbo.group.add(___card)
        ___card.applyPowers()
        val target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng)
        top(NewQueueCardAction(___card, target, false, true))
        top(UnlimboAction(___card))
        top(WaitAction(if (Settings.FAST_MODE) { Settings.ACTION_DUR_FASTER } else { Settings.ACTION_DUR_MED }))

        return SpireReturn.Return()
    }

    private class Locator : SpireInsertLocator() {
        override fun Locate(ctBehavior: CtBehavior?): IntArray {
            val finalMatcher = Matcher.FieldAccessMatcher(AbstractPlayer::class.java, "hand")
            return LineFinder.findInOrder(ctBehavior, finalMatcher)
        }
    }

    private fun top(action: AbstractGameAction) =
        AbstractDungeon.actionManager.addToTop(action)
}
