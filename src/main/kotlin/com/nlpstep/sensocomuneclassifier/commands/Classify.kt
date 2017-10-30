/* Copyright 2017-present The NLPStep Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.nlpstep.sensocomuneclassifier.commands

import com.kotlinnlp.hanclassifier.HANClassifier
import com.kotlinnlp.neuraltokenizer.NeuralTokenizer
import com.kotlinnlp.neuraltokenizer.Sentence
import com.kotlinnlp.simplednn.simplemath.ndarray.dense.DenseNDArray

/**
 * The command executed on the route '/classify'.
 *
 * @property tokenizer a [NeuralTokenizer]
 * @property classifier a [HANClassifier]
 */
class Classify(
  private val tokenizer: NeuralTokenizer,
  private val classifier: HANClassifier,
  private val classes: List<String>
) {

  /**
   * Check classes and classifier compatibility.
   */
  init {
    require(this.classes.size == this.classifier.model.han.outputSize)
  }

  /**
   * Classify the given [gloss].
   *
   * @param gloss the gloss to classify
   *
   * @return the SensoComune ontology category of the given [gloss]
   */
  operator fun invoke(gloss: String): String {

    val sentences: List<Sentence> = this.tokenizer.tokenize(gloss)
    val output: DenseNDArray = this.classifier.classify(sentences.toNestedStrings())

    return this.classes[output.argMaxIndex()]
  }

  /**
   * Convert a [List] of [Sentence]s to nested [String]s (forms).
   *
   * @return a list of list of Strings
   */
  private fun List<Sentence>.toNestedStrings(): List<List<String>> {

    return this.map { it.tokens.map { it.form } }
  }
}
