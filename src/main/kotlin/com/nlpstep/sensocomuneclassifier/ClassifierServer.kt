/* Copyright 2017-present The NLPStep Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.nlpstep.sensocomuneclassifier

import com.kotlinnlp.hanclassifier.HANClassifier
import com.kotlinnlp.hanclassifier.HANClassifierModel
import com.kotlinnlp.neuraltokenizer.NeuralTokenizer
import com.kotlinnlp.neuraltokenizer.NeuralTokenizerModel
import com.nlpstep.sensocomuneclassifier.commands.Classify
import com.nlpstep.sensocomuneclassifier.commands.exceptions.MissingParameters
import spark.Request
import spark.Spark
import java.io.File
import java.io.FileInputStream
import java.util.logging.Logger

/**
 * The SensoComune Classifier Server.
 *
 * @param port the port listened from the server (default = 3000)
 * @param tokenizerModelFilename the filename of the tokenizer model
 * @param hanClassifierModelFilename the filename of the HANClassifier model
 * @param classesFilename the name of the file containing the list of possible classes (one per line)
 */
class ClassifierServer(
  port: Int = 3000,
  tokenizerModelFilename: String,
  hanClassifierModelFilename: String,
  classesFilename: String
) {

  /**
   * The logger of the server.
   */
  private val logger = Logger.getLogger("SensoComune Classifier Server")

  /**
   * The handler of the Classify command.
   */
  private val classify = Classify(
    tokenizer = this.buildTokenizer(tokenizerModelFilename),
    classifier = this.buildHANClassifier(hanClassifierModelFilename),
    classes = this.loadClasses(classesFilename))

  /**
   * Initialize Spark: set port and exceptions handling.
   */
  init {

    Spark.port(port)

    Spark.exception(MissingParameters::class.java) { exception, _, response ->
      response.status(400)
      response.body("Missing required parameters: %s\n".format((exception as MissingParameters).message))
    }

    Spark.exception(RuntimeException::class.java) { exception, _, response ->
      response.status(500)
      response.body("500 Server error\n")
      this.logger.warning("%d. Stacktrace: \n  %d".format(exception.toString(), exception.stackTrace.joinToString("\n  ")))
    }
  }

  /**
   * Start the server.
   */
  fun start() {

    Spark.path("/classify") {
      this.classifyRoute()
    }

    this.logger.info("SensoComune Classifier Server running on 'localhost:%d'\n".format(Spark.port()))
  }

  /**
   * Build a [NeuralTokenizer] loading its model from file.
   *
   * @param tokenizerModelFilename the filename of the tokenizer model
   *
   * @return a [NeuralTokenizer] with the given model
   */
  private fun buildTokenizer(tokenizerModelFilename: String): NeuralTokenizer {

    this.logger.info("Loading tokenizer model from '$tokenizerModelFilename'\n")
    val model = NeuralTokenizerModel.load(FileInputStream(File(tokenizerModelFilename)))

    return NeuralTokenizer(model)
  }

  /**
   * Build a [HANClassifier] loading its model from file.
   *
   * @param hanClassifierModelFilename the filename of the HANClassifier model
   *
   * @return a [HANClassifier] with the given model
   */
  private fun buildHANClassifier(hanClassifierModelFilename: String): HANClassifier {

    this.logger.info("Loading HAN model from '$hanClassifierModelFilename'\n")
    val model: HANClassifierModel = HANClassifierModel.load(FileInputStream(File(hanClassifierModelFilename)))

    return HANClassifier(model)
  }

  /**
   * @param classesFilename the name of the file containing the list of possible classes (one per line)
   *
   * @return the list of possible classes
   */
  private fun loadClasses(classesFilename: String): List<String> = File(classesFilename).readLines()

  /**
   * Define the '/classify' route.
   */
  private fun classifyRoute() {

    Spark.get("") { request, _ ->

      request.checkRequiredParams(requiredParams = listOf("gloss"))

      this.classify(gloss = request.queryParams("gloss"))
    }

    Spark.post("") { request, _ ->
      this.classify(gloss = request.body())
    }
  }

  /**
   * Check if all [requiredParams] are present in this [Request].
   *
   * @param requiredParams the list of required parameters to check
   *
   * @throws MissingParameters if at least one parameter is missing
   */
  private fun Request.checkRequiredParams(requiredParams: List<String>) {

    val missingParams: List<String> = this.getMissingParams(requiredParams)

    if (missingParams.isNotEmpty()) {
      throw MissingParameters(missingParams)
    }
  }

  /**
   * @param requiredParams a list of required parameters
   *
   * @return a list of required parameters that are missing in this [Request]
   */
  private fun Request.getMissingParams(requiredParams: List<String>): List<String> {

    val requestParams = this.queryParams()

    return requiredParams.filter { !requestParams.contains(it) }
  }
}
