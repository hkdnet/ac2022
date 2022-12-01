fun main(_args: Array<String>) {
    val elves: MutableList<List<Int>> = mutableListOf()
    var tmp: MutableList<Int> = mutableListOf()
    l@ while (true) {
        val line = readLine()
        when (line) {
            null -> l@break;
            "" -> {
                elves.add(tmp);
                tmp = mutableListOf()
            }
            else -> {
                tmp.add(line.toInt())
            }
        }
    }
    println(elves)
}
