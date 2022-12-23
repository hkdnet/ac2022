import java.awt.PointerInfo

class Day22(private val field: List<List<Cell>>, private val instructions: List<Instruction>) {
    sealed interface Instruction
    data class Step(val n: Int) : Instruction
    object Right : Instruction
    object Left : Instruction

    data class Point(val x: Int, val y: Int) {
        operator fun plus(o: Point): Point {
            return Point(x + o.x, y + o.y)
        }

        operator fun times(v: Int): Point {
            return Point(x * v, y * v)
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

        fun opposite(): Direction {
            return toLeft().toLeft()
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

    private val xMin = 0
    private val xMax = field.size - 1
    private val yMin = 0
    private val yMax = field[0].size - 1

    /*
        o13
        o2o
        46o
        5oo

        1N -> 5W
        1E -> 3W
        1W -> 4W
        1S -> 2N
        2N -> 1S
        2E -> 3S
        2W -> 4N
        2S -> 6N
        3N -> 5S
        3E -> 6E
        3W -> 1E
        3S -> 2E
        4N -> 2W
        4E -> 6W
        4W -> 1W
        4S -> 5N
        5N -> 4S
        5E -> 6S
        5W -> 1N
        5S -> 3N
        6N -> 2S
        6E -> 3E
        6W -> 4E
        6S -> 5E
     */
    private val f2f: Map<Pair<Int, Direction>, Pair<Int, Direction>> = mapOf(
        Pair(1, Direction.N) to Pair(5, Direction.W), Pair(1, Direction.E) to Pair(3, Direction.W),
        Pair(1, Direction.W) to Pair(4, Direction.W), Pair(1, Direction.S) to Pair(2, Direction.N),
        Pair(2, Direction.N) to Pair(1, Direction.S), Pair(2, Direction.E) to Pair(3, Direction.S),
        Pair(2, Direction.W) to Pair(4, Direction.N), Pair(2, Direction.S) to Pair(6, Direction.N),
        Pair(3, Direction.N) to Pair(5, Direction.S), Pair(3, Direction.E) to Pair(6, Direction.E),
        Pair(3, Direction.W) to Pair(1, Direction.E), Pair(3, Direction.S) to Pair(2, Direction.E),
        Pair(4, Direction.N) to Pair(2, Direction.W), Pair(4, Direction.E) to Pair(6, Direction.W),
        Pair(4, Direction.W) to Pair(1, Direction.W), Pair(4, Direction.S) to Pair(5, Direction.N),
        Pair(5, Direction.N) to Pair(4, Direction.S), Pair(5, Direction.E) to Pair(6, Direction.S),
        Pair(5, Direction.W) to Pair(1, Direction.N), Pair(5, Direction.S) to Pair(3, Direction.N),
        Pair(6, Direction.N) to Pair(2, Direction.S), Pair(6, Direction.E) to Pair(3, Direction.E),
        Pair(6, Direction.W) to Pair(4, Direction.E), Pair(6, Direction.S) to Pair(5, Direction.E)
    )

    private fun check() {
        assert(f2f.keys.size == f2f.values.size)
        for (k in f2f.keys) {
            assert(f2f[f2f[k]] == k)
        }
    }

    private fun solve(): Int {
        check()
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
                        val (nextPoint, nextDirection) = step(p, d)
                        if (p == nextPoint) {
                            println("$p -> $nextPoint, failed")
                            break
                        }
                        println("$p -> $nextPoint and $d -> $nextDirection OK")
                        p = nextPoint
                        d = nextDirection
                    }
                }
            }

            println("$p, $d")
        }
        return answer(p, d)
    }

    private fun step(p: Point, d: Direction): Pair<Point, Direction> {
        val nextPoint = p + d.toVec()

        fun doWarp(): Pair<Point, Direction> {
            val (warpPoint, warpDirection) = warp(p, d)
            print("warped to $p -> $warpPoint")
            val warpDestination = cellOf(warpPoint)
            println("($warpDestination)")
            return if (warpDestination == Cell.Tile) {
                Pair(warpPoint, warpDirection)
            } else {
                assert(warpDestination == Cell.Wall) { "warp destination must be tile or wall but $warpDestination" }
                Pair(p, d)
            }
        }

        if (nextPoint.x !in xMin..xMax || nextPoint.y !in yMin..yMax) {
            println("reached the edge")
            return doWarp()
        }

        return when (cellOf(nextPoint)) {
            Cell.Empty -> doWarp()
            Cell.Tile -> Pair(nextPoint, d)
            Cell.Wall -> Pair(p, d)
        }
    }

    /*
    o13
    o2o
    46o
    5oo
     */
    private val faceIdToPoint = mapOf(
        1 to Point(0, 1),
        3 to Point(0, 2),
        2 to Point(1, 1),
        4 to Point(2, 0),
        6 to Point(2, 1),
        5 to Point(3, 0)
    )
    private val pointToFaceId = faceIdToPoint.map { (k, v) -> Pair(v, k) }.toMap()
    private fun faceOf(p: Point): Int {
        val (x, y) = p
        val xx = x / 50
        val yy = y / 50
        return pointToFaceId[Point(xx, yy)]!!
    }

    private fun warp(p: Point, d: Direction): Pair<Point, Direction> {
        val (x, y) = p
        val f = faceOf(p)
        val distanceFromLeftEdge = when (d) {
            Direction.N -> y % 50
            Direction.S -> 49 - y % 50
            Direction.E -> x % 50
            Direction.W -> 49 - x % 50
        }
        val (destFace, destFaceEdge) = f2f[Pair(f, d)]!!
        println("$p on $f(edge: $d) -> $destFace(edge: $destFaceEdge)")
        println("The point is far from the left edge by $distanceFromLeftEdge")
        val destDirection =
            destFaceEdge.opposite() // We reached destFaceEdge. It means we will go the opposite direction.
        println("going to $destDirection")
        val destRightEdge = rightEdgeOf(destFace, destFaceEdge)
        val destPoint = destRightEdge + when (destFaceEdge) {
            Direction.N -> Point(0, -distanceFromLeftEdge)
            Direction.E -> Point(-distanceFromLeftEdge, 0)
            Direction.W -> Point(distanceFromLeftEdge, 0)
            Direction.S -> Point(0, distanceFromLeftEdge)
        }
        println("reached $destPoint")

        return Pair(destPoint, destDirection)
    }


    /*
    o13
    o2o
    46o
    5oo
     */
    private val len = 50
    private fun rightEdgeOf(faceId: Int, d: Direction): Point {
        val delta = when (d) {
            Direction.N -> Point(0, len - 1)
            Direction.E -> Point(len - 1, len - 1)
            Direction.W -> Point(0, 0)
            Direction.S -> Point(len - 1, 0)
        }
        val base = faceIdToPoint[faceId]!! * 50
        return base + delta
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
