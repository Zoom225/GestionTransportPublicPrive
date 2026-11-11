# Cadrage — Gestion de Transport Public et Privé

## Périmètre
Backend uniquement : Java 21, Spring Boot 4.1.2, Maven, Spring Security, Spring Data JPA et PostgreSQL. Cette correction de l'issue #1 et de la PR #2 est exclusivement documentaire. Aucun frontend, code Java, schéma SQL ou secret n'est modifié.

## Invariants imposés
1. Chaque trajet possède exactement un chauffeur déjà affecté dès sa création, quel que soit son état ou son type PUBLIC/PRIVE. Un éventuel brouillon ne fait pas exception.
2. Le chauffeur est distinct des passagers du même trajet et ne compte jamais parmi les quatre places passagers. Une réaffectation doit préserver ces règles sans laisser de trajet sans chauffeur.
3. La capacité maximale est de quatre réservations occupant chacune une place passager. Le nombre de réservations confirmées seul ne suffit pas à contrôler cette limite.
4. Toute réservation en attente qui bloque une place entre dans ce décompte. Les réservations annulées ou expirées libèrent leur place ; leur historique peut être conservé.
5. La cinquième réservation occupant une place est refusée, y compris sous concurrence. Quatre passagers et un chauffeur sont autorisés ; cinq passagers sont interdits.

## Décisions restant à préciser
- Conserver les deux droits administrateurs GLOBAL et LOCAL ; préciser leurs opérations autorisées et le périmètre attribué à LOCAL.
- Conserver les catégories clients STANDARD, ABONNE et VIP ; aucun avantage, tarif ou délai préférentiel n'est validé.
- Définir qui crée, modifie ou annule les trajets et qui affecte ou remplace le chauffeur et le véhicule.
- Définir la politique de blocage d'une place en attente, sa durée et les conditions d'expiration. Dès qu'une attente bloque une place, elle compte dans la capacité.
- Préciser les états du trajet, les droits liés aux types PUBLIC/PRIVE, la disponibilité des ressources, les tarifs, paiements, annulations et éventuelles pénalités.

## Architecture proposée
Organisation par modules métier `agence`, `client`, `chauffeur`, `vehicule`, `trajet`, `reservation`, chacun structuré en `controller`, `service`, `repository`, `dto`, `mapper`, `entity`. Modules transversaux : `configuration`, `securite`, `erreur`. Les responsabilités et le découpage de l'étape 2 sont détaillés dans les deux autres documents.

## Validation attendue
Les futures implémentations doivent tester l'absence de chauffeur dès la création, l'identité chauffeur/passager, la capacité incluant les attentes bloquantes, la libération sur annulation/expiration et les réservations concurrentes sur PostgreSQL isolé. Une compilation du squelette ne prouve pas ces comportements.

## Traçabilité
Correction sur la branche existante `feature/backend-01-cadrage-gestion-transport`, issue #1 et PR #2 vers master. PR maintenue en brouillon, sans fusion. Aucune nouvelle issue ou branche distante pour cette correction.
