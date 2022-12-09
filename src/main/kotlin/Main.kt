import java.io.File

fun readLines(): List<String> {
    val l = mutableListOf<String>()
    while (true) {
        val s = readlnOrNull() ?: break
        l.add(s)
    }
    return l
}

fun solve(ms: List<Move>): Int {
    val s = mutableSetOf<Point>()
    var h = Pair(0, 0)
    var t = Pair(0, 0)
    s.add(t)

    fun isTouching(p1: Point, p2: Point): Boolean {
        val (x1, y1) = p1
        val (x2, y2) = p2
        return x2 in x1 - 1..x1 + 1 && y2 in y1 - 1..y1 + 1
    }

    fun followIfNecessary(head: Point, tail: Point): Point {
        if (isTouching(head, tail)) {
            return tail
        }

        val (x1, y1) = head
        val (x2, y2) = tail

        return if (x1 == x2) {
            if (y1 < y2) {
                Pair(x2, y2 - 1)
            } else {
                Pair(x2, y2 + 1)
            }
        } else if (y1 == y2) {
            if (x1 < x2) {
                Pair(x2 - 1, y2)
            } else {
                Pair(x2 + 1, y2)
            }
        } else {
            when (Pair(x1 < x2, y1 < y2)) {
                Pair(true, true) -> Pair(x2 - 1, y2 - 1)
                Pair(true, false) -> Pair(x2 - 1, y2 + 1)
                Pair(false, true) -> Pair(x2 + 1, y2 - 1)
                Pair(false, false) -> Pair(x2 + 1, y2 + 1)
                else -> {
                    assert(false)
                    Pair(0, 0)
                }
            }
        }
    }

    for ((direction, step) in ms) {
        repeat(step) {
            h = direction.step(h)
            t = followIfNecessary(h, t)
            s.add(t)
            // println("$h and $t")
        }
    }

    return s.size
}

typealias Move = Pair<Direction, Int>

enum class Direction {
    U, D, L, R;

    fun step(p: Point): Point {
        val (x, y) = p
        return when (this) {
            Direction.D -> Pair(x, y - 1)
            Direction.U -> Pair(x, y + 1)
            Direction.L -> Pair(x - 1, y)
            Direction.R -> Pair(x + 1, y)
        }
    }
}
typealias Point = Pair<Int, Int>

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    fun parse(): List<Move> {
        val lines = readLines()
        return lines.map {
            val a = it.split(" ")
            Pair(Direction.valueOf(a[0]), a[1].toInt())
        }
    }
    println(solve(parse()))
}
