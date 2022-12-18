import kotlin.math.max

class Day16(private val valves: List<Valve>) {
    data class Valve(val name: String, val rate: Int, val tunnels: List<String>)
    data class State(
        val cur1: String, val restMinutes1: Int, val cur2: String, val restMinutes2: Int, val opened: List<String>
    ) {
        fun normalize(): State {
            return if (restMinutes1 == restMinutes2) {
                if (cur1 <= cur2) {
                    this
                } else {
                    State(cur2, restMinutes2, cur1, restMinutes1, opened)
                }
            } else if (restMinutes1 > restMinutes2) {
                this
            } else {
                State(cur2, restMinutes2, cur1, restMinutes1, opened)
            }
        }

        fun open(s: String): List<String> {
            val l = opened.toMutableList()
            l.add(s)
            return l.sorted()
        }
    }


    private val valvesByName = valves.associateBy { it.name }
    private val targetValveNames =
        valves.filter { it.name == "AA" || it.rate != 0 }.map { it.name }.sorted()
    private var valveToValveCostMap: Map<String, Map<String, Int>> = mapOf()

    companion object {
        fun exec() {
            val valves = parse(readLines())
            val s = Day16(valves)
            val ans = s.solve()
            println(ans)
        }

        private val regex = Regex("""^Valve ([A-Z]+) has flow rate=(\d+); tunnels? leads? to valves? (.*)$""")

        private fun parse(lines: List<String>): List<Valve> {
            return lines.map {
                val m = regex.matchEntire(it)!!
                val name = m.groups[1]!!.value
                val rate = m.groups[2]!!.value.toInt()
                val tunnels = m.groups[3]!!.value.split(", ")
                Valve(name, rate, tunnels)
            }
        }
    }

    private fun solve(): Int {
        build()

        val dp = List(27) { mutableMapOf<State, Int>() }

        val initialState = State("AA", 26, "AA", 26, listOf())
        dp[26][initialState] = 0
        var m = 26

        fun updateDp(s: State, point: Int) {
            val nextIndex = s.restMinutes1
            if (nextIndex in dp.indices) {
                val nextPoint = dp[nextIndex][s]
                if (nextPoint != null) {
                    dp[nextIndex][s] = max(nextPoint, point)
                } else {
                    dp[nextIndex][s] = point
                }
            }
        }

        var max = 0
        val threshold = if (valves.size == 10) {
            1651
        } else {
            2330
        }

        val pointMap = valves.filter { it.rate > 0 }.associate { Pair(it.name, it.rate) }

        fun possiblePoints(s: State): Int {
            return pointMap.entries.filter { (k, _) -> !s.opened.contains(k) }.sumOf { (k, v) ->
                if (s.cur1 == k) {
                    v * (s.restMinutes1 - 1)
                } else if (s.cur2 == k) {
                    v * (s.restMinutes1 - 1)
                } else {
                    val moveCost1 = valveToValveCostMap[s.cur1]!![k]!!
                    val moveCost2 = valveToValveCostMap[s.cur2]!![k]!!
                    max(
                        v * (s.restMinutes1 - moveCost1 - 1), v * (s.restMinutes2 - moveCost2 - 1)
                    )
                }

            }
        }

        while (m > 0) {
            while (dp[m].isNotEmpty()) {
                val s = dp[m].keys.first()
                val point = dp[m][s]!!

                if (point + possiblePoints(s) <= threshold) {
                    dp[m].remove(s) // done
                    // println("skipped $s")
                    continue
                }

                val curValve = valvesByName[s.cur1]!!
                if (curValve.rate != 0 && !s.opened.contains(curValve.name)) {
                    // open
                    val rest = s.restMinutes1 - 1
                    val newPoint = point + rest * curValve.rate
                    val newOpened = s.open(curValve.name)

                    if (rest > 0) {
                        max = max(newPoint, max)
                    }

                    // swap 1 and 2 if required
                    val newState = State(curValve.name, rest, s.cur2, s.restMinutes2, newOpened).normalize()
                    updateDp(newState, newPoint)
                }
                for ((nx, timeCost) in valveToValveCostMap[curValve.name]!!.entries) {
                    // swap if required
                    val newState = State(nx, s.restMinutes1 - timeCost, s.cur2, s.restMinutes2, s.opened).normalize()
                    updateDp(newState, point)
                }

                dp[m].remove(s)
            }
            m -= 1
            println(m)
        }
        return max
    }

    private fun build() {
        val p2p = mutableMapOf<String, Map<String, Int>>()
        for (p in targetValveNames) {
            val costMap = mutableMapOf(p to 0)
            val q = ArrayDeque<String>()
            q.addLast(p)
            while (q.isNotEmpty()) {
                val cur = q.removeFirst()
                val nextValves = valvesByName[cur]!!.tunnels.map { valvesByName[it]!! }
                for (nextValve in nextValves) {
                    if (costMap.containsKey(nextValve.name)) {
                        // visited
                        continue
                    } else {
                        costMap[nextValve.name] = costMap[cur]!! + 1
                        q.addLast(nextValve.name)
                    }
                }
            }

            val tmp = targetValveNames.toMutableSet()
            tmp.remove(p)
            p2p[p] = costMap.filter { tmp.contains(it.key) }
        }
        valveToValveCostMap = p2p
    }
}
