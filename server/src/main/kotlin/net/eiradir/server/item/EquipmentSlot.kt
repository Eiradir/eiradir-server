package net.eiradir.server.item

enum class EquipmentSlot(vararg val slotIds: Int) {
    None,
    Feet(0),
    Legs(1),
    Gloves(2),
    Hands(3, 4),
    Rings(5, 6),
    Torso(7),
    Neck(8),
    Back(9),
    Bag(10),
    Head(11),
}