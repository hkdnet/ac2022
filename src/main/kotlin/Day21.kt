class Day21(private val m: Map<String, Expr>) {
    sealed interface Expr {
        fun evaluate(m: MutableMap<String, Expr>): Long
    }

    data class Constant(val v: Long) : Expr {
        override fun evaluate(m: MutableMap<String, Expr>): Long {
            return v
        }
    }

    data class Reference(val name: String) : Expr {
        override fun evaluate(m: MutableMap<String, Expr>): Long {
            val v = m[name]!!.evaluate(m)
            m[name] = Constant(v)
            return v
        }
    }

    data class BinaryOperator(val l: Expr, val op: String, val r: Expr) : Expr {
        override fun evaluate(m: MutableMap<String, Expr>): Long {
            val lValue = l.evaluate(m)
            val rValue = r.evaluate(m)
            return when (op) {
                "+" -> {
                    lValue + rValue
                }

                "-" -> {
                    lValue - rValue
                }

                "*" -> {
                    lValue * rValue
                }

                "/" -> {
                    lValue / rValue
                }

                else -> {
                    throw Exception("unknown operator $op")
                }
            }

        }
    }


    companion object {
        fun exec() {
            val m = parse(readLines())
            val s = Day21(m)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): Map<String, Expr> {
            return lines.associate {
                val arr = it.split(": ")
                assert(arr.size == 2)
                val name = arr[0]
                val constantOp = arr[1].toLongOrNull()
                val expr = if (constantOp == null) {
                    val binArr = arr[1].split(" ")
                    assert(binArr.size == 3)
                    BinaryOperator(
                        Reference(binArr[0]),
                        binArr[1],
                        Reference(binArr[2])
                    )
                } else {
                    Constant(constantOp)
                }

                Pair(name, expr)
            }
        }
    }

    private fun solve(): Long {
        return m["root"]!!.evaluate(m.toMutableMap())
    }
}
