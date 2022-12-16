import kotlin.math.absoluteValue
import kotlin.math.max

class Day15(private val sensors: List<Sensor>) {
    data class Sensor(val x: Long, val y: Long, val closestBeacon: Pair<Long, Long>) {
        private val distance = distanceFromSensor(closestBeacon)

        fun xRangeOf(yy: Long): Pair<Long, Long>? {
            val rest = distance - (yy - y).absoluteValue
            if (rest < 0) {
                return null
            }

            return Pair(x - rest, x + rest)
        }

        private fun distanceFromSensor(p: Pair<Long, Long>): Long {
            val (xx, yy) = p
            return (xx - x).absoluteValue + (yy - y).absoluteValue
        }
    }

    companion object {
        fun exec() {
            val sensors = parse(readLines())
            val s = Day15(sensors)
            val ans = s.solve()
            println(ans)
        }

        private val regex = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")

        private fun parse(lines: List<String>): List<Sensor> {
            return lines.map {
                val m = regex.matchEntire(it)!!
                val x = m.groups[1]!!.value.toLong()
                val y = m.groups[2]!!.value.toLong()
                val xx = m.groups[3]!!.value.toLong()
                val yy = m.groups[4]!!.value.toLong()
                Sensor(x, y, Pair(xx, yy))
            }
        }
    }

    // 😅
    private val len = if (sensors.size == 14) {
        20L
    } else {
        4000000L
    }

    private fun solve(): Long {
        for (y in 0..len) {
            val skippables = mutableListOf<Pair<Long, Long>>()
            for (sensor in sensors) {
                val (x1, x2) = sensor.xRangeOf(y) ?: continue
                skippables.add(Pair(x1, x2))
            }
            var x = 0L
            val sorted = skippables.sortedBy { it.first }
            for ((beg, end) in sorted) {
                if (beg <= x) {
                    x = max(x, end + 1)
                } else {
                    if (x > len) {
                        break
                    }
                    // found
                    return x * 4000000 + y
                }
            }
        }
        assert(false) { "should not reach here, no answer found" }
        return 0
    }
}
