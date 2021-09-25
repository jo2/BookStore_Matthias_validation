# BookStore_Andreas

## Funktionale Anforderungen

* F.1: Das System muss Bücher speichern können.
Zu einem Buch gehört jeweils ein Identifikator, ein Titel, ein Autor, ein Erscheinungsjahr, ein Preis und eine Menge.
Die Kombination aus Autor und Titel muss eindeutig sein.
Der Titel muss mindestens zwei und maximal 30 Zeichen lang sein.
Der Name des Autors muss mindestens zwei und maximal 20 Zeichen lang sein.
Der Preis des Buches muss mindestens 1 € betragen.
Das Erscheinungsjahr des Buches muss zwischen 1000 und 2050 nach Christus liegen.
Die Menge des Buches darf nicht negativ sein.

* F.2: Das System muss eine Möglichkeit zum anlegen eines Buches bereitstellen.
Beim Anlegen eines Buches müssen die Beschränkungen für die Attribute des Buches aus Anforderung F.1 eingehalten werden.
Ein Buch, dass gegen diese Anforderung verstößt, darf nicht gespeichert werden können.

* F.3: Das System muss eine Möglichkeit zur Ausgabe der zu einem Buch gespeicherten Informationen bieten.

* F.4: Das System muss eine Möglichkeit zur Ausgabe der Liste aller gespeicherter Bücher bieten.
Die Liste der Bücher soll dabei absteigend anhand des Titels sortiert sein.

* F.5: Das System muss eine Möglichkeit zur Bearbeitung der zu einem Buch gespeicherten Informationen bieten.
Es darf nicht möglich sein, durch die Bearbeitung der Attribute eines Buches gegen die Beschränkungen für diese Attribute aus Anforderung F.1 zu verstoßen.
Die Anpassung des Preises darf die Beträge in den bestehenden Rechnungen nicht verändern.

* F.6: Das System muss eine Möglichkeit zur Löschung eines Buch bieten.
Das Löschen eines Buches darf die bestehenden Rechnungen nicht verändern.

* F.7: Das System muss Rechnungen speichern.
Zu einer Rechnung gehört das Rechnungsdatum, die Rechnungssumme und eine Liste an Rechnungspositionen.
Die Rechnungssumme ergibt sich dabei aus der Summe der Kosten der Rechnungspositionen.
Das Rechnungsdatum ist ergibt sich aus dem Datum und der Uhrzeit des Kaufs.

* F.8: Das System muss eine Möglichkeit zum Auslesen aller Rechnungsdaten anbieten.
Die Rechnungen sollen dabei nach dem Rechnungsdatum aufsteigen sortiert werden.

* F.9: Das System muss Rechnungspositionen speichern.
Zu einer Rechnungsposition gehört jeweils ein Identifikator, der Titel, der Autor und der Preis des zugehörigen Buches,die gewünschte Anzahl an Exemplaren, ein Rabatt, der gewährt werden kann, und die aufsummierten Kosten dieser Rechnungsposition.
Für die Attribute, die auch im Buch gespeichert werden, gelten die selben Beschränkungen wie in Anforderung F.1.
Des Weiteren muss die Anzahl gewünschter Exemplare mindestens eins und maximal sechs betragen.
Der gewährte Rabatt muss mindestens 0 % und maximal 20 % betragen.
Die Kosten berechnen sich aus der Anzahl an Exemplaren multipliziert mit dem Preis je Exemplar abzüglich des gewährten Rabattes.

* F.10: Das System muss eine Möglichkeit bieten, mehrere Bücher gleichzeitig kaufen zu können.
Dazu müssen mehrere Bücher ausgewählt werden können.
Das System muss zusätzlich zur Auswahl der Bücher eine Möglichkeit bereit stellen, die gewünschte Anzahl an Exemplaren sowie den gewährten Rabatt anzugeben.
Das System muss dem Nutzer dabei die aktuellen Kosten sowohl der einzelnen Rechnungspositionen als auch der gesamten Rechnung anzeigen.

## Nicht-funktionale Anforderungen

* N.1: Das System muss über ein Backend und eine Benutzerschnittstelle verfügen.
Das Backend muss in Spring Boot implementiert werden, die Benutzerschnittstelle in Thymeleaf.
Dynamische Inhalte in der Benutzerschnittstelle dürfen in JavaScript implementiert werden.
Zur Gestaltung der Benutzerschnittstelle soll CSS verwendet werden, ergänzende Bibliotheken wie Bootstrap dürfen verwendet werden.

* N.2: Das System soll eine Testabdeckung von mindestens 80 % aufweisen.
Code, der durch Frameworks bereitgestellt wird, muss nicht explizit getestet werden.
Stattdessen kann davon ausgegangen werden, dass dieser Code bereits getestet wurde.
Das System soll sowohl durch Unit- als auch durch Integrationstests getestet werden.

* N.3: Für die persistente Speicherung der Daten soll eine SQL-Datenbank verwendet werden.
Die Auswahl der SQL-Datenbank ist dabei dem Entwickler überlassen.
Die Daten müssen einen Neustart des Systems überdauern.
