package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class ActionableComponent : Pool.Poolable, Component {
    @Transient
    lateinit var source: Entity

    override fun reset() {
    }
}