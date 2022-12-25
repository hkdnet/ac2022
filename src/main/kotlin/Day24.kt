class Day24(private val field: Field) {
    enum class Direction {
        N, E, S, W
    }

    data class Wind(val d: Direction, val startAt: Vec2)
    data class Field(val start: Vec2, val goal: Vec2, val winds: List<Wind>)

    companion object {
        fun exec() {
            val f = parse(readLines())
            val s = Day24(f)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): Field {
            val start = Vec2(0, 0)
            val goal = Vec2(lines.size - 1, lines[0].length - 3)
            val winds = mutableListOf<Wind>()
            // read all lines except the first and the last
            for (x in 1 until lines.size - 1) {
                for ((idx, c) in lines[x].toCharArray().withIndex()) {
                    val y = idx - 1
                    when (c) {
                        '^' -> winds.add(Wind(Direction.N, Vec2(x, y)))
                        '>' -> winds.add(Wind(Direction.E, Vec2(x, y)))
                        'v' -> winds.add(Wind(Direction.S, Vec2(x, y)))
                        '<' -> winds.add(Wind(Direction.W, Vec2(x, y)))
                        '.', '#' -> {} // do nothing
                        else -> {
                            TODO("UNREACHABLE $c")
                        }
                    }
                }
            }
            return Field(start, goal, winds)
        }
    }

    private val width = field.goal.y
    private val height = field.goal.x
    private val cycleLength = lcm(width + 1, height - 1)

    fun solve(): Int {
        val nWinds = field.winds.filter { it.d == Direction.N }
        val sWinds = field.winds.filter { it.d == Direction.S }
        val eWinds = field.winds.filter { it.d == Direction.E }
        val wWinds = field.winds.filter { it.d == Direction.W }

        fun normalizeWind(p: Vec2): Vec2 {
            val (x, y) = p
            if (x <= 0) {
                return normalizeWind(Vec2(height + x - 1, y))
            }
            if (x >= height) {
                return normalizeWind(Vec2(x - height + 1, y))
            }
            if (y < 0) {
                return normalizeWind(Vec2(x, width + y + 1))
            }
            if (y > width) {
                return normalizeWind(Vec2(x, y - width - 1))
            }
            return p
        }

        val windsByCycle = (0 until cycleLength).map {
            val nDelta = Vec2(-1, 0) * it
            val sDelta = Vec2(1, 0) * it
            val eDelta = Vec2(0, 1) * it
            val wDelta = Vec2(0, -1) * it
            val s = mutableSetOf<Vec2>()
            nWinds.mapTo(s) { wind -> normalizeWind(wind.startAt + nDelta) }
            sWinds.mapTo(s) { wind -> normalizeWind(wind.startAt + sDelta) }
            eWinds.mapTo(s) { wind -> normalizeWind(wind.startAt + eDelta) }
            wWinds.mapTo(s) { wind -> normalizeWind(wind.startAt + wDelta) }
            s
        }

        fun nextPoints(p: Vec2): List<Vec2> {
            val (x, y) = p
            if (x == 0) {
                assert(y == 0) { "($x, $y) is passed but it should not be allowed!" }
                return listOf(Vec2(1, y))
            }
            val l = mutableListOf<Vec2>(p)
            if (p == field.goal + Vec2(-1, 0)) {
                l.add(field.goal)
            }
            if (x < height - 1) {
                l.add(p + Vec2(1, 0))
            }
            if (x > 1) {
                l.add(p + Vec2(-1, 0))
            }
            if (y > 0) {
                l.add(p + Vec2(0, -1))
            }
            if (y < width) {
                l.add(p + Vec2(0, 1))
            }
            return l
        }

        val q = ArrayDeque<Pair<Int, Vec2>>()
        q.addLast(Pair(0, Vec2(0, 0)))


        val visitedByCycle = List(cycleLength) { mutableSetOf<Vec2>() }

        while (q.isNotEmpty()) {
            val (step, cur) = q.removeFirst()
            val nextStep = step + 1

            val state = nextStep % windsByCycle.size
            val winds = windsByCycle[state]

            val visited = visitedByCycle[state]

            for (nx in nextPoints(cur)) {
                if (winds.contains(nx)) {
                    continue
                }
                if (nx == field.goal) {
                    return nextStep
                }

                if (!visited.contains(nx)) {
                    q.addLast(Pair(nextStep, nx))
                    visited.add(nx)
                }
            }
        }
        println("not reached to the goal!!!!!")
        return -1
    }
}
