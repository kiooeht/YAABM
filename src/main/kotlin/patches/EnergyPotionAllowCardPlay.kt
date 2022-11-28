package patches

import com.evacipated.cardcrawl.mod.yaabm.patches.NoPlayingCards
import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.actions.AbstractGameAction
import com.megacrit.cardcrawl.cards.AbstractCard
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.core.EnergyManager
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.potions.EnergyPotion
import javassist.CtBehavior

@SpirePatch2(
    clz = EnergyPotion::class,
    method = "use"
)
object EnergyPotionAllowCardPlay {
    @JvmStatic
    fun Postfix(__instance: EnergyPotion, ___potency: Int) {
        AbstractDungeon.actionManager
            .addToBottom(object : AbstractGameAction() {
                override fun update() {
                    NoPlayingCards.allowedEnergy += ___potency
                    isDone = true
                }
            })
    }
}

@SpirePatch2(
    clz = AbstractPlayer::class,
    method = "useCard"
)
object UsePotionEnergy {
    @JvmStatic
    @SpireInsertPatch(
        locator = Locator::class
    )
    fun Insert(c: AbstractCard) {
        NoPlayingCards.allowedEnergy -= c.costForTurn
        if (NoPlayingCards.allowedEnergy < 0) {
            NoPlayingCards.allowedEnergy = 0
        }
    }

    private class Locator : SpireInsertLocator() {
        override fun Locate(ctBehavior: CtBehavior?): IntArray {
            val finalMatcher = Matcher.MethodCallMatcher(EnergyManager::class.java, "use")
            return LineFinder.findInOrder(ctBehavior, finalMatcher)
        }
    }
}
