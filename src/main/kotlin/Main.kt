fun intersect(s1: String, s2: String): Char {
    val cs1 = s1.toCharArray().sorted()
    val cs2 = s2.toCharArray().sorted()
    var i1 = 0
    var i2 = 0
    while (i1 < cs1.size && i2 < cs2.size) {
        val c1 = cs1[i1]
        val c2 = cs2[i2]
        if (c1 == c2) return c1

        if (c1 < c2) {
            i1 += 1
        } else {
            i2 += 1
        }
    }
    1 / 0 // unreachable!!!
    return '-'
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val rucksacks = mutableListOf<Pair<String, String>>()
    while (true) {
        val line = readLine();
        if (line == null) {
            break;
        }

        val len = line.length
        val s1 = line.substring(0, len/2)
        val s2 = line.substring(len/2, len)
        rucksacks.add(Pair(s1, s2))
    }
    val points = rucksacks.map { (s1, s2) ->
        val c = intersect(s1, s2)
        if (c >= 'a') {
            c.code - 'a'.code + 1
        } else {
            c.code - 'A'.code + 27
        }
    }

    println(points.sum())
}
