package com.quillraven.masamune.screen

import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.ui.GameUI

class GameScreen : Q2DScreen() {
    private val gameUI = GameUI(game.skin, game.gameEventManager)

    override fun hide() {
        stage.clear()
    }

    override fun show() {
        game.mapManager.setMap(EMapType.MAP01)
        stage.addActor(gameUI)
    }

    override fun render(delta: Float) {
        game.ecsEngine.update(delta)
        stage.viewport.apply(true)
        stage.act()
        stage.draw()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        game.gameViewPort.update(width, height, true)
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {

    }
}