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

        override fun toString(): String {
            return "(%2d, %2d, %2d)".format(x, y, z)
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

    private val minX = points.minOf { it.x }
    private val maxX = points.maxOf { it.x }
    private val minY = points.minOf { it.y }
    private val maxY = points.maxOf { it.y }
    private val minZ = points.minOf { it.z }
    private val maxZ = points.maxOf { it.z }

    private fun solve(): Int {
        val visited = mutableSetOf<Point3>()
        val q = ArrayDeque<Point3>()

        val init = Point3(minX - 1, minY, minZ) // this cannot be a boundary

        q.addFirst(init)
        visited.add(init)

        fun isValidPoint(p: Point3): Boolean {
            return p.x in minX-1..maxX+1 && p.y in minY-1..maxY+1 && p.z in minZ-1..maxZ+1
        }

        var ans = 0

        while (q.isNotEmpty()) {
            val p = q.removeFirst()

            val valids = p.adjacentPoints().filter { isValidPoint(it) }
            val nexts = valids.filter { !pointSet.contains(it) }

            val diff = valids.size - nexts.size
            if (diff > 0) {
                ans += diff
            }
            for (nx in nexts) {
                if (!visited.contains(nx)) {
                    visited.add(nx)
                    q.addFirst(nx)
                }
            }
        }

        return ans
    }
}
