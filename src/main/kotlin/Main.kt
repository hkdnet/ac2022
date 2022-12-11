fun readLines(): List<String> {
    val l = mutableListOf<String>()
    while (true) {
        val s = readlnOrNull() ?: break
        l.add(s)
    }
    return l
}

fun solve(monkeys: List<Monkey>): Int {
    val rounds = 20
    repeat(rounds) {
        println("${it + 1} round")

        for (monkey in monkeys) {
            for ((dest, items) in monkey.inspectAll()) {
                for (item in items) {
                    monkeys[dest].add(item)
                }
            }
        }
    }

    val arr = monkeys.map { it.getInspectCount() }.sortedDescending().take(2)

    println("top2: $arr")

    return arr[0] * arr[1]
}

class Monkey(val operation: (Int) -> Int, val test: (Int) -> Boolean, val destinations: Pair<Int, Int>) {
    private val items = mutableListOf<Int>()
    private var inspectCount = 0

    fun add(item: Int) {
        items.add(item)
    }

    fun getInspectCount(): Int {
        return inspectCount
    }

    fun inspectAll(): Map<Int, List<Int>> {
        val m = mutableMapOf<Int, MutableList<Int>>()
        for (item in items) {
            inspectCount += 1
            val newWorry = operation(item)
            val bored = newWorry / 3
            val dest = if (test(bored)) {
                destinations.first
            } else {
                destinations.second
            }
            m.getOrPut(dest) { mutableListOf<Int>() }.add(bored)
        }
        items.clear()
        return m
    }
}

val operationRegex = """^  Operation: new = old (.) (\d+|old)$""".toRegex()

sealed interface Rhs {
    fun value(old: Int): Int
}

data class IntegerLiteral(val v: Int) : Rhs {
    override fun value(old: Int): Int {
        return v
    }
}

object Old : Rhs {
    override fun value(old: Int): Int {
        return old
    }
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    fun parse(): List<Monkey> {
        val lines = readLines()
        var idx = 0
        val monkeys = mutableListOf<Monkey>()

        while (idx < lines.size) {
            if (!lines[idx].startsWith("Monkey")) {
                idx += 1
                continue
            }

            idx += 1 // go to starting items
            val startingItems = lines[idx].split(": ")[1].split(", ").map(String::toInt)
            idx += 1 // operation
            println("${lines[idx]}")
            val m = operationRegex.matchEntire(lines[idx])!!
            val op = m.groups[1]!!.value
            val rhs =
                if (m.groups[2]!!.value == "old") {
                    Old
                } else {
                    IntegerLiteral(
                        m.groups[2]!!.value.toInt()
                    )
                }
            val operation = { old: Int ->
                when (op) {
                    "+" -> old + rhs.value(old)
                    "-" -> old - rhs.value(old)
                    "*" -> old * rhs.value(old)
                    "/" -> old / rhs.value(old)
                    else -> 1 / 0
                }
            }
            idx += 1 // to Test
            val divider = lines[idx].split(" ").last().toInt()
            idx += 1 // to true
            val trueDest = lines[idx].split(" ").last().toInt()
            idx += 1 // to false
            val falseDest = lines[idx].split(" ").last().toInt()
            idx += 2 // to Monkey

            val monkey = Monkey(operation, { v -> (v % divider) == 0 }, Pair(trueDest, falseDest))
            startingItems.forEach { monkey.add(it) }
            monkeys.add(monkey)
        }


        return monkeys
    }
    println(solve(parse()))
}
