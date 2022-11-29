package com.evacipated.cardcrawl.mod.yaabm.patches

import basemod.ReflectionHacks
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.evacipated.cardcrawl.modthespire.lib.*
import com.megacrit.cardcrawl.core.Settings
import com.megacrit.cardcrawl.helpers.Hitbox
import com.megacrit.cardcrawl.helpers.TipHelper
import com.megacrit.cardcrawl.screens.options.GiantToggleButton
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton
import kotlin.math.abs

object AutoEndTurnUI {
    object Enums {
        @JvmStatic
        @SpireEnum
        lateinit var AUTO_END_TURN_ENABLED: GiantToggleButton.ToggleType

    }

    @SpirePatch2(
        clz = EndTurnButton::class,
        method = SpirePatch.CLASS
    )
    object Fields {
        @JvmField
        val autoEndTurnButton: SpireField<GiantToggleButton> = SpireField {
            GiantToggleButton(Enums.AUTO_END_TURN_ENABLED, 0f, 0f, "Auto").apply {
                val hb = ReflectionHacks.getPrivate<Hitbox>(this, GiantToggleButton::class.java, "hb")
                hb.resize(150 * Settings.scale, 53 * Settings.scale)
            }
        }
    }

    @SpirePatch2(
        clz = EndTurnButton::class,
        method = "update"
    )
    object Update {
        @JvmStatic
        fun Prefix(__instance: EndTurnButton, @ByRef ___current_x: FloatArray, ___current_y: Float, ___hb: Hitbox, ___target_x: Float): SpireReturn<Void> {
            val autoEndTurnButton = Fields.autoEndTurnButton[__instance]
            val x = ___current_x[0] - 40 * Settings.scale
            val y = ___current_y - 80 * Settings.scale
            ReflectionHacks.setPrivate(autoEndTurnButton, GiantToggleButton::class.java, "x", x)
            ReflectionHacks.setPrivate(autoEndTurnButton, GiantToggleButton::class.java, "y", y)
            val hb = ReflectionHacks.getPrivate<Hitbox>(autoEndTurnButton, GiantToggleButton::class.java, "hb")
            hb.move(x + 40 * Settings.scale, y)

            autoEndTurnButton.update()

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                autoEndTurnButton.ticked = !autoEndTurnButton.ticked
                AutoEndTurn.ENABLED = autoEndTurnButton.ticked
            }

            return if (AutoEndTurn.ENABLED) {
                // update end turn button position
                if (___current_x[0] != ___target_x) {
                    ___current_x[0] = MathUtils.lerp(___current_x[0], ___target_x, Gdx.graphics.deltaTime * 9f)
                    if (abs(___current_x[0] - ___target_x) < Settings.UI_SNAP_THRESHOLD) {
                        ___current_x[0] = ___target_x
                    }
                    ___hb.move(___current_x[0], ___current_y)
                }

                SpireReturn.Return()
            } else {
                SpireReturn.Continue()
            }
        }
    }

    @SpirePatch2(
        clz = EndTurnButton::class,
        method = "render"
    )
    object Render {
        private var fakeDisabled = false

        @JvmStatic
        fun Prefix(__instance: EndTurnButton, sb: SpriteBatch, ___isHidden: Boolean, ___current_x: Float, ___current_y: Float): SpireReturn<Void> {
            if (!___isHidden) {
                val autoEndTurnButton = Fields.autoEndTurnButton[__instance]
                autoEndTurnButton.render(sb)

                val hb = ReflectionHacks.getPrivate<Hitbox>(autoEndTurnButton, GiantToggleButton::class.java, "hb")
                if (hb.hovered) {
                    TipHelper.renderGenericTip(
                        ___current_x - 90 * Settings.scale,
                        ___current_y + 300 * Settings.scale,
                        "Auto End Turn (Space)",
                        "When enabled, your turn will automatically end after after the cards played by Mayhem are resolved."
                    )
                }
            }

            return if (AutoEndTurn.ENABLED) {
                fakeDisabled = true
                __instance.enabled = false
                __instance.isGlowing = false
                SpireReturn.Continue()
            } else {
                SpireReturn.Continue()
            }
        }

        @JvmStatic
        fun Postfix(__instance: EndTurnButton) {
            if (fakeDisabled) {
                fakeDisabled = false
                __instance.enabled = true
            }
        }
    }

    @SpirePatch2(
        clz = GiantToggleButton::class,
        method = "initialize"
    )
    object Initialize {
        @JvmStatic
        fun Prefix(__instance: GiantToggleButton): SpireReturn<Void> {
            if (__instance.type == Enums.AUTO_END_TURN_ENABLED) {
                __instance.ticked = AutoEndTurn.ENABLED
                return SpireReturn.Return()
            }
            return SpireReturn.Continue()
        }
    }

    @SpirePatch2(
        clz = GiantToggleButton::class,
        method = "useEffect"
    )
    object UseEffect {
        @JvmStatic
        fun Prefix(__instance: GiantToggleButton): SpireReturn<Void> {
            if (__instance.type == Enums.AUTO_END_TURN_ENABLED) {
                AutoEndTurn.ENABLED = __instance.ticked
                return SpireReturn.Return()
            }
            return SpireReturn.Continue()
        }
    }
}
