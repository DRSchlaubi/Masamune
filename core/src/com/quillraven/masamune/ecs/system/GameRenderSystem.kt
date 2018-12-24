package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.UNIT_SCALE
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.event.MapEvent

class GameRenderSystem constructor(game: MainGame) : EntitySystem(), Listener<MapEvent>, Disposable {
    private val viewport = game.gameViewPort
    private val camera = viewport.camera as OrthographicCamera
    private val batch = game.batch
    private val world = game.world
    private val b2dDebugRenderer = Box2DDebugRenderer()
    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, batch)

    private val renderEntities by lazy { engine.getEntitiesFor(Family.all(Box2DComponent::class.java, RenderComponent::class.java).get()) }
    private val testTex = Texture("test.png")

    init {
        game.gameEventManager.addMapEventListener(this)
    }

    override fun update(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        mapRenderer.setView(camera)
        if (mapRenderer.map != null) {
            mapRenderer.render()
        }
        batch.begin()
        for (entity in renderEntities) {
            val b2dCmp = (engine as ECSEngine).box2DMapper.get(entity)
            batch.draw(testTex, b2dCmp.interpolatedX - b2dCmp.width * 0.5f, b2dCmp.interpolatedY - b2dCmp.height * 0.5f, b2dCmp.width, b2dCmp.height)
        }
        batch.end()
        b2dDebugRenderer.render(world, viewport.camera.combined)
    }

    override fun receive(signal: Signal<MapEvent>?, `object`: MapEvent?) {
        mapRenderer.map = `object`!!.map
    }

    override fun dispose() {
        b2dDebugRenderer.dispose()
        mapRenderer.dispose()
        testTex.dispose()
    }
}