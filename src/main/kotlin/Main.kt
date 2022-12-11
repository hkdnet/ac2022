fun solve(monkeys: List<Monkey>): Long {
    val rounds = 10000
    repeat(rounds) {
        for ((idx, monkey) in monkeys.withIndex()) {
            println("Monkey $idx")
            for ((dest, items) in monkey.inspectAll()) {
                for (item in items) {
                    monkeys[dest].add(item)
                }
            }
        }
        println("round ${it + 1}: ${monkeys.map { it.getInspectCount() }}")
    }

    val arr = monkeys.map { it.getInspectCount() }.sortedDescending().take(2)

    println("top2: $arr")

    return arr[0] * arr[1]
}



class Monkey(
    private val operator: String,
    private val rhs: Rhs,
    val test: (ULong) -> Boolean,
    private val destinations: Pair<Int, Int>
) {
    private val items = mutableListOf<ULong>()
    private var inspectCount = 0L
    private var operation: (ULong) -> ULong = { _ -> 0UL }

    fun add(item: ULong) {
        items.add(item)
    }

    fun getInspectCount(): Long {
        return inspectCount
    }

    fun setOperation(mod: ULong) {
        this.operation = when (operator) {
            "+" -> { old ->
                val r = rhs.value(old)
                (old + r) % mod
            }

            "-" -> { old ->
                val r = rhs.value(old)
                (old - r) % mod
            }

            "*" -> { old ->
                val r = rhs.value(old)
                (old * r) % mod
            }

            "/" -> {
                val d = modinv(rhs.value(1UL), mod);
                { old ->
                    (old * d) % mod
                }
            }

            else -> {
                throw Exception("unreachable")
            }
        }
    }


    fun inspectAll(): Map<Int, List<ULong>> {
        val m = mutableMapOf<Int, MutableList<ULong>>()
        for (item in items) {
            val newWorry = operation(item)
            val dest = if (test(newWorry)) {
                destinations.first
            } else {
                destinations.second
            }
            println("$item => $newWorry, passing it to $dest")
            m.getOrPut(dest) { mutableListOf() }.add(newWorry)
        }

        inspectCount += items.size
        items.clear()

        return m
    }
}

val operationRegex = """^  Operation: new = old (.) (\d+|old)$""".toRegex()

sealed interface Rhs {
    fun value(old: ULong): ULong
}

data class IntegerLiteral(val v: ULong) : Rhs {
    override fun value(old: ULong): ULong {
        return v
    }
}

object Old : Rhs {
    override fun value(old: ULong): ULong {
        return old
    }
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {

    assert(modinv(3UL, 11UL) == 4UL)
    assert(modinv(10UL, 17UL) == 12UL)
    fun parse(): List<Monkey> {
        val lines = readLines()
        var idx = 0
        val monkeys = mutableListOf<Monkey>()

        var mod = 1UL

        while (idx < lines.size) {
            if (!lines[idx].startsWith("Monkey")) {
                idx += 1
                continue
            }

            idx += 1 // go to starting items
            val startingItems = lines[idx].split(": ")[1].split(", ").map(String::toULong)
            idx += 1 // operation
            val m = operationRegex.matchEntire(lines[idx])!!
            val op = m.groups[1]!!.value
            val rhs =
                if (m.groups[2]!!.value == "old") {
                    Old
                } else {
                    IntegerLiteral(
                        m.groups[2]!!.value.toULong()
                    )
                }

            idx += 1 // to Test
            val divider = lines[idx].split(" ").last().toULong()
            mod *= divider
            idx += 1 // to true
            val trueDest = lines[idx].split(" ").last().toInt()
            idx += 1 // to false
            val falseDest = lines[idx].split(" ").last().toInt()
            idx += 2 // to Monkey

            val monkey = Monkey(op, rhs, { v -> (v % divider) == 0UL }, Pair(trueDest, falseDest))
            startingItems.forEach { monkey.add(it) }
            monkeys.add(monkey)
            println("Monkey")
            println("startingItems = $startingItems")
            println("op = $op, rhs = $rhs")
            println("divider = $divider")
            println("dest = $trueDest, $falseDest")
        }

        monkeys.forEach { it.setOperation(mod) }

        return monkeys
    }
    println(solve(parse()))
}
