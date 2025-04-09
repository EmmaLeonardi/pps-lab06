# Exercise 2
- take a look to this [oop exam](https://bitbucket.org/mviroli/oop2018-esami/src/master/a03b/e1/Test.java)
- implement the solution using the scala collection library
- follow the line guides described in class (immutable var over mutable val, ...)


Si consulti la documentazione dell'interfaccia ConferenceReviewing, che modella i risultati del processo di revisione
degli articoli di una conferenza. Ogni articolo viene da revisionato da uno o più revisori anonimi, ognuno dei quali fornisce
una valutazione (score) da 0 a 10 per 4 diverse "domande", modellate da ConferenceReviewing.Question. Un articolo viene
accettato se il valore medio della valutazione alla domanda "FINAL" è >5 e se ha almeno una valutazione "RELEVANCE" >= 8.

Implementare ConferenceReviewing attraverso una classe ConferenceReviewingImpl con costruttore senza argomenti,
in modo che passi tutti i test di cui sotto, realizzati per essere autoesplicativi.

Sono considerati opzionali ai fini della possibilità di correggere l'esercizio, ma concorrono comunque al raggiungimento
della totalità del punteggio:
- implementazione dei test opzionali (relativi alla realizzazione del metodo averageWeightedFinalScoreMap)
- la qualità della soluzione, in particolare con minimizzazione di ripetizioni e codice non inutilmente complesso