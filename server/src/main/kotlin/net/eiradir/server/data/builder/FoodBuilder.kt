package net.eiradir.server.data.builder

import net.eiradir.server.data.Food
import net.eiradir.server.data.FoodRegistry
import net.eiradir.server.data.ItemReference

class FoodBuilder(private val name: String, private val foodPoints: Int, private val drinkPoints: Int) {
    var restItem: ItemReference? = null
    var poison = 0

    fun build(): Food {
        return Food(name, foodPoints, drinkPoints, restItem, poison)
    }
}
//
//fun food(
//    name: String,
//    foodPoints: Int,
//    drinkPoints: Int,
//    body: FoodBuilder.() -> Unit = {}
//) = FoodBuilder(
//    name = name,
//    foodPoints = foodPoints,
//    drinkPoints = drinkPoints
//).apply(body).build().also {
//    FoodRegistry.register(it)
//}