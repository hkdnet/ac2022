import java.util.PriorityQueue
import kotlin.math.max
import kotlin.math.min

class Day19(private val bluePrints: List<BluePrint>) {
    data class BluePrint(
        val id: Int,
        val oreRobotCost: Counts,
        val clayRobotCost: Counts,
        val obsidianRobotCost: Counts,
        val geodeRobotCost: Counts
    ) {
        private val allCosts = listOf(oreRobotCost, clayRobotCost, obsidianRobotCost, geodeRobotCost)
        val maxOreCost: Int = allCosts.maxOf { it.ore }
        val maxClayCost: Int = allCosts.maxOf { it.clay }
        val maxObsidianCost: Int = allCosts.maxOf { it.obsidian }
    }

    companion object {
        fun exec() {
            val bluePrints = parse(readLines())
            val s = Day19(bluePrints)
            val ans = s.solve()
            println(ans)
        }

        private val pattern =
            """Blueprint \d+: Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()

        private fun parse(lines: List<String>): List<BluePrint> {
            return lines.withIndex().map { (idx, line) ->
                val m = pattern.matchEntire(line)!!
                BluePrint(
                    idx + 1,
                    Counts(m.groups[1]!!.value.toInt(), 0, 0, 0),
                    Counts(m.groups[2]!!.value.toInt(), 0, 0, 0),
                    Counts(m.groups[3]!!.value.toInt(), m.groups[4]!!.value.toInt(), 0, 0),
                    Counts(
                        m.groups[5]!!.value.toInt(), 0, m.groups[6]!!.value.toInt(), 0
                    )
                )
            }
        }
    }

    private fun solve(): Int {
        val arr = bluePrints.take(3).map { bestGeodeCount(it) }
        println("1: ${arr[0]}")
        println("2: ${arr[1]}")
        if (arr.size > 2) {
            println("3: ${arr[2]}")
        }

        return arr[0] * arr[1] * arr[2]
    }

    data class Counts(val ore: Int, val clay: Int, val obsidian: Int, val geode: Int) {
        operator fun plus(c: Counts): Counts {
            return Counts(ore + c.ore, clay + c.clay, obsidian + c.obsidian, geode + c.geode)
        }

        operator fun minus(c: Counts): Counts {
            return Counts(ore - c.ore, clay - c.clay, obsidian - c.obsidian, geode - c.geode)
        }

        fun isValid(): Boolean {
            return ore >= 0 && clay >= 0 && obsidian >= 0 && geode >= 0
        }
    }

    data class State(val restMinutes: Int, val stocks: Counts, val robots: Counts)
    data class ComparableState(val score: Int, val state: State) : Comparable<ComparableState> {
        override fun compareTo(other: ComparableState): Int {
            return score.compareTo(other.score)
        }

    }

    private fun bestGeodeCount(bp: BluePrint): Int {
        fun calcScore(s: State): Int {
            if (s.restMinutes == 0) {
                return s.stocks.geode * 10000000
            }
            var score = s.robots.geode * s.restMinutes * 10000000
            score += s.robots.obsidian * 10000
            score += s.robots.clay * 100
            score += s.robots.ore * 10
            score += s.stocks.geode * 50000
            score += s.stocks.obsidian * 5000 / s.restMinutes
            score += s.stocks.clay * 50 / s.restMinutes
            score += s.stocks.ore * 5 / s.restMinutes
            return score
        }

        var maxGeode = 0
        val width = 2
        val maxMinutes = 32
        val pqs = List(maxMinutes + 2) { PriorityQueue<ComparableState>() }
        val visited = List(maxMinutes + 2) { mutableSetOf<State>() }
        fun stateInserter(i: Int, s: State) {
            val score = calcScore(s)
            pqs[i].add(ComparableState(-score, s))
        }
        stateInserter(1, State(maxMinutes, Counts(0, 0, 0, 0), Counts(1, 0, 0, 0)))

        val loops = 100

        for (i in 0 until loops) {
            for (minutes in 1..maxMinutes) {
                println("$minutes minutes")
                val pq = pqs[minutes]

                fun nextInserter(s: State) {
                    assert(s.stocks.isValid()) { "You're trying to insert an invalid state: $s" }
                    if (!visited[minutes].contains(s)) {
                        println("insert $s")
                        stateInserter(minutes + 1, s)
                        visited[minutes].add(s)
                    }
                }

                repeat(min(width, pq.size)) {
                    val (_, state) = pq.poll()
                    val (restMinutes, stocks, robots) = state
                    println("simulating $state...")
                    val newStocks = stocks + robots
                    println("got some stocks -> $newStocks")
                    maxGeode = max(maxGeode, newStocks.geode)

                    println("do nothing")
                    nextInserter(State(restMinutes - 1, newStocks, robots))
                    // geode
                    val geodeStocks = stocks - bp.geodeRobotCost
                    if (geodeStocks.isValid()) {
                        println("create geode robot")
                        nextInserter(State(restMinutes - 1, geodeStocks + robots, robots + Counts(0, 0, 0, 1)))
                    }
                    if (robots.obsidian <= bp.maxObsidianCost) {
                        // obsidian
                        val obsidianStocks = stocks - bp.obsidianRobotCost
                        if (obsidianStocks.isValid()) {
                            println("create obsidian robot")
                            nextInserter(State(restMinutes - 1, obsidianStocks + robots, robots + Counts(0, 0, 1, 0)))
                        }
                    }

                    if (robots.clay <= bp.maxClayCost) {
                        val clayStocks = stocks - bp.clayRobotCost
                        if (clayStocks.isValid()) {
                            println("create clay robot")
                            nextInserter(State(restMinutes - 1, clayStocks + robots, robots + Counts(0, 1, 0, 0)))
                        }
                    }

                    if (robots.ore <= bp.maxOreCost) {
                        // ore
                        val oreStocks = stocks - bp.oreRobotCost
                        if (oreStocks.isValid()) {
                            println("create ore robot")
                            nextInserter(State(restMinutes - 1, oreStocks + robots, robots + Counts(1, 0, 0, 0)))
                        }
                    }
                }
            }
        }
        return maxGeode
    }
}
