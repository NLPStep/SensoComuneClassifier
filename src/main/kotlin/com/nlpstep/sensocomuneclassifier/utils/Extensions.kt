/* Copyright 2017-present The NLPStep Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.neuraltokenizer.Sentence

/**
 * Convert a [List] of [Sentence]s to nested [String]s (forms), excluding spaces.
 *
 * @return a list of list of strings
 */
fun List<Sentence>.toNestedStrings(): List<List<String>> = this.map {
  it.tokens.filter { !it.isSpace }.map {
    it.form
  }
}
