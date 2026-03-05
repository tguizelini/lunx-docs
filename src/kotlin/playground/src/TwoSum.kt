fun main() {
    val values = listOf(255, 7, 11, 55, 15, 17, 6)

    val result = twoSumMap(nums = values.toIntArray(), target = 13)

    result.forEach { i ->
        println("index: $i, value: ${values[i]}")
    }
}

private fun twoSumMap(nums: IntArray, target: Int): List<Int> {
    val indexByValue = mutableMapOf<Int, Int>() // value -> index

    nums.forEachIndexed { i, value ->
        // If the target equals the current value, return only this
        // index (same behavior as your Java code)
        if (value == target) return listOf(i)

        val complement = target - value
        val j = indexByValue[complement]
        if (j != null) return listOf(j, i)

        indexByValue[value] = i
    }

    return emptyList()
}

/*
    [ Big-O ]
    Time: O(n) - percorre o array uma vez, sem loops aninhados
    Space: O(n) - usa um Map auxiliar que pode crescer até o tamanho do input
 */