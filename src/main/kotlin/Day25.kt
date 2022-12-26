class Day25(private val snafus: List<Snafu>) {
    data class Snafu(val s: String) {
        val value = parse()

        fun parse(): Long {
            var base = 1L
            val r = s.toCharArray().reversed().fold(0L) { acc, c ->
                val l = c2l(c)
                val delta = base * l
                val ret = acc + delta
                base *= 5L
                ret
            }
            return r
        }

        companion object {
            fun c2l(c: Char): Long {
                return when (c) {
                    '=' -> -2L
                    '-' -> -1L
                    '0' -> 0L
                    '1' -> 1L
                    '2' -> 2L
                    else -> TODO("UNREACHABLE $c")
                }
            }

            fun fromLong(v: Long): Snafu {
                if (v == 0L) {
                    return Snafu("0")
                }
                var tmp = v
                // low -> high
                val base5 = mutableListOf<Long>()
                while (tmp != 0L) {
                    val r = tmp % 5
                    base5.add(r)
                    tmp /= 5
                }
                val arr = base5.map {
                    if (it > 2) {
                        Pair(1L, it - 5)
                    } else {
                        Pair(0L, it)
                    }
                }
                val reversedCounts = MutableList(arr.size + 1) { 0L }
                for ((idx, p) in arr.withIndex()) {
                    val (p1, p2) = p
                    reversedCounts[idx] += p2
                    reversedCounts[idx + 1] += p1
                }
                for (i in 0 until reversedCounts.size) {
                    if (reversedCounts[i] > 2) {
                        val newValue = reversedCounts[i] - 5
                        if (i + 1 < reversedCounts.size) {
                            reversedCounts[i] = newValue
                            reversedCounts[i + 1] += 1L
                        } else {
                            reversedCounts[i] = newValue
                            reversedCounts.add(1)
                        }
                    } else if (reversedCounts[i] < -2) {
                        val newValue = reversedCounts[i] + 5
                        reversedCounts[i] = newValue
                        reversedCounts[i + 1] -= 1L
                    }
                }
                assert(reversedCounts.all { it in -2..2 })
                val counts = reversedCounts.reversed().dropWhile { it == 0L }
                val s = counts.map {
                    when (it) {
                        -2L -> '='
                        -1L -> '-'
                        0L -> '0'
                        1L -> '1'
                        2L -> '2'
                        else -> TODO("UNREACHABLE")
                    }
                }.joinToString("")

                val ret = Snafu(s)
                assert(ret.value == v) { "$base5\n$counts" }

                return ret
            }
        }
    }

    companion object {
        fun exec() {
            val snafus = parse(readLines())
            val s = Day25(snafus)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): List<Snafu> {
            return lines.map { Snafu(it) }
        }
    }

    private fun solve(): String {
        snafus.forEach {
            val test = Snafu.fromLong(it.value)
            assert(it == test) { "${it.s} -> ${it.value} -> ${test.s} (== ${test.value})" }
        }
        val sum = snafus.sumOf { it.value }
        return Snafu.fromLong(sum).s
    }
}
