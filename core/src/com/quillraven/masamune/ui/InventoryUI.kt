package com.quillraven.masamune.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.quillraven.masamune.event.GameEventManager

private const val TAG = "InventoryUI"

class InventoryUI constructor(skin: Skin, private val eventMgr: GameEventManager) : Table(skin) {
    private val dragAndDrop = DragAndDrop()
    private val dragActor = Image()
    private val payload = DragAndDrop.Payload().apply {
        dragActor = this@InventoryUI.dragActor
        dragActor.setSize(50f, 50f)
    }

    private val itemInfoImg = Image()
    private val itemInfoTitle = TextButton("", skin, "label")
    private val itemInfoDesc = TextButton("", skin, "label_small")
    private val contentTable = Table(skin)
    private val slotTable = Table(skin)

    init {
        setFillParent(true)

        // content table
        contentTable.background = skin.getDrawable("dialog_light")
        contentTable.pad(40f, 40f, 35f, 40f)

        // item info of content table
        contentTable.add(itemInfoImg).size(75f, 75f).padLeft(35f)
        val itemInfo = VerticalGroup()
        itemInfo.addActor(itemInfoTitle)
        itemInfo.addActor(itemInfoDesc)
        contentTable.add(itemInfo).expandX().fillX().padRight(35f).minHeight(100f).row()

        // slot table of content table
        slotTable.defaults().space(5f)
        for (i in 1..40) {
            addInventorySlot()
        }
        contentTable.add(slotTable).padTop(10f).expand().fill().colspan(2)

        // add title area and content to table
        // title area
        val label = TextButton("Inventory", skin, "dialog_title")
        val imgSkull = Image(skin.getDrawable("skull"))
        val btnClose = ImageButton(skin.getDrawable("btn_close"))
        imgSkull.setScale(0.75f, 0.75f)
        add(imgSkull).padBottom(-25f).padLeft(70f).colspan(2).row()
        add(label).size(Value.percentWidth(0.6f, this), Value.prefHeight).height(130f).right().padLeft(90f)
        add(btnClose).left().padLeft(-5f).row()
        imgSkull.toFront()
        // content
        add(contentTable).colspan(2).padBottom(30f).padTop(-5f)

        // move little bit to the right to center between touchpad and action button
        padLeft(60f)

        // close button hides the inventory UI
        btnClose.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                // clear item info and hide inventory UI
                updateItemInfo("", "", "")
                stage.root.removeActor(this@InventoryUI)
                return true
            }
        })

        // test stuff
        addItem(0, "claymore")
        addItem(2, "claymore")
        addItem(3, "claymore")
        addItem(13, "brilliant_blue_new")
        addItem(13, "brilliant_blue_new")
        addItem(-1, "brilliant_blue_new")
    }

    private fun addInventorySlot() {
        val slot = WidgetGroup()
        val imgSlot = Image(skin.getDrawable("slot_cursed"))
        imgSlot.setSize(60f, 60f)
        slot.addActor(imgSlot)
        val imgItem = Image()
        imgItem.setSize(50f, 50f)
        imgItem.setPosition(10f, 10f)
        imgItem.scaleBy(-0.25f)
        slot.addActor(imgItem)

        slotTable.add(slot).expand().fill()
        if (slotTable.children.size % 10 == 0) {
            slotTable.row()
        }

        // drag source
        dragAndDrop.addSource(object : DragAndDrop.Source(slot) {
            override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): DragAndDrop.Payload? {
                if (imgItem.drawable == null) {
                    // no item in slot -> ignore drag
                    return null
                }

                // add item as drag actor --> item gets visually removed from slot but the real remove is not triggered yet
                dragActor.drawable = imgItem.drawable
                imgItem.isVisible = false
                dragAndDrop.setDragActorPosition(dragActor.width * 0.5f, -dragActor.height * 0.5f)
                return payload
            }

            override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                imgItem.isVisible = true
            }
        })

        // drag target
        dragAndDrop.addTarget(object : DragAndDrop.Target(slot) {
            override fun drag(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int): Boolean {
                // valid drag only on empty slots that are different from the source slot
                return imgItem.drawable == null && source.actor != actor
            }

            override fun drop(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int) {
                // valid drop --> add item to new slot and remove it from old slot
                removeItem(slotTable.children.indexOf(source.actor))
                addItem(slotTable.children.indexOf(actor), (dragActor.drawable as BaseDrawable).name)
            }
        })

        // show item info listener
        slot.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (imgItem.drawable != null) {
                    //teststuff
                    updateItemInfo("Claymore", "A basic sword with awesome stats\n+10 Strength", (imgItem.drawable as BaseDrawable).name)
                }
                return true
            }
        })
    }

    fun updateItemInfo(name: String, description: String, texture: String) {
        itemInfoTitle.setText(name)
        itemInfoDesc.setText(description)
        if (texture.isBlank()) {
            itemInfoImg.drawable = null
        } else {
            itemInfoImg.drawable = skin.getDrawable(texture)
        }
    }

    fun removeItem(slotIdx: Int) {
        if (slotIdx < 0 || slotIdx >= slotTable.children.size) {
            Gdx.app.error(TAG, "Trying to remove item from invalid slot $slotIdx")
            return
        }

        val slot = slotTable.children[slotIdx] as WidgetGroup
        val item = slot.children[1] as Image
        if (item.drawable == null) {
            Gdx.app.error(TAG, "Slot $slotIdx does not have any item")
            return
        }
        item.drawable = null
    }

    fun addItem(slotIdx: Int, texture: String) {
        if (slotIdx < 0 || slotIdx >= slotTable.children.size) {
            Gdx.app.error(TAG, "Trying to add item $texture to invalid slot $slotIdx")
            return
        }

        val slot = slotTable.children[slotIdx] as WidgetGroup
        val item = slot.children[1] as Image
        if (item.drawable != null) {
            Gdx.app.error(TAG, "Slot $slotIdx already has an item")
            return
        }

        item.drawable = skin.getDrawable(texture)
    }
}