/* Copyright 2017-present The NLPStep Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.nlpstep.sensocomuneclassifier.commands

import com.beust.klaxon.JsonArray
import com.beust.klaxon.json
import com.kotlinnlp.hanclassifier.HANClassifier
import com.kotlinnlp.neuraltokenizer.NeuralTokenizer
import com.kotlinnlp.neuraltokenizer.Sentence
import com.kotlinnlp.simplednn.deeplearning.attentionnetwork.han.HierarchyGroup
import com.kotlinnlp.simplednn.deeplearning.attentionnetwork.han.HierarchySequence
import com.kotlinnlp.simplednn.simplemath.ndarray.dense.DenseNDArray
import toNestedStrings

/**
 * The command executed on the route '/classify-with-score'.
 *
 * @property tokenizer a [NeuralTokenizer]
 * @property classifier a [HANClassifier]
 */
class ClassifyWithScore(
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
   * Classify the given [gloss], returning the outcomes distribution and the importance scores of the input tokens.
   * Spaces are not considered.
   *
   * The returned JSON object format:
   * {
   *   "class": <STRING>,
   *   "distribution": [
   *     {"class": <STRING>, "score": <DOUBLE>},
   *     ...
   *   ],
   *   "importance_scores": [  // list of sentences
   *     [
   *       {"token": <STRING>, "score": <DOUBLE>},
   *       ...
   *     ],
   *     ...
   *   ]
   * }
   *
   * @param gloss the gloss to classify
   *
   * @return a JSON string containing the SensoComune ontology category of the given [gloss], with the probability
   *         distribution of all categories and the importance scores of the input tokens
   */
  operator fun invoke(gloss: String): String {

    val sentences: List<Sentence> = this.tokenizer.tokenize(gloss)
    val nestedStrings: List<List<String>> = sentences.toNestedStrings()

    val output: DenseNDArray = this.classifier.classify(nestedStrings)
    val importanceScores = this.classifier.encoder.getInputImportanceScores() as HierarchyGroup

    return json {
      obj(
        "class" to this@ClassifyWithScore.classes[output.argMaxIndex()],
        "distribution" to array(List(
          size = output.length,
          init = { i -> obj("class" to this@ClassifyWithScore.classes[i], "score" to output[i]) }
        ).sortedByDescending { it["score"] as Double }),
        "importance_scores" to importanceScores.toJsonScores(nestedStrings)
      )
    }.toJsonString()
  }

  /**
   * Convert a [HierarchyGroup] of importance scores into json lists of json objects (token-score).
   *
   * @param sentences
   *
   * @return a json array of json arrays of objects token-score
   */
  private fun HierarchyGroup.toJsonScores(sentences: List<List<String>>): JsonArray<*> = json {

    array(this@toJsonScores.mapIndexed { i, item ->

      val sentence: List<String> = sentences[i]
      val tokensScores: DenseNDArray = (item as HierarchySequence<*>)[0] as DenseNDArray

      array(sentence.mapIndexed { j, token ->
        obj("token" to token, "score" to tokensScores[j])
      })
    })
  }
}
