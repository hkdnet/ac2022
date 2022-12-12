class Day12(val f: List<CharArray>) {
    companion object {
        fun exec() {
            val f = parse(readLines())
            val s = Day12(f)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): List<CharArray> {
            return lines.map { it.toCharArray() }
        }
    }

    private val h = f.size
    private val w = f[0].size
    private val start = findStart()
    private val end = findEnd()
    private val steps = MutableList(h) { MutableList(w) { 1000000000 } }

    private fun findStart(): Pair<Int, Int> {
        return findChar('S')
    }

    private fun findEnd(): Pair<Int, Int> {
        return findChar('E')
    }

    private fun findChar(c: Char): Pair<Int, Int> {
        for (x in 0 until h) {
            for (y in 0 until w) {
                if (f[x][y] == c) {
                    return Pair(x, y)
                }
            }
        }
        throw Exception("should not reachable")
    }

    private fun stepOf(cur: Pair<Int, Int>): Int {
        val (x, y) = cur
        return steps[x][y]
    }

    private fun setStepOf(cur: Pair<Int, Int>, step: Int) {
        val (x, y) = cur
        steps[x][y] = step
    }

    private fun solve(): Int {
        normalize()
        val q = ArrayDeque<Pair<Int, Int>>()
        setStepOf(start, 0)
        q.addLast(start)
        while (q.isNotEmpty()) {
            val cur = q.removeFirst()
            val curStep = stepOf(cur)
            val newStep = curStep + 1
            for (reachable in reachables(cur)) {
                if (reachable == end) {
                    return newStep
                }
                if (stepOf(reachable) > newStep) {
                    setStepOf(reachable, newStep)
                    q.addLast(reachable)
                }
            }
        }

        return -1
    }
    private fun normalize() {
        val (sx, sy) = start
        f[sx][sy] = 'a'
        val (ex, ey) = end
        f[ex][ey] = 'z'
    }

    private fun reachables(cur: Pair<Int, Int>): List<Pair<Int, Int>> {
        val (x, y) = cur
        val l = listOf(Pair(x - 1, y), Pair(x + 1, y), Pair(x, y - 1), Pair(x, y + 1))
        return l.filter { (xx, yy) ->
            if (
                xx !in 0 until h || yy !in 0 until w
            ) {
                false
            } else {
                f[xx][yy] <= f[x][y] + 1
                // f[xx][yy] == f[x][y] || f[xx][yy] == f[x][y] + 1
            }
        }
    }

}
