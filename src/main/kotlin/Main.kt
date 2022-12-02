@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val elves: MutableList<List<Int>> = mutableListOf()
    var tmp: MutableList<Int> = mutableListOf()
    while (true) {
        val line = readLine()
        when (line) {
            null -> break;
            "" -> {
                elves.add(tmp);
                tmp = mutableListOf()
            }
            else -> {
                tmp.add(line.toInt())
            }
        }
    }
    val sorted = elves.map { it.sum() }.sortedBy{ -it }
    val sum: Int = sorted.take(3).sum()
    println(sum)
}
