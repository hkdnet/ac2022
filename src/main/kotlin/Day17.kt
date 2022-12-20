import kotlin.math.max


class Day17(private val moves: List<Move>) {
    private val width = 7
    private val cells = List(width) { mutableSetOf<Long>() }
    private val heights = MutableList(width) { 0L }

    data class Rock(val points: List<Point>) {
        fun project(leftEdge: Point): List<Point> {
            val (x, y) = leftEdge
            return points.map { (dx, dy) -> Point(x + dx, y + dy) }
        }
    }

    data class Point(val x: Int, val y: Long)

    enum class Move {
        Left, Right
    }

    data class State(val rocks: String, val rockIndex: Int, val moveIndex: Int)


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

    private fun solve(): Long {
        var rockCount = 0L
        var rockIndex = 0
        var moveIndex = 0

        val rockThreshold = 1_000_000_000_000L

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

            val isAvailable = r.project(possibleNextLeftEdge).all {
                !isFilled(it)
            }
            return if (isAvailable) {
                possibleNextLeftEdge
            } else {
                null
            }
        }

        fun dump(): State {
            val lowest = getLowest()
            val highest = getHighest()
            return State(rockDump(lowest, highest), rockIndex, moveIndex)
        }

        val states = mutableMapOf<State, Pair<Long, Long>>()

        var warped = false

        while (rockCount < rockThreshold) {
            val r = rocks[rockIndex]
            rockIndex += 1
            rockIndex %= rocks.size

            var leftEdge = Point(2, getHighest() + 4L)
            do {
                val m = moves[moveIndex]
                moveIndex += 1
                moveIndex %= moves.size
                val newLeftEdge = applyJetIfPossible(m, r, leftEdge)
                leftEdge = newLeftEdge
                leftEdge = fall(r, leftEdge) ?: break
            } while (true)

            r.project(leftEdge).forEach { set(it) }

            val s = dump()

            if (!warped) {

                when (val v = states[s]) {
                    is Pair -> {

                        println("found the loop!")
                        println(s.rocks)
                        println("the lowest edge is ${getLowest()}")
                        val (prevLowest, prevRockCount) = v
                        val rockDelta = rockCount - prevRockCount
                        val curLowest = getLowest()
                        val heightDelta = curLowest - prevLowest
                        val cycleCount = (rockThreshold - rockCount) / rockDelta

                        println("Currently $rockCount rocks fell.")
                        println("$cycleCount times apply the cycle (len = $rockDelta).")
                        println("  height delta is $heightDelta: $prevLowest -> $curLowest")
                        println("  rock delta is $rockDelta: $prevRockCount -> $rockCount")

                        rockCount += cycleCount * rockDelta
                        // no need to change rockIndex nor moveIndex because it's included in the state.
                        println("Now $rockCount rocks fell")

                        val baseHeight = curLowest + heightDelta * cycleCount

                        println("The lowest was changed: $curLowest -> $baseHeight")

                        for ((dx, dy) in parseRockDump(s.rocks)) {
                            val x = dx
                            val y = baseHeight + dy
                            set(Point(x, y))
                            println("($x, $y)")
                        }

                        warped = true
                    }

                    else -> {
                        states[s] = Pair(getLowest(), rockCount)
                    }

                }
            }

            rockCount += 1
        }

//        val lines = rockDump(0L, getHighest()).split("\n")
//        val idx = getHighest()
//        for ((di, l) in lines.withIndex()) {
//            println("%03d: %s".format(idx - di, l))
//        }
//        println()

        return getHighest()
    }

    private fun rockDump(minY: Long, maxY: Long): String {
        val sb = StringBuilder()
        for (y in maxY downTo minY) {
            for (x in 0 until width) {
                if (isFilled(Point(x, y))) {
                    sb.append('#')
                } else {
                    sb.append('.')
                }
            }
            sb.append("\n")
        }
        return sb.toString().trim()
    }

    private fun parseRockDump(s: String): List<Point> {
        val lines = s.split("\n")
        return lines.flatMapIndexed { idx, line ->
            val y = lines.size - idx - 1L
            line.toCharArray().withIndex().filter { (_, c) ->
                c == '#'
            }.map() { (x, _) -> Point(x, y) }
        }
    }

    private fun getHighest(): Long {
        return heights.max()
    }

    private fun getLowest(): Long {
        return heights.min()
    }
}
