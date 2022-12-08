import java.io.File

fun readLines(f: String): List<String> {
    return File(f).useLines { it.toList() }
}

fun solve(c: List<CharArray>): Int {
    val h = c.size
    val w = c[0].size

    var ans = 0

    fun visible(x: Int, y: Int): Boolean {
        // edge
        if (x == 0 || y == 0 || x == h - 1 || y == w - 1) {
            return true
        }
        val height = c[x][y]
        fun leftVisible(): Boolean {
            return (0 until x).all { c[it][y] < height }
        }
        fun rightVisible(): Boolean {
            return (x+1 until h).all { c[it][y] < height }
        }
        fun topVisible(): Boolean {
            return (0 until y).all { c[x][it] < height }
        }
        fun bottomVisible(): Boolean {
            return (y+1 until w).all { c[x][it] < height }
        }
        return leftVisible() || rightVisible() || topVisible() || bottomVisible()
    }

    for (x in 0 until h) {
        for (y in 0 until w) {
            if (visible(x, y)) {
                ans += 1
            }
        }
    }

    return ans
}
@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    fun parse(f: String): List<CharArray> {
        val lines = readLines(f)
        return lines.map { it.toCharArray() }
    }
    println(
        solve(parse("input/8-0.txt"))
    )
    println(
        solve(parse("input/8-1.txt"))
    )
}
