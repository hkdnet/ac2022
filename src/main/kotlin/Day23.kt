class Day23(private val initialElves: MutableSet<Point>) {
    data class Point(val x: Int, val y: Int) {
        fun checkPoints(d: Direction): List<Point> {
            val deltas = when (d) {
                Direction.N -> listOf(Point(-1, -1), Point(-1, 0), Point(-1, 1))
                Direction.S -> listOf(Point(1, -1), Point(1, 0), Point(1, 1))
                Direction.E -> listOf(Point(-1, 1), Point(0, 1), Point(1, 1))
                Direction.W -> listOf(Point(-1, -1), Point(0, -1), Point(1, -1))
            }
            return deltas.map { it + this }
        }

        fun stepTo(d: Direction): Point {
            val delta = when (d) {
                Direction.N -> Point(-1, 0)
                Direction.S -> Point(1, 0)
                Direction.E -> Point(0, 1)
                Direction.W -> Point(0, -1)
            }

            return this + delta
        }

        fun eightAdjacent(): List<Point> {
            val deltas = listOf(
                Point(-1, -1),
                Point(-1, 0),
                Point(-1, 1),
                Point(0, 1),
                Point(0, -1),
                Point(1, -1),
                Point(1, 0),
                Point(1, 1),
            )
            return deltas.map { it + this }
        }

        operator fun plus(p: Point): Point {
            return Point(x + p.x, y + p.y)
        }
    }

    companion object {
        fun exec() {
            val elves = parse(readLines())
            val s = Day23(elves)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): MutableSet<Point> {
            val m = mutableSetOf<Point>()
            lines.withIndex().flatMap { (x, line) ->
                line.toCharArray().withIndex().filter { (_, c) -> c == '#' }.map { (y, _) ->
                    println("$x $y")
                    m.add(Point(x, y))
                }
            }
            return m
        }
    }

    enum class Direction {
        N, S, W, E
    }

    private val directions = listOf(Direction.N, Direction.S, Direction.W, Direction.E)


    private fun display(elves: Collection<Point>) {
        val minX = elves.minOf { it.x }
        val maxX = elves.maxOf { it.x }
        val minY = elves.minOf { it.y }
        val maxY = elves.maxOf { it.y }

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if (elves.contains(Point(x, y))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    private fun solve(): Int {
        val rotateDirections = run {
            var directionIndex = 0
            {
                ->
                val ret = directions.indices.map { delta -> directions[(directionIndex + delta) % directions.size] }
                directionIndex += 1
                ret
            }
        }
        val cycles = 10

        var elves = initialElves.toSet()

        display(elves)

        repeat(cycles) {
            println("===${it + 1} Cycle starts")
            val curDirections = rotateDirections()
            println("Use this: $curDirections")
            val nextPoints = mutableMapOf<Point, Point>() // next -> orig
            val duplicates = mutableMapOf<Point, Point>() // orig -> next
            for (elf in elves) {
                println("Considering $elf")
                if (elf.eightAdjacent().all { !elves.contains(it) }) {
                    duplicates[elf] = elf
                    continue
                }
                var isSet = false
                for (d in curDirections) {
                    if (isSet) {
                        break
                    }
                    println("Checking $d")
                    if (elf.checkPoints(d).all { !elves.contains(it) }) {
                        println("OK!")
                        // The elf can go to the direction.
                        val nextPoint = elf.stepTo(d)
                        if (nextPoints.contains(nextPoint)) {
                            duplicates[elf] = nextPoint
                        } else {
                            nextPoints[nextPoint] = elf
                        }
                        isSet = true
                    }
                }
                if (!isSet) {
                    duplicates[elf] = elf
                }
            }
            val nextElves = mutableSetOf<Point>()
            for ((nx, orig) in nextPoints) {
                if (!duplicates.containsValue(nx)) {
                    nextElves.add(nx)
                } else {
                    nextElves.add(orig)
                }
            }

            println("nextElves: $nextPoints")
            println("duplicates: $duplicates")
            duplicates.keys.toCollection(nextElves)
            assert(elves.size == nextElves.size) { "elf disappeared!?" }
            elves = nextElves

            println("=== ${it + 1}th round finished.")
            display(elves)
        }

        val minX = elves.minOf { it.x }
        val maxX = elves.maxOf { it.x }
        val minY = elves.minOf { it.y }
        val maxY = elves.maxOf { it.y }

        println("X: $minX - $maxX, Y: $minY - $maxY")
        return (maxX - minX + 1) * (maxY - minY + 1) - elves.size
    }
}
