class Day20(private val encrypted: List<Long>) {
    class BidirectionalNode(val id: Int, val value: Long, var next: BidirectionalNode?, var prev: BidirectionalNode?) {
        fun toList(): List<BidirectionalNode> {
            val l = mutableListOf(this)
            var tmp = this.next!!
            while (tmp.id != id) {
                l.add(tmp)
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
            fun fromList(l: List<Long>): BidirectionalNode {
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

        private fun parse(lines: List<String>): List<Long> {
            return lines.map(String::toLong)
        }
    }

    private fun solve(): Long {
        // [-5644, 7], [-3945, 7], [7762, 6], [-2705, 6]
        val head = BidirectionalNode.fromList(encrypted.map { it * 811589153L })
        val originalPosition = head.toList()

        val mod = encrypted.size - 1

        val zeroNode = originalPosition.find { it.value == 0L }

        println("start!")

        for (round in 0 until 10) {
            for (cur in originalPosition) {
                // println("Trying to move $cur")
                var delta = cur.value
                delta %= mod
                if (delta < 0) {
                    delta += mod
                }
                assert(delta >= 0L)
                var target = cur
                if (delta == 0L) {
                    continue
                }
                while (delta > 0) {
                    target = target.next!!
                    delta -= 1
                }

                // println("The target is ${target}. Insert the element next to the target.")
                // cur prev - cur - cur next => cur prev - cur next
                val curNext = cur.next!!
                val curPrev = cur.prev!!
                curPrev.linkNext(curNext)
                // target - targetNext => target - cur - targetNext
                val targetNext = target.next!!
                target.linkNext(cur)
                cur.linkNext(targetNext)
            }
            println("Round ${round + 1} finished")
        }


        var checkCur = zeroNode!!
        var ans = 0L
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
