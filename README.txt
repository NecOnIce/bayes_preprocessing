Vorgehensweise beim Reproduzieren der Ergebnisse:

Der Aufruf erfolgt in der Main-Klasse. Dieser ist in die zwei folgenden Bereiche unterteilt:
1) Konvertieren der csv-daten in ein eigenes dataset-format
2) Konvertieren des dataset-formats in das WEKA-format und anschließendes erzeugen der Modelle.

Man wähle unter 1) die jeweillige Person aus. Anschließend kommentiere man die entsprechende Methode für Naive Bayes oder
Bayes Net unter 2) ein (oder eben auch beide). Dabei ist unbedingt auf die Nutzung des Filters zu achten (siehe nächten Absatz).

Benutzung eines Filters:
Man beachte beim reproduzieren unbedingt die Bermerkung als Kommentar, dass nicht benötigte Attribute durch einen Filter
entfernt wurden! Es gilt dabei auf folgendes zu achten:
Am Ende der Methode generateArffFile() in der Main-Klasse wird eine Methode für den Filter aufgerufen. Der erste Parameter
dieser Methode beschreibt die zu filternden Daten und der zweite definiert, ob es sich um original-daten oder vorverarbeitete
Daten handelt (true --> originale daten, also NICHT vorverarbeitet; false --> vorverarbeitete Daten).
Es ist immer darauf zu achten, dass diese Variable richtig gesetzt ist, da sonst andere Werte entstehen. Beim Wechsel von orginalen
Daten zu Vorverarbeitenden (oder umgekehrt) MUSS diese Variable auch immer angepasst werden!!!
