import kotlin.math.sign

class Day21(private val m: Map<String, Expr>) {
    sealed interface Expr {
        fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr
        fun evaluate(m: MutableMap<String, Expr>): Pair<Long, Boolean>
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
//
//        operator fun unaryMinus(): ArithmeticExpr {
//            return BinaryOperator(Constant(-1L), "*", this)
//        }
    }

    data class VariableHuman(val coefficient: Long) : ArithmeticExpr {
        override fun evaluate(m: MutableMap<String, Expr>): Pair<Long, Boolean> {
            return m["humn"]!!.evaluate(m)
        }
//
//        override fun plus(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is VariableHuman) {
//                return VariableHuman(coefficient + o.coefficient)
//            }
//            return super.plus(o)
//        }
//
//        override fun minus(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is VariableHuman) {
//                return VariableHuman(coefficient - o.coefficient)
//            }
//            return super.minus(o)
//        }
//
//        override fun times(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is Constant) {
//                return VariableHuman(coefficient * o.v)
//            }
//            return super.times(o)
//        }
//
//        override fun div(o: ArithmeticExpr): ArithmeticExpr {
//            return super.times(o)
//        }

//        override fun unaryMinus(): ArithmeticExpr {
//            return VariableHuman(-coefficient)
//        }

        override fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr {
            return this
        }

        override fun toString(): String {
            return "${coefficient}h"
        }
    }

    data class Constant(val v: Long) : ArithmeticExpr {
        override fun evaluate(m: MutableMap<String, Expr>): Pair<Long, Boolean> {
            return Pair(v, true)
        }
//
//        override fun plus(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is Constant) {
//                return Constant(v + o.v)
//            }
//            return o + this
//        }
//
//        override fun minus(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is Constant) {
//                return Constant(v - o.v)
//            }
//            return super.minus(o)
//        }
//
//        override fun times(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is Constant) {
//                return Constant(v * o.v)
//            }
//            return super.times(o)
//        }
//
//        override fun div(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is Constant) {
//                return Constant(v / o.v)
//            }
//            return super.div(o)
//        }

        override fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr {
            return this
        }

//        override fun unaryMinus(): ArithmeticExpr {
//            return Constant(-v)
//        }

        override fun toString(): String {
            return v.toString()
        }
    }

    data class Reference(val name: String) : Expr {

        override fun simplify(m: MutableMap<String, Expr>): ArithmeticExpr {
            val v = m[name]!!.simplify(m)
            m[name] = v
            return v
        }

        override fun evaluate(m: MutableMap<String, Expr>): Pair<Long, Boolean> {
            return m[name]!!.evaluate(m)
        }
    }

    data class BinaryOperator(val l: Expr, val op: String, val r: Expr) : ArithmeticExpr {
//        override fun plus(o: ArithmeticExpr): ArithmeticExpr {
//            if (o is Constant) {
//                if (l is Constant) {
//                    return BinaryOperator(l + o, op, r)
//                }
//                if (r is Constant) {
//                    return BinaryOperator(l, op, r + o)
//                }
//            }
//            return super.plus(o)
//        }
//
//        override fun times(o: ArithmeticExpr): ArithmeticExpr {
//            if (op == "+" || op == "-") {
//                return BinaryOperator(
//                    BinaryOperator(l, "*", o),
//                    op,
//                    BinaryOperator(r, "*", o)
//                )
//            }
//            return super.times(o)
//        }

//        override fun unaryMinus(): ArithmeticExpr {
//            return BinaryOperator(Constant(-1L), "*", this)
//        }

        override fun evaluate(m: MutableMap<String, Expr>): Pair<Long, Boolean> {
            val (lValue, lOk) = l.evaluate(m)
            val (rValue, rOk) = r.evaluate(m)

            return when (op) {
                "+" -> {
                    Pair(Math.addExact(lValue, rValue), lOk && rOk)
                }

                "-" -> {
                    Pair(Math.subtractExact(lValue, rValue), lOk && rOk)
                }

                "*" -> {
                    Pair(Math.multiplyExact(lValue, rValue), lOk && rOk)
                }

                "/" -> {
                    val v = lValue / rValue

                    if (lValue % rValue == 0L) {
                        Pair(v, lOk && rOk)
                    } else {
                        Pair(v, false)
                    }
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

        override fun toString(): String {
            val base = "$l $op $r"
            return when (op) {
                "+", "-" -> "($base)"
                else -> base
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
        val lhs = root.l.simplify(m.toMutableMap())
        val rhs = root.r.simplify(m.toMutableMap())
        val mutable = m.toMutableMap()
        println(lhs)
        println(rhs)

        var l = 0L
        var r = 1_000_000_000_000_000L

        fun eval(l: Long): Long {
            println("humn = $l")
            mutable["humn"] = Constant(l)
            val (lv, _) = lhs.evaluate(mutable)
            val (rv, _) = rhs.evaluate(mutable)
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
        val isUpper = eval(0) < eval(100)
        var nearAns = 0L
        while ((r - l) > 1) {
            val n = (r + l) / 2
            println("$l - $r -> $n")

            var evaluated = eval(n)
            if (evaluated == 0L) {
                nearAns = n
                break
            }
            if (evaluated < 0 && isUpper || evaluated > 0 && !isUpper) {
                l = n
            } else {
                r = n
            }
        }
        println("Found $nearAns")
        var delta = 0
        while (true) {

            var np = nearAns + delta
            var nm = nearAns - delta
            println("trying delta = $delta")
            println("np = $np")
            mutable["humn"] = Constant(np)
            val (nplValue, nplOk) = lhs.evaluate(mutable)
            val (nprValue, nprOk) = lhs.evaluate(mutable)
            if (nplOk && nprOk && nplValue == nprValue) {
                return np
            }
            println("nm = $nm")
            mutable["humn"] = Constant(nm)
            val (nmlValue, nmlOk) = lhs.evaluate(mutable)
            val (nmrValue, nmrOk) = lhs.evaluate(mutable)
            if (nmlOk && nmrOk && nmlValue == nmrValue) {
                return nm
            }

            delta += 1
        }
    }
}
