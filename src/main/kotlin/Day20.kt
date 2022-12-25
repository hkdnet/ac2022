class Day20(private val encrypted: List<Int>) {
    class BidirectionalNode(val id: Int, val value: Int, var next: BidirectionalNode?, var prev: BidirectionalNode?) {
        fun toList(): List<Int> {
            val l = mutableListOf<Int>(value)
            var tmp = this.next!!
            while (tmp.value != value) {
                l.add(tmp.value)
                tmp = tmp.next!!
            }
            return l
        }

        fun linkNext(o: BidirectionalNode) {
            this.next = o
            o.prev = this
        }

        override fun toString(): String {
            return "Node(id = $id, val = $value, left = (${prev?.id}, ${prev?.value}), right = (${next?.id}, ${next?.value}))"
        }

        companion object {
            fun fromList(l: List<Int>): BidirectionalNode {
                val nodes = l.mapIndexed { idx, v -> BidirectionalNode(idx, v, null, null) }
                for (i in 0 until l.size - 1) {
                    nodes[i].linkNext(nodes[i + 1])
                }
                nodes[l.size - 1].linkNext(nodes[0])

                return nodes[0]
            }
        }
    }


    companion object {
        fun exec() {
            val m = parse(readLines())
            val s = Day20(m)
            val ans = s.solve()
            println(ans)
        }

        private fun parse(lines: List<String>): List<Int> {
            return lines.map(String::toInt)
        }
    }

    private fun solve(): Int {
        // [-5644, 7], [-3945, 7], [7762, 6], [-2705, 6]
        val moved = mutableSetOf<BidirectionalNode>()
        var cur = BidirectionalNode.fromList(encrypted)

        val mod = encrypted.size - 1

        assert(cur.toList() == encrypted)
        var zeroNode: BidirectionalNode? = null
        while (moved.size < encrypted.size) {
            println("Trying to move $cur")
            if (moved.contains(cur)) {
                val curNext = cur.next!!
                println("${cur} was already moved. Trying next: ${curNext}")
                cur = curNext
                continue
            }
            var delta = cur.value
            while (delta < 0) {
                delta += mod
            }
            delta %= mod
            // println("${cur.value} -> index delta is $delta")
//            while (delta < 0) {
//                delta += encrypted.size - 1
//                println("Converted the negative delta to the positive equivalent: $delta")
//            }
            var target = cur
            if (delta == 0) {
                if (cur.value == 0) {
                    assert(zeroNode == null)
                    zeroNode = cur
                }

                moved.add(cur)
                cur = cur.next!!
                continue
            }
            while (delta > 0) {
                target = target.next!!
                delta -= 1
            }

            println("The target is ${target}. Insert the element next to the target.")
            // cur prev - cur - cur next => cur prev - cur next
            val curNext = cur.next!!
            val curPrev = cur.prev!!
            curPrev.linkNext(curNext)
            // target - targetNext => target - cur - targetNext
            val targetNext = target.next!!
            target.linkNext(cur)
            cur.linkNext(targetNext)

            moved.add(cur)

            cur = curNext
        }

        var checkCur = zeroNode!!
        var ans = 0
        for (i in 1..3) {
            repeat(1000) {
                checkCur = checkCur.next!!
            }
            println("found ${checkCur.value}")
            ans += checkCur.value
        }

        return ans
    }
}
