class Day22(private val field: List<List<Cell>>, private val instructions: List<Instruction>) {
    sealed interface Instruction
    data class Step(val n: Int) : Instruction
    object Right : Instruction
    object Left : Instruction

    data class Point(val x: Int, val y: Int) {
        operator fun plus(o: Point): Point {
            return Point(x + o.x, y + o.y)
        }
    }

    enum class Cell {
        Wall, Tile, Empty;

        companion object {
            fun fromChar(c: Char): Cell {
                return when (c) {
                    '#' -> Cell.Wall
                    '.' -> Cell.Tile
                    ' ' -> Cell.Empty
                    else -> {
                        TODO("unreachable!!!")
                    }
                }
            }
        }
    }

    enum class Direction(val v: Int) {
        E(0), S(1), W(2), N(3);

        fun toRight(): Direction {
            return when (this) {
                E -> S
                S -> W
                W -> N
                N -> E
            }
        }

        fun toLeft(): Direction {
            return when (this) {
                E -> N
                S -> E
                W -> S
                N -> W
            }
        }

        fun toVec(): Point {
            return when (this) {
                E -> Point(0, 1)
                S -> Point(1, 0)
                W -> Point(0, -1)
                N -> Point(-1, 0)
            }
        }
    }

    companion object {
        fun exec() {
            val (field, insns) = parse(readLines())
            val s = Day22(field, insns)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): Pair<List<List<Cell>>, List<Instruction>> {
            val fieldLines = mutableListOf<String>()
            var idx = 0
            while (lines[idx] != "") {
                fieldLines.add(lines[idx])
                idx += 1
            }
            val maxX = fieldLines.maxOfOrNull { it.length }!!
            val f = fieldLines.map {
                val arr = it.toCharArray()
                (0 until maxX).map { x ->
                    if (x in arr.indices) {
                        Cell.fromChar(arr[x])
                    } else {
                        Cell.Empty
                    }
                }
            }
            idx += 1 // skip the empty line

            val cs = lines[idx].toCharArray()
            val insns = mutableListOf<Instruction>()
            var charIndex = 0
            var tmp = ""
            fun clearTmp() {
                if (tmp != "") {
                    insns.add(Step(tmp.toInt()))
                    tmp = ""
                }
            }
            while (charIndex < cs.size) {
                when (val c = cs[charIndex]) {
                    in '0'..'9' -> tmp += c
                    'L' -> {
                        clearTmp()
                        insns.add(Left)
                    }

                    'R' -> {
                        clearTmp()
                        insns.add(Right)
                    }

                    else -> {
                        TODO("unreachable!")
                    }
                }
                charIndex += 1
            }
            clearTmp()

            return Pair(f, insns)
        }
    }

    private val startPoint
        get() = Point(0, field[0].indexOf(Cell.Tile))

    private val xMinMap = mutableMapOf<Int, Int>()
    private val xMaxMap = mutableMapOf<Int, Int>()
    private val yMinMap = mutableMapOf<Int, Int>()
    private val yMaxMap = mutableMapOf<Int, Int>()

    private val xMin = 0
    private val xMax = field.size - 1
    private val yMin = 0
    private val yMax = field[0].size - 1

    private fun solve(): Int {
        var p = startPoint
        var d = Direction.E

        println("start at $p, direction is $d")
        for (insn in instructions) {
            println("consume $insn")
            when (insn) {
                Left -> d = d.toLeft()
                Right -> d = d.toRight()
                is Step -> {
                    for (i in 0 until insn.n) {
                        println("step ${i + 1}")
                        val nextPoint = step(p, d)
                        if (p == nextPoint) {
                            println("$p -> $nextPoint, failed")
                            break
                        }
                        println("$p -> $nextPoint, OK")
                        p = nextPoint
                    }
                }
            }

            println("$p, $d")
        }
        return answer(p, d)
    }

    private fun step(p: Point, d: Direction): Point {
        val nextPoint = normalizePoint(p + d.toVec())
        return when (cellOf(nextPoint)) {
            Cell.Tile -> nextPoint
            Cell.Wall -> p
            Cell.Empty -> {
                val warpPoint = warp(p, d)
                val warpDestination = cellOf(warpPoint)
                println("warped to $p -> $warpPoint($warpDestination)")
                if (warpDestination == Cell.Tile) {
                    warpPoint
                } else {
                    assert(warpDestination == Cell.Wall) { "warp destination must be tile or wall but $warpDestination" }
                    p
                }
            }
        }
    }

    private fun normalizePoint(p: Point): Point {
        if (p.x < 0) {
            return normalizePoint(Point(xMax + p.x + 1, p.y))
        }
        if (p.x > xMax) {
            val delta = p.x - xMax
            return normalizePoint(Point(delta - 1, p.y))
        }
        if (p.y < 0) {
            return normalizePoint(Point(p.x, yMax + p.y + 1))
        }
        if (p.y > yMax) {
            val delta = p.y - yMax
            return normalizePoint(Point(p.x, delta - 1))
        }

        return p
    }

    private fun warp(p: Point, d: Direction): Point {
        return when (d) {
            Direction.N -> Point(xMaxOf(p.y), p.y)
            Direction.E -> Point(p.x, yMinOf(p.x))
            Direction.S -> Point(xMinOf(p.y), p.y)
            Direction.W -> Point(p.x, yMaxOf(p.x))
        }
    }

    private fun xMaxOf(y: Int): Int {
        return xMaxMap.getOrPut(y) {
            for (x in xMax downTo xMin) {
                if (cellOf(Point(x, y)) != Cell.Empty) {
                    return x
                }
            }
            TODO("unreachable")
        }
    }

    private fun xMinOf(y: Int): Int {
        return xMinMap.getOrPut(y) {
            for (x in xMin..xMax) {
                if (cellOf(Point(x, y)) != Cell.Empty) {
                    return x
                }
            }
            TODO("unreachable")
        }
    }

    private fun yMaxOf(x: Int): Int {
        return yMaxMap.getOrPut(x) {
            for (y in yMax downTo yMin) {
                if (cellOf(Point(x, y)) != Cell.Empty) {
                    return y
                }
            }
            TODO("unreachable")
        }
    }

    private fun yMinOf(x: Int): Int {
        return yMinMap.getOrPut(x) {
            for (y in yMin..yMax) {
                if (cellOf(Point(x, y)) != Cell.Empty) {
                    return y
                }
            }
            TODO("unreachable")
        }
    }


    private fun cellOf(p: Point): Cell {
        val (x, y) = p
        return field[x][y]
    }

    private fun answer(p: Point, d: Direction): Int {
        val (x, y) = p
        return 1000 * (x + 1) + 4 * (y + 1) + d.v
    }
}
