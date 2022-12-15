import kotlin.math.absoluteValue

class Day15(private val sensors: List<Sensor>) {
    data class Sensor(val x: Int, val y: Int, val closestBeacon: Pair<Int, Int>) {
        val distance = distanceFromSensor(closestBeacon)

        fun xRangeOf(yy: Int): Pair<Int, Int>? {
            val rest = distance - (yy - y).absoluteValue
            if (rest <= 0) {
                return null
            }

            println("beacon at ($x, $y), distance = $distance -> ${x - rest} - ${x + rest}")
            return Pair(x - rest, x + rest)
        }

        private fun distanceFromSensor(p: Pair<Int, Int>): Int {
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
                val x = m.groups[1]!!.value.toInt()
                val y = m.groups[2]!!.value.toInt()
                val xx = m.groups[3]!!.value.toInt()
                val yy = m.groups[4]!!.value.toInt()
                Sensor(x, y, Pair(xx, yy))
            }
        }
    }

    // 😅
    private val targetY = if (sensors.size == 14) {
        10
    } else {
        2000000
    }

    private val minX = sensors.minOf { it.x - it.distance }
    private val maxX = sensors.maxOf { it.x + it.distance }

    private fun solve(): Int {
        fun idxOf(x: Int): Int {
            return x - minX
        }

        val xs = MutableList(maxX - minX + 2) { 0 }
        for (sensor in sensors) {
            val r = sensor.xRangeOf(targetY)
            if (r != null) {
                val (x1, x2) = r
                xs[idxOf(x1)] += 1
                xs[idxOf(x2 + 1)] -= 1
            }
        }
        for (i in 0 until xs.size - 1) {
            xs[i + 1] += xs[i]
        }
//
//        val msg = xs.map {
//            if (it > 0) {
//                '#'
//            } else {
//                '.'
//            }
//        }.joinToString("")
//        println(msg)
        val okXs = xs.withIndex().filter { (_, v) ->
            v > 0
        }.map { (idx, _) -> idx + minX }.filter { x ->
            !sensors.any { sensor ->
                Pair(x, targetY) == sensor.closestBeacon ||
                        Pair(x, targetY) == Pair(sensor.x, sensor.y)
            }
        }

        return okXs.size
    }
}
