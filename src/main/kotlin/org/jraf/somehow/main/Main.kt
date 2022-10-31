/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2022-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.somehow.main

import kotlinx.browser.document
import org.jraf.klibregexdsl.AnyCharacter
import org.jraf.klibregexdsl.BeginningOfLine
import org.jraf.klibregexdsl.Characters
import org.jraf.klibregexdsl.Either
import org.jraf.klibregexdsl.Group
import org.jraf.klibregexdsl.QuantifierType
import org.jraf.klibregexdsl.Sequence
import org.jraf.klibregexdsl.WhitespaceCharacter
import org.jraf.klibregexdsl.onceOrNotAtAll
import org.jraf.klibregexdsl.oneOrMoreTimes
import org.jraf.somehow.util.logi
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.w3c.dom.get

fun main() {
    logi("Somehowization of the page...")
    val endOfSentence =
        Either(
            BeginningOfLine,
            Sequence(
                Characters("."),
                Group(
                    Sequence(
                        Characters("<sup "),
                        AnyCharacter.oneOrMoreTimes(QuantifierType.RELUCTANT),
                        Characters("</sup>"),
                    )
                )
                    .onceOrNotAtAll()
            )
        )
    val spaces = WhitespaceCharacter.oneOrMoreTimes()
    val firstWord = AnyCharacter.oneOrMoreTimes(QuantifierType.RELUCTANT)
    val regex = Sequence(
        Group(Sequence(endOfSentence, spaces)),
        Group(firstWord)
    ).toRegex()

    val nodes: NodeList = document.querySelectorAll("p")
    for (i in 0 until nodes.length) {
        val node: Element = nodes[i] as Element
        node.innerHTML = node.innerHTML.replace(regex) {
            val endOfSentenceMatch = it.groupValues[1]
            val beginOfNextSentence = it.groupValues[3]
            "$endOfSentenceMatch Somehow, ${beginOfNextSentence.replaceFirstChar { c -> c.lowercase() }}"
        }
    }
    logi("Done.")
}
