package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.quillraven.masamune.event.GameEventManager


class GameUI constructor(skin: Skin, private val eventMgr: GameEventManager) : Table(skin) {
    init {
        setFillParent(true)

        val touchpad = Touchpad(0f, skin)
        touchpad.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventMgr.dispatchInputMoveEvent(touchpad.knobPercentX, touchpad.knobPercentY)
            }
        })
        add(touchpad).bottom().left()

        val btn = ImageButton(skin, "action")
        btn.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                eventMgr.dispatchInputActionEvent()
                return true
            }
        })
        add(btn).bottom().expandX().right()

        bottom().left()
    }
}