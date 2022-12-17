import kotlin.math.max

class Day16(private val valves: List<Valve>) {
    data class Valve(val name: String, val rate: Int, val tunnels: List<String>)
    data class State(val cur: String, val restMinutes: Int, val opened: List<String>) {
        companion object {
            fun load(s: String): State {
                val arr = s.split("$")

                return State(
                    arr[0],
                    arr[1].toInt(),
                    arr[2].split(",")
                )
            }
        }

        fun dump(): String {
            return "$cur$${restMinutes}$${opened.joinToString(",")}"
        }
    }

    private val valvesByName = valves.associateBy { it.name }
    private val targetValveNames =
        valves.filter { it.name == "AA" || it.rate != 0 }.sortedBy { it.name }.map { it.name }
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

        val dp = List(31) { mutableMapOf<String, Int>() }

        val initialState = State("AA", 30, listOf())
        dp[0][initialState.dump()] = 0
        var m = 0
        while (m <= 30) {
            for ((stateDump, point) in dp[m]) {
                val s = State.load(stateDump)
                val curValve = valvesByName[s.cur]!!
                if (curValve.rate != 0 && !s.opened.contains(curValve.name)) {
                    // open
                    val rest = s.restMinutes - 1
                    val newPoint = point + rest * curValve.rate
                    val newOpened = s.opened.toMutableList()
                    newOpened.add(curValve.name)
                    val newState = State(curValve.name, rest, newOpened.sorted())
                    val newDump = newState.dump()
                    val nextIndex = m + 1
                    if (nextIndex < dp.size) {
                        val nextPoint = dp[nextIndex][newDump]
                        if (nextPoint != null) {
                            dp[nextIndex][newDump] = max(nextPoint, newPoint)
                        } else {
                            dp[nextIndex][newDump] = newPoint
                        }
                    }
                }
                for ((nx, timeCost) in valveToValveCostMap[curValve.name]!!.entries) {
                    val newState = State(nx, s.restMinutes - timeCost, s.opened)
                    val newDump = newState.dump()
                    val nextIndex = m + timeCost
                    if (nextIndex < dp.size) {
                        val nextPoint = dp[nextIndex][newDump]
                        if (nextPoint != null) {
                            dp[nextIndex][newDump] = max(nextPoint, point)
                        } else {
                            dp[nextIndex][newDump] = point
                        }
                    }
                }
            }
            m += 1
        }

        return dp.maxOf { it.values.maxOrNull() ?: 0 }
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
