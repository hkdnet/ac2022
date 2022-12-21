class Day18(private val points: List<Point3>) {
    private val pointSet = points.toSet()

    data class Point3(val x: Int, val y: Int, val z: Int) {
        fun adjacentPoints(): List<Point3> {
            return listOf(
                Point3(x + 1, y, z),
                Point3(x - 1, y, z),
                Point3(x, y + 1, z),
                Point3(x, y - 1, z),
                Point3(x, y, z + 1),
                Point3(x, y, z - 1),
            )
        }

        operator fun plus(p: Point3): Point3 {
            return Point3(x + p.x, y + p.y, z + p.z)
        }

        operator fun times(v: Int): Point3 {
            return Point3(x * v, y * v, z * v)
        }

    }

    enum class Axis {
        X {
            override val unit = Point3(1, 0, 0)
        },
        Y {
            override val unit: Point3 = Point3(0, 1, 0)
        },
        Z {
            override val unit: Point3 = Point3(0, 0, 1)
        };

        abstract val unit: Point3
    }

    enum class Sign {
        Plus, Minus;

        val reverse: Sign
            get() = if (this == Plus) {
                Minus
            } else {
                Plus
            }
    }

    enum class AxisPair(private val axis: Axis, private val sign: Sign) {
        XPlus(Axis.X, Sign.Plus),
        XMinus(Axis.X, Sign.Minus),
        YPlus(Axis.Y, Sign.Plus),
        YMinus(Axis.Y, Sign.Minus),
        ZPlus(Axis.Z, Sign.Plus),
        ZMinus(Axis.Z, Sign.Minus);

        fun goTo(dest: AxisPair): Pair<Pair<Point3, AxisPair>, Pair<Point3, AxisPair>>? {
            if (axis == dest.axis) {
                return null
            }
            var stepTo = axis.unit
            if (sign == Sign.Minus) {
                stepTo *= -1
            }
            var base = dest.axis.unit
            if (dest.sign == Sign.Plus) {
                base *= -1
            }
            return Pair(
                Pair(base + stepTo, dest),
                Pair(base, this)
            )
        }

        private fun find(axis: Axis, sign: Sign): AxisPair {
            return AxisPair.valueOf("${axis}${sign.reverse}")
        }

        val reverse: AxisPair
            get() = find(axis, sign)
    }

    data class Face(val p: Point3, val ap: AxisPair) {
        fun checkers(): List<Checker> {
            val l = mutableListOf<Checker>()
            for (toSurface in AxisPair.values()) {
                val (firstCond, secondCond) = ap.goTo(toSurface) ?: continue

                l.add(
                    IfElseChecker(
                        p + firstCond.first,
                        firstCond.second,
                        IfElseChecker(
                            p + secondCond.first,
                            secondCond.second,
                            AlwaysTrueChecker(
                                Face(p, firstCond.second.reverse)
                            )
                        )
                    )
                )
            }
            return l
        }
    }

    interface Checker {
        fun nextFace(points: Set<Point3>): Face
    }

    data class AlwaysTrueChecker(val f: Face) : Checker {
        override fun nextFace(points: Set<Point3>): Face {
            return f
        }

    }

    data class IfElseChecker(val target: Point3, val ok: AxisPair, val elseChecker: Checker) : Checker {
        override fun nextFace(points: Set<Point3>): Face {
            return if (points.contains(target)) {
                Face(target, ok)
            } else {
                elseChecker.nextFace(points)
            }
        }
    }

    companion object {
        fun exec() {
            val points = parse(readLines())
            val s = Day18(points)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): List<Point3> {
            return lines.map {
                val arr = it.split(",").map(String::toInt)
                Point3(arr[0], arr[1], arr[2])
            }
        }
    }

    private fun solve(): Int {
        val pointWithMinX = points.minByOrNull { it.x }!!
        val visited = mutableSetOf<Face>()

        val q = ArrayDeque<Face>()
        val sFace = Face(pointWithMinX, AxisPair.XMinus)
        q.addLast(sFace)
        visited.add(sFace)

        var ans = 0

        while (q.isNotEmpty()) {
            val f = q.removeFirst()
            ans += 1
            for (checker in f.checkers()) {
                val nextFace = checker.nextFace(pointSet)
                if (!visited.contains(nextFace)) {
                    q.addLast(nextFace)
                    visited.add(nextFace)
                }
            }
        }
        return ans
    }
}
