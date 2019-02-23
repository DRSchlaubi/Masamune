package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ConversationComponent : Pool.Poolable, Component {
    var currentConversationId = ""

    override fun reset() {
        currentConversationId = ""
    }
}