/* Copyright 2017-present The NLPStep Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.nlpstep.sensocomuneclassifier

/**
 * Run the SensoComune Classifier Server.
 */
fun main(args: Array<String>) {

  val parsedArgs = CommandLineArguments(args)

  ClassifierServer(
    port = parsedArgs.port,
    tokenizerModelFilename = parsedArgs.tokenizerModel,
    hanClassifierModelFilename = parsedArgs.hanClassifierModel,
    classesFilename = parsedArgs.classesFilename
  ).start()
}
