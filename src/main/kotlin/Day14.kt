typealias Day14Point = Pair<Int, Int>

enum class Day14Cell {
    Wall, Sand
}

class Day14(private val pointLists: List<List<Day14Point>>) {
    companion object {
        fun exec() {
            val pointLists = parse(readLines())
            val s = Day14(pointLists)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): List<List<Day14Point>> {
            return lines.map {
                it.split(" -> ").map {
                    val arr = it.split(",").map(String::toInt)
                    Pair(arr[0], arr[1])
                }
            }
        }
    }

    private var w = 0
    private var h = 0
    private var f: MutableMap<Int, MutableMap<Int, Day14Cell>> = mutableMapOf()

    private fun build() {
        w = pointLists.maxOf { pointList -> pointList.maxOf { (x, _) -> x } } + 1
        h = pointLists.maxOf { pointList -> pointList.maxOf { (_, y) -> y } } + 1

        for (pointList in pointLists) {
            for (idx in 0 until pointList.size - 1) {
                val cs = cellsBetween(pointList[idx], pointList[idx + 1])
                cs.forEach { setCell(it, Day14Cell.Wall) }
            }
        }
    }

    private fun getCell(p: Day14Point): Day14Cell? {
        val (x, y) = p
        return f[y]?.get(x)
    }

    private fun setCell(p: Day14Point, v: Day14Cell) {
        val (x, y) = p
        f.getOrPut(y) { mutableMapOf() }.put(x, v)
    }

    private fun range(a: Int, b: Int): IntRange {
        return if (a <= b) {
            a..b
        } else {
            b..a
        }
    }

    private fun cellsBetween(p1: Day14Point, p2: Day14Point): Set<Day14Point> {
        val (x1, y1) = p1
        val (x2, y2) = p2
        val s = mutableSetOf<Day14Point>()
        for (x in range(x1, x2)) {
            for (y in range(y1, y2)) {
                s.add(Pair(x, y))
            }
        }

        return s
    }

    private fun solve(): Int {
        build()

        var cnt = 0
        while (fall()) {
            cnt += 1
        }

        return cnt
    }

    private fun fall(): Boolean {
        var p = Pair(500, 0)
        while (p.second <= h) {
            val (x, y) = p
            if (getCell(Pair(x, y + 1)) == null) {
                p = Pair(x, y + 1)
                continue
            }
            // Something is at (x, y + 1)
            if (getCell(Pair(x - 1, y + 1)) == null) {
                // go to left
                p = Pair(x - 1, y + 1)
                continue
            }
            if (getCell(Pair(x + 1, y + 1)) == null) {
                // go to right
                p = Pair(x + 1, y + 1)
                continue
            }
            setCell(p, Day14Cell.Sand)
            return true
        }

        // p's y is greater than the max. The sand does not stop.
        return false
    }
}
