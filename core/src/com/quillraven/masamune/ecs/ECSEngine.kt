package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.bodyDef
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.CameraComponent
import com.quillraven.masamune.ecs.component.PlayerInputComponent
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.ecs.system.Box2DSystem
import com.quillraven.masamune.ecs.system.CameraSystem
import com.quillraven.masamune.ecs.system.GameRenderSystem
import com.quillraven.masamune.ecs.system.PlayerInputSystem
import com.quillraven.masamune.fixtureDef
import com.quillraven.masamune.resetBodyAndFixtureDef

class ECSEngine : PooledEngine(), Disposable {
    private val game = Gdx.app.applicationListener as MainGame
    internal val box2DMapper = ComponentMapper.getFor(Box2DComponent::class.java)

    init {
        addSystem(PlayerInputSystem())
        addSystem(Box2DSystem(game))
        addSystem(CameraSystem(game))
        addSystem(GameRenderSystem(game))
    }

    override fun dispose() {
        for (system in systems) {
            if (system is Disposable) {
                system.dispose()
            }
        }
    }

    fun createPlayer(x: Float, y: Float) {
        val entity = createEntity()

        resetBodyAndFixtureDef()
        entity.add(createComponent(Box2DComponent::class.java).apply {
            width = 1f
            height = 1f
            prevX = x
            prevY = y
            interpolatedX = x
            interpolatedY = y

            bodyDef.type = BodyDef.BodyType.DynamicBody
            bodyDef.position.set(x + width * 0.5f, y + height * 0.5f)
            bodyDef.fixedRotation = true
            body = game.world.createBody(bodyDef)

            val polygonShape = PolygonShape()
            polygonShape.setAsBox(width * 0.5f, height * 0.5f)
            fixtureDef.shape = polygonShape
            fixtureDef.isSensor = false
            body?.createFixture(fixtureDef)
            polygonShape.dispose()
        })

        entity.add(createComponent(CameraComponent::class.java))
        entity.add(createComponent(PlayerInputComponent::class.java))
        entity.add(createComponent(RenderComponent::class.java))

        addEntity(entity)
    }
}