Vorgehensweise beim Reproduzieren der Ergebnisse:

Der Aufruf erfolgt in der Main-Klasse. Dieser ist in die zwei folgenden Bereiche unterteilt:
1) Konvertieren der csv-daten in ein eigenes dataset-format
2) Konvertieren des dataset-formats in das WEKA-format und anschließendes erzeugen der Modelle.

Man wähle unter 1) die jeweillige Person im oberen Bereich aus. Man kommentiere im Bereich "Building Models" den Aufruf
von genearateArffFile() ein, den man ausführen möchte: der eine Aufruf übergibt die NICHT vorverarbeiteten daten
(dataSet), der andere üergibt die vorverarbeiteten daten (featureDataSet). Anschließend kommentiere man
buildAnNaiveBayesModelAndEvaluate() ein, wenn man ein Naive-Bayes model generieren will. Will man ein Bayes Net bauen,
kommentiere man buildAnBayesNetAndEvaluate() ein. Man beachte dabei die übergebenen Optionen (siehe Beschreibung
im Code).


