package com.emmav.adventofcode

import java.io.File

private val reactions = File("input/day14-2.txt").readLines()
    .map {
        val inputsAndOutputs = it.split("=>")
        val inputs = inputsAndOutputs[0].split(",")
        val chemicals = inputs.map { input -> input.toChemical() }
        Reaction(chemicals, output = inputsAndOutputs[1].toChemical())
    }

private fun String.toChemical(): Chemical {
    val amountAndChemical = trim().split(" ")
    return Chemical(amountAndChemical[1].trim(), amountAndChemical[0].trim().toInt())
}

fun main() {
    reactions.forEach { println(it) }
    println()

    val requiredOre = requiredOre()
    println("Required ore: $requiredOre")
}

fun requiredOre(): Int {
    val reactionForFuel = reactions.find { it.output.name == "FUEL" }!!

    // Find all the base chemicals (chemicals produced directly from ore) we need to create fuel
    val requiredBaseChemicals = findBaseChemicalsRequired(reactionForFuel, amount = 1)

    // Figure out how much we need of each
    val baseChemicalsTotals = requiredBaseChemicals.groupingBy { it }
        .fold(0) { accumulator, _ -> accumulator + 1 }

    println("Base chemicals needed")
    baseChemicalsTotals.forEach { println(it) }

    var requiredOre = 0
    for ((chemical, amount) in baseChemicalsTotals) {
        val reaction = chemical.reactionToCreate()
        if (reaction.chemicals.size != 1) {
            throw IllegalArgumentException("Invalid base reaction $reaction")
        }
        val numberOfReactionsNeeded = if ((amount % reaction.output.amount) != 0) {
            (amount + reaction.output.amount) / reaction.output.amount
        } else {
            amount / reaction.output.amount
        }
        val ore = reaction.chemicals.first()
        requiredOre += ore.amount * numberOfReactionsNeeded
    }
    return requiredOre
}

fun findBaseChemicalsRequired(reaction: Reaction, amount: Int): List<Chemical> {
    println("Reaction creating ${reaction.output.name}, we need $amount")
    val baseChemicals = mutableListOf<Chemical>()
    for (chemical in reaction.chemicals) {
        if (chemical.name == "ORE") {
            for (i in 0 until amount) {
                baseChemicals.add(reaction.output)
            }
        } else {
            val amountNeeded = if ((chemical.amount * amount % reaction.output.amount) != 0) {
                (chemical.amount * amount + reaction.output.amount) / reaction.output.amount
            } else {
                chemical.amount * amount / reaction.output.amount
            }
            baseChemicals.addAll(
                findBaseChemicalsRequired(chemical.reactionToCreate(), amount = amountNeeded)
            )
        }
    }
    return baseChemicals
}

fun Chemical.reactionToCreate(): Reaction {
    return reactions.first { this.name == it.output.name }
}

data class Reaction(val chemicals: List<Chemical>, val output: Chemical)
data class Chemical(val name: String, val amount: Int)