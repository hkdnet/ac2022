class Day18(private val points: List<Point3>) {
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
        val s = mutableSetOf<Point3>()
        var ans = 0
        for (p in points) {
            s.add(p)
            val faces = p.adjacentPoints().count { s.contains(it) }
            ans += 6
            ans -= 2 * faces
        }
        return ans
    }
}
