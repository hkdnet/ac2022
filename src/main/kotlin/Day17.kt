import kotlin.math.max


class Day17(private val moves: List<Move>) {
    private val width = 7
    private val cells = List(width) { mutableSetOf<Int>() }
    private val heights = MutableList(width) { 0 }

    private fun <T> infiniteLoop(l: List<T>): Iterator<T> {
        var idx = 0
        return object : Iterator<T> {
            override fun hasNext(): Boolean {
                return true
            }

            override fun next(): T {
                val ret = l[idx]
                idx += 1
                idx %= l.size
                return ret
            }
        }
    }

    data class Rock(val points: List<Point>) {
        fun project(leftEdge: Point): List<Point> {
            val (x, y) = leftEdge
            return points.map { (dx, dy) -> Point(x + dx, y + dy) }
        }
    }

    data class Point(val x: Int, val y: Int)

    enum class Move {
        Left, Right
    }


    private val rocks = listOf(
        /*
        ####
        */
        Rock(listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))),

        /*
        .#.
        ###
        .#.
        */
        Rock(listOf(Point(0, 1), Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 1))),

        /*
        ..#
        ..#
        ###
        */
        Rock(listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2))),

        /*
        #
        #
        #
        #
        */
        Rock(listOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3))),

        /*
        ##
        ##
        */
        Rock(listOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1)))
    )

    companion object {
        fun exec() {
            val moves = parse(readLines())
            val s = Day17(moves)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): List<Move> {
            assert(lines.size == 1)
            return lines.first().toCharArray().map {
                if (it == '<') {
                    Move.Left
                } else {
                    assert(it == '>')
                    Move.Right
                }
            }
        }
    }

    private fun set(point: Point) {
        val (x, y) = point
        cells[x].add(y)
        heights[x] = max(heights[x], y)
    }

    private fun isFilled(point: Point): Boolean {
        val (x, y) = point
        return cells[x].contains(y)
    }

    private fun solve(): Int {
        var rockCount = 0
        val rockIterator = infiniteLoop(rocks)
        val moveIterator = infiniteLoop(moves)

        (0 until width).forEach { set(Point(it, 0)) }

        fun applyJetIfPossible(m: Move, r: Rock, leftEdge: Point): Point {
            val (x, y) = leftEdge

            val possibleNextLeftEdge = when (m) {
                Move.Left -> {
                    Point(x - 1, y)
                }

                Move.Right -> {
                    Point(x + 1, y)
                }
            }

            return if (
                r.project(possibleNextLeftEdge).all {
                    it.x in 0 until width && !isFilled(it)
                }
            ) {
                possibleNextLeftEdge
            } else {
                leftEdge
            }
        }

        fun fall(r: Rock, leftEdge: Point): Point? {
            val (x, y) = leftEdge
            val possibleNextLeftEdge = Point(x, y - 1)
            return if (
                r.project(possibleNextLeftEdge).all {
                    !isFilled(it)
                }) {
                possibleNextLeftEdge
            } else {
                null
            }
        }

        while (rockCount < 2022) {
            val r = rockIterator.next()
            var leftEdge = Point(2, highest() + 4)
            do {
                val m = moveIterator.next()
                val newLeftEdge = applyJetIfPossible(m, r, leftEdge)
                leftEdge = newLeftEdge
                leftEdge = fall(r, leftEdge) ?: break
            } while (true)

            r.project(leftEdge).forEach { set(it) }
            rockCount += 1
        }

        return highest()
    }

    private fun debug(minY: Int, maxY: Int) {
        for (y in maxY downTo minY) {
            for (x in 0 until width) {
                if (isFilled(Point(x, y))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    private fun highest(): Int {
        return heights.max()
    }
}
