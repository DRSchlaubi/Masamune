package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity

interface ContactListener {
    fun beginCharacterContact(player: Entity, character: Entity)

    fun endCharacterContact(player: Entity, character: Entity)

    fun beginItemContact(player: Entity, item: Entity)

    fun endItemContact(player: Entity, item: Entity)
}