# SensoComuneClassifier

SensoComuneClassifier is a classifier of glosses based on the SensoComune ontology categories.

It is implemented as a server application that uses the [HANClassifier](https://github.com/KotlinNLP/HANClassifier "HANClassifier on GitHub") 
of the [KotlinNLP](http://kotlinnlp.com/ "KotlinNLP") library.

SensoComuneClassifier is part of [NLPStep](http://nlpstep.com/ "NLPStep").


## Getting Started

Run the server simply passing configuration parameters by command line to the
[RunServerKt](https://github.com/NLPStep/SensoComuneClassifier/blob/master/src/main/kotlin/com/nlpstep/sensocomuneclassifier/RunServer.kt "RunServerKt") 
script.


### Command line arguments

This is the help command output:
```
required arguments:
  -p PORT,                     the port listened from the server
  -t TOKENIZER_MODEL,          the filename of the model of the neural tokenizer
  -m HAN_CLASSIFIER_MODEL,     the filename of the model of the HAN classifier
  -c CLASSES_FILENAME          the filename of the possible classes (one per line)
```


## License

This software is released under the terms of the 
[Mozilla Public License, v. 2.0](https://mozilla.org/MPL/2.0/ "Mozilla Public License, v. 2.0")


## Contributions

We greatly appreciate any bug reports and contributions, which can be made by filing an issue or making a pull 
request through the [github page](https://github.com/NLPStep/SensoComuneClassifier "SensoComuneClassifier on GitHub").
