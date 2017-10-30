/* Copyright 2017-present The NLPStep Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.nlpstep.sensocomuneclassifier

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

/**
 * The interpreter of command line arguments.
 *
 * @param args the array of command line arguments
 */
class CommandLineArguments(args: Array<String>) {

  /**
   * The parser of the string arguments.
   */
  private val parser = ArgParser(args)

  /**
   * The port listened by the server (default = 3000).
   */
  val port: Int by parser.storing(
    "-p",
    "--port",
    help="the port listened from the server"
  ) { toInt() }.default(3000)

  /**
   * The filename of the NeuralTokenizer serialized model.
   */
  val tokenizerModel: String by parser.storing(
    "-t",
    "--tokenizer-model",
    help="the filename of the tokenizer serialized model"
  )

  /**
   * The filename of the HANClassifier serialized model.
   */
  val hanClassifierModel: String by parser.storing(
    "-m",
    "--han-model",
    help="the filename of the HANClassifier serialized model"
  )

  /**
   * The filename of the classes.
   */
  val classesFilename: String by parser.storing(
    "-c",
    "--classes-filename",
    help="the filename of the classes (one per line)"
  )

  /**
   * Force parsing all arguments (only read ones are parsed by default).
   */
  init {

    parser.force()
  }
}
