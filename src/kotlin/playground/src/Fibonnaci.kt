fun main() {
    println("----------------------------------")
    println("---------- BinarySearch ----------")

    val fiboNumbers = fibonacci(10)

    fiboNumbers.forEach { println(it) }

    println("----------------------------------")
}

fun fibonacci(generateNumber: Int): List<Int> {
    val list = mutableListOf<Int>()

    for (i in 0 until generateNumber) {
        if (list.isEmpty() || list.size == 1) {
            list.add(i)
        } else {
            val prevNumber1 = list[list.size - 1]
            val prevNumber2 = list[list.size - 2]
            list.add(prevNumber1 + prevNumber2)
        }
    }

    return list
}