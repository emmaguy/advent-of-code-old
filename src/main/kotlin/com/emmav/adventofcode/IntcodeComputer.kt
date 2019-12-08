package com.emmav.adventofcode

class IntcodeComputer {

    fun compute(memory: Array<Int>, input: List<Int>): List<Int> {
        val output = mutableListOf<Int>()
        var index = 0
        var inputIndex = 0
        while (true) {
            val instruction = memory[index]
            when (instruction % 100) {
                1 -> {
                    memory[memory[index + 3]] = param1(instruction, memory, index) + param2(instruction, memory, index)
                    index += 4
                }
                2 -> {
                    memory[memory[index + 3]] = param1(instruction, memory, index) * param2(instruction, memory, index)
                    index += 4
                }
                3 -> {
                    val address = memory[index + 1]
                    memory[address] = input[inputIndex++]
                    index += 2
                }
                4 -> {
                    output.add(param1(instruction, memory, index))
                    index += 2
                }
                5 -> {
                    if (param1(instruction, memory, index) != 0) {
                        index = param2(instruction, memory, index)
                    } else {
                        index += 3
                    }
                }
                6 -> {
                    if (param1(instruction, memory, index) == 0) {
                        index = param2(instruction, memory, index)
                    } else {
                        index += 3
                    }
                }
                7 -> {
                    val value = if (param1(instruction, memory, index) < param2(instruction, memory, index)) {
                        1
                    } else {
                        0
                    }
                    memory[memory[index + 3]] = value
                    index += 4
                }
                8 -> {
                    val value = if (param1(instruction, memory, index) == param2(instruction, memory, index)) {
                        1
                    } else {
                        0
                    }
                    memory[memory[index + 3]] = value
                    index += 4
                }
                99 -> return output
                else -> throw IllegalArgumentException("Unexpected instruction: $instruction")
            }
        }
    }

    private fun param1(instruction: Int, memory: Array<Int>, index: Int): Int {
        return if ((instruction / 100) % 10 == 1) memory[index + 1] else memory[memory[index + 1]]
    }

    private fun param2(instruction: Int, memory: Array<Int>, index: Int): Int {
        return if ((instruction / 1000) % 10 == 1) memory[index + 2] else memory[memory[index + 2]]
    }
}