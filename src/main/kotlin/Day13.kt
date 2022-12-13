import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

class Day13(val packetPairs: List<Pair<JsonElement, JsonElement>>) {
    companion object {
        fun exec() {
            val packetPairs = parse(readLines())
            val s = Day13(packetPairs)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): List<Pair<JsonElement, JsonElement>> {
            val l = mutableListOf<Pair<JsonElement, JsonElement>>()
            var idx = 0
            while (idx < lines.size) {
                val left = Json.decodeFromString<JsonElement>(lines[idx])
                idx += 1 // to right
                val right = Json.decodeFromString<JsonElement>(lines[idx])
                idx += 2 // to next left
                l.add(
                    Pair(left, right)
                )
            }
            return l
        }
    }

    private fun solve(): Int {
        return packetPairs.map { (l, r) ->
            if (l is JsonArray && r is JsonArray) {
                isOrdered(l, r)!!
            } else {
                throw Exception("should not reach here")
            }
        }.withIndex().sumOf { (idx, flag) ->
            if (flag) {
                idx + 1
            } else {
                0
            }
        }
    }

    private fun isOrdered(l: JsonArray, r: JsonArray): Boolean? {
        var idx = 0
        while (true) {
            when (Pair(idx in 0 until l.size, idx in 0 until r.size)) {
                Pair(true, true) -> {
                    val lValue = l[idx]
                    val rValue = r[idx]
                    when (lValue) {
                        is JsonPrimitive -> {
                            when (rValue) {
                                is JsonPrimitive -> {
                                    if (lValue.int < rValue.int) {
                                        return true
                                    } else if (lValue.int > rValue.int) {
                                        return false
                                    }
                                    // same value, go to next
                                }
                                is JsonArray -> {
                                    val ret = isOrdered(lValue, rValue)
                                    if (ret != null) {
                                        return ret
                                    }
                                    // if ret == null, go to next
                                }
                                else -> { throw Exception("should not reach here") }
                            }
                        }
                        is JsonArray -> {
                            when (rValue) {
                                is JsonPrimitive -> {
                                    val ret = isOrdered(lValue, rValue)
                                    if (ret != null) {
                                        return ret
                                    }
                                    // if ret == null, go to next
                                }
                                is JsonArray -> {
                                    val ret = isOrdered(lValue, rValue)
                                    if (ret != null) {
                                        return ret
                                    }
                                    // if ret == null, go to next
                                }
                                else -> {
                                    throw Exception("should not reach here")
                                }
                            }
                        }
                        else -> { throw Exception("should not reach here") }
                    }
                }

                Pair(true, false) -> {
                    return false
                }

                Pair(false, true) -> {
                    return true
                }

                Pair(false, false) -> {
                    return null
                }
            }
            idx += 1
        }
        return true
    }

    private fun isOrdered(l: JsonArray, r: JsonPrimitive): Boolean? {
        return isOrdered(l, buildJsonArray { add(r) })
    }

    private fun isOrdered(l: JsonPrimitive, r: JsonArray): Boolean? {
        return isOrdered(buildJsonArray { add(l) }, r)
    }
}
