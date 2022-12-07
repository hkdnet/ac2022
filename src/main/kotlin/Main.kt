typealias Directory = Pair<Int, List<String>>

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val lines = mutableListOf<String>()
    while (true) {
        val line = readlnOrNull() ?: break
        lines.add(line)
    }

    val commands = mutableListOf<MutableList<String>>()
    for (l in lines) {
        if (l.startsWith("$")) {
            val tmp = mutableListOf<String>()
            tmp.add(l)
            commands.add(tmp)
        } else {
            commands.last().add(l)
        }
    }
    var cur = "/"
    val ds: MutableMap<String, Directory> = mutableMapOf()
    for (c in commands) {
        val cmd = c.first()

        if (cmd.startsWith("$ cd ")) {
            val dst = cmd.drop(5)
            when (dst) {
                "/" -> {
                    cur = "/"
                }

                ".." -> {
                    cur = cur.split("/").dropLast(1).joinToString("/")
                }

                else -> {
                    if (cur == "/") {
                        cur = "/$dst"
                    } else {
                        cur += "/$dst"
                    }
                }
            }
            println("cd-ed to $cur by $cmd")
        } else if (cmd == "$ ls") {
            val refs = mutableListOf<String>()
            var size = 0
            for (entry in c.drop(1)) {
                if (entry.startsWith("dir ")) {
                    val ref = entry.drop(4)
                    if (cur == "/") {
                        refs.add("/$ref")
                    } else {
                        refs.add("$cur/$ref")
                    }
                } else {
                    val fileSize = entry.split(" ")[0].toInt()
                    size += fileSize
                }

            }
            ds[cur] = Pair(size, refs)
        } else {
            assert(false)
        }
    }
    val memo = mutableMapOf<String, Int>()

    fun calc(s: String): Int {
        return if (memo.containsKey(s)) {
            memo[s]!!
        } else {
            val (dSize, refs) = ds[s]!!
            val size = refs.sumOf { calc(it) } + dSize
            memo[s] = size
            size
        }
    }
    for (key in ds.keys) {
        calc(key)
    }

    val ans = memo.values.filter { it <= 100_000 }.sum()

    println(ans)
}
