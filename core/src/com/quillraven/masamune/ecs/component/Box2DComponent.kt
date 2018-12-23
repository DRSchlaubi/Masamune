package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class Box2DComponent : Pool.Poolable, Component {
    var body: Body? = null
    var width = 0f
    var height = 0f
    var prevX = 0f
    var prevY = 0f
    var interpolatedX = 0f
    var interpolatedY = 0f

    override fun reset() {
        body?.world?.destroyBody(body)
        body = null
    }
}