package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.UNIT_SCALE
import com.quillraven.masamune.ecs.CmpMapperB2D
import com.quillraven.masamune.ecs.CmpMapperRender
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.event.MapEvent


private const val TAG = "GameRenderSystem"

private class YComparator : Comparator<Entity> {
    override fun compare(o1: Entity, o2: Entity): Int {
        return Math.signum(CmpMapperB2D.get(o2).interpolatedY - CmpMapperB2D.get(o1).interpolatedY).toInt()
    }
}

class GameRenderSystem constructor(game: MainGame) : SortedIteratingSystem(Family.all(Box2DComponent::class.java, RenderComponent::class.java).get(), YComparator()), Listener<MapEvent>, Disposable {
    private val viewport = game.gameViewPort
    private val camera = viewport.camera as OrthographicCamera
    private val batch = game.batch
    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, batch)

    private val clipBounds = Rectangle()
    private val scissors = Rectangle()

    init {
        game.gameEventManager.addMapEventListener(this)
    }

    override fun update(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        mapRenderer.setView(camera)

        clipBounds.set(camera.position.x - camera.viewportWidth * 0.5f, camera.position.y - camera.viewportHeight * 0.5f, viewport.worldWidth, viewport.worldHeight)
        ScissorStack.calculateScissors(camera, viewport.screenX.toFloat(), viewport.screenY.toFloat(), viewport.screenWidth.toFloat(), viewport.screenHeight.toFloat(), batch.transformMatrix, clipBounds, scissors)
        ScissorStack.pushScissors(scissors)

        if (mapRenderer.map != null) {
            mapRenderer.render()
        }
        batch.begin()
        forceSort()
        super.update(deltaTime)
        batch.end()
        batch.flush()

        ScissorStack.popScissors()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val b2dCmp = CmpMapperB2D.get(entity)
        val renderCmp = CmpMapperRender.get(entity)

        renderCmp.sprite.apply {
            flip((renderCmp.flipX && !isFlipX) || (!renderCmp.flipX && isFlipX), (renderCmp.flipY && !isFlipY) || (!renderCmp.flipY && isFlipY))
            setBounds(b2dCmp.interpolatedX - renderCmp.width * 0.5f, b2dCmp.interpolatedY - b2dCmp.height * 0.5f, renderCmp.width, renderCmp.height)
            setOriginCenter()
            rotation = b2dCmp.interpolatedAngle * MathUtils.radDeg
            draw(batch)
        }
    }

    override fun receive(signal: Signal<MapEvent>?, obj: MapEvent) {
        mapRenderer.map = obj.map
    }

    override fun dispose() {
        mapRenderer.dispose()
    }
}