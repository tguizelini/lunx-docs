import kotlin.text.iterator

fun main() {
    val list = mutableListOf<String>("A", "B", "C")
    list.forEach { t ->
        val b = "$t OK"
        println(b)
    }

    val set = mutableSetOf(10, 20, 30)
    val value = set.firstOrNull { it > 15 }

    val target = 3
    val map = HashMap<Int, Int>()
    val nums = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    nums.forEachIndexed { index, value ->
        val complement = target - value

        if (map.containsKey(complement)) {
            //return intArrayOf(map[complement]!!, index)
        }

        map[value] = index
    }

    for ((index, value) in nums.withIndex()) {
        println("Index: $index, Value: $value")
    }

    val myStr = "Kotlin"

    for (c in myStr) {
        println(c)
    }
}