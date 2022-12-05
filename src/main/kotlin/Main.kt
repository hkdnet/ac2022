val moveFromTo = Regex("""^move (\d+) from (\d+) to (\d+)$""")

typealias Operation = Triple<Int, Int, Int>

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    var s = readln()
    val n = (s.length + 1) / 4
    val ds = Array<ArrayDeque<Char>>(n + 1) { ArrayDeque<Char>() }
    while (s != "") {
        val cs = s.toCharArray()
        for (i in 0 until n) {
            val idx = 4 * i + 1
            val c = cs[idx]
            if (c in 'A'..'Z') {
                ds[i + 1].addFirst(c)
            }
        }
        s = readln()
    }
    val ops = mutableListOf<Operation>()
    while (true) {
        val s = readlnOrNull() ?: break
        val match = moveFromTo.matchEntire(s)!!
        val move = match.groups[1]!!.value.toInt()
        val from = match.groups[2]!!.value.toInt()
        val to = match.groups[3]!!.value.toInt()
        ops.add(Triple(move, from, to))
    }

    for ((move, from, to) in ops) {
        val df = ds[from]
        val dt = ds[to]
        val tmp = mutableListOf<Char>()
        for (c in 0 until move) {
            tmp.add(df.removeLast())
        }
        for (c in tmp) {
            dt.addLast(c)
        }
    }
    val ans = ds.drop(1).map { it.last() }.joinToString("")
    println(ans)
}
