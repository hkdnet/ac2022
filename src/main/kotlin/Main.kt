import java.io.File

fun readLines(): List<String> {
    val l = mutableListOf<String>()
    while (true) {
        val s = readlnOrNull() ?: break
        l.add(s)
    }
    return l
}

fun solve(c: List<CharArray>): Int {
    val h = c.size
    val w = c[0].size

    fun score(x: Int, y: Int): Int {
        // edge
        if (x == 0 || y == 0 || x == h - 1 || y == w - 1) {
            return 0
        }

        val height = c[x][y]

        fun count(range: Iterable<Char>): Int {
            var max = '0' - 1
            var ans = 0
            for (cc in range) {
                if (cc >= height) {
                    return ans + 1
                } else {
                    ans += 1
                }
            }
            return ans
        }

        fun topVisibleCount(): Int {
            return count((x - 1 downTo 0).map { c[it][y] })
        }

        fun bottomVisibleCount(): Int {
            return count((x + 1 until h).map { c[it][y] })
        }

        fun leftVisibleCount(): Int {
            return count((y - 1 downTo 0).map { c[x][it] })
        }

        fun rightVisibleCount(): Int {
            return count((y + 1 until w).map { c[x][it] })
        }

        val l = leftVisibleCount()
        val r = rightVisibleCount()
        val t = topVisibleCount()
        val b = bottomVisibleCount()
        val ans = l * r * t * b

        return ans
    }

    return (0 until h).flatMap { x -> (0 until w).map { y -> Pair(x, y) } }.maxOfOrNull { (x, y) -> score(x, y) }!!
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    fun parse(): List<CharArray> {
        val lines = readLines()
        return lines.map { it.toCharArray() }
    }
    println(solve(parse()))
}
