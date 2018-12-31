package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.masamune.map.EMapType

class GameEventManager {
    private val mapSignal = Signal<MapEvent>()
    private val mapEvent = MapEvent

    private val inputSignal = Signal<InputEvent>()
    private val inputEvent = InputEvent

    private val contactSignal = Signal<ContactEvent>()
    private val contactEvent = ContactEvent

    fun addMapEventListener(listener: Listener<MapEvent>) {
        mapSignal.add(listener)
    }

    fun dispatchMapEvent(oldType: EMapType, newType: EMapType, map: TiledMap, width: Float, height: Float) {
        mapEvent.apply {
            this.oldType = oldType
            this.newType = newType
            this.map = map
            this.width = width
            this.height = height
        }
        mapSignal.dispatch(mapEvent)
    }

    fun addInputEventListener(listener: Listener<InputEvent>) {
        inputSignal.add(listener)
    }

    fun dispatchInputMoveEvent(percX: Float, percY: Float) {
        inputEvent.apply {
            type = EInputType.MOVE
            movePercX = percX
            movePercY = percY
        }
        inputSignal.dispatch(inputEvent)
    }

    fun addContactEventListener(listener: Listener<ContactEvent>) {
        contactSignal.add(listener)
    }

    fun dispatchContactPlayerCharacterEvent(player: Entity, character: Entity) {
        contactEvent.apply {
            this.player = player
            this.character = character
        }
        contactSignal.dispatch(contactEvent)
    }
}