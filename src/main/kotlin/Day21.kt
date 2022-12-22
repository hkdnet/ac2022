import kotlin.math.sign

class Day21(private val m: Map<String, Expr>) {
    sealed interface Expr {
        fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr
        fun evaluate(m: MutableMap<String, Expr>): Long
    }

    sealed interface ArithmeticExpr : Expr {
        operator fun plus(o: ArithmeticExpr): ArithmeticExpr {
            return BinaryOperator(this, "+", o)
        }

        operator fun minus(o: ArithmeticExpr): ArithmeticExpr {
            return BinaryOperator(this, "-", o)
        }

        operator fun times(o: ArithmeticExpr): ArithmeticExpr {
            return BinaryOperator(this, "*", o)
        }

        operator fun div(o: ArithmeticExpr): ArithmeticExpr {
            return BinaryOperator(this, "/", o)
        }

        operator fun unaryMinus(): ArithmeticExpr
    }

    data class VariableHuman(val coefficient: Long) : ArithmeticExpr {
        override fun evaluate(m: MutableMap<String, Expr>): Long {
            return m["humn"]!!.evaluate(m)
        }

        override fun plus(o: ArithmeticExpr): ArithmeticExpr {
            if (o is VariableHuman) {
                return VariableHuman(coefficient + o.coefficient)
            }
            return super.plus(o)
        }

        override fun minus(o: ArithmeticExpr): ArithmeticExpr {
            if (o is VariableHuman) {
                return VariableHuman(coefficient - o.coefficient)
            }
            return super.minus(o)
        }

        override fun times(o: ArithmeticExpr): ArithmeticExpr {
            if (o is Constant) {
                return VariableHuman(coefficient * o.v)
            }
            return super.times(o)
        }

        override fun div(o: ArithmeticExpr): ArithmeticExpr {
            return super.times(o)
        }

        override fun unaryMinus(): ArithmeticExpr {
            return VariableHuman(-coefficient)
        }

        override fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr {
            return this
        }

    }

    data class Constant(val v: Long) : ArithmeticExpr {
        override fun evaluate(m: MutableMap<String, Expr>): Long {
            return v
        }

        override fun plus(o: ArithmeticExpr): ArithmeticExpr {
            if (o is Constant) {
                return Constant(v + o.v)
            }
            return o + this
        }

        override fun minus(o: ArithmeticExpr): ArithmeticExpr {
            if (o is Constant) {
                return Constant(v - o.v)
            }
            return super.minus(o)
        }

        override fun times(o: ArithmeticExpr): ArithmeticExpr {
            if (o is Constant) {
                return Constant(v * o.v)
            }
            return super.times(o)
        }

        override fun div(o: ArithmeticExpr): ArithmeticExpr {
            if (o is Constant) {
                return Constant(v / o.v)
            }
            return super.div(o)
        }

        override fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr {
            return this
        }

        override fun unaryMinus(): ArithmeticExpr {
            return Constant(-v)
        }
    }

    data class Reference(val name: String) : Expr {

        override fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr {
            val v = m[name]!!.simplify(m)
            m[name] = v
            return v
        }

        override fun evaluate(m: MutableMap<String, Expr>): Long {
            return m[name]!!.evaluate(m)
        }
    }

    data class BinaryOperator(val l: Expr, val op: String, val r: Expr) : ArithmeticExpr {
        override fun unaryMinus(): ArithmeticExpr {
            return BinaryOperator(Constant(-1L), "*", this)
        }

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

        override fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr {
            val lValue = l.simplify(m)
            val rValue = r.simplify(m)

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
                val expr = if (name == "humn") {
                    VariableHuman(1)
                } else if (constantOp == null) {
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
        val root = m["root"] as BinaryOperator

        val lhs = root.l
        val rhs = root.r
        var l = 0L
        var r = 1_000_000_000_000_000L

        fun eval(l: Long): Long {
            val mutable = m.toMutableMap()
            println("humn = $l")
            mutable["humn"] = Constant(l)
            val lv = lhs.evaluate(mutable)
            val rv = rhs.evaluate(mutable)
            println("$lv vs $rv")
            return lv - rv
        }
        run {
            // checking
            val initialValueL = eval(l)
            val initialValueR = eval(r)
            println("$initialValueL -> ${initialValueL.sign}")
            println("$initialValueR -> ${initialValueR.sign}")
            assert(initialValueL.sign * initialValueR.sign < 0) {
                "the answer is not in the range, reconsider l and r"
            }
        }
        val isUpper = eval(0) < eval(1)
        while ((r - l) > 1) {
            val n = (r + l) / 2
            println("$l - $r -> $n")
            val evaluated = eval(n)
            if (evaluated == 0L) {
                return n
            }
            if (evaluated < 0 && isUpper || evaluated > 0 && !isUpper) {
                l = n
            } else {
                r = n
            }
        }
        TODO("unreachable!")
    }
}
