# Modèle métier — Gestion de Transport

Ce document définit les invariants de cadrage et propose une architecture ; aucune fonctionnalité n'est implémentée.

## Concepts

| Concept | Responsabilité |
|---|---|
| Agence | Regrouper les moyens et le périmètre de gestion locale |
| Administrateur | Porter les droits GENERAL ou LOCAL, dont le détail reste à valider |
| Chauffeur | Conduire le trajet auquel il est affecté dès sa création |
| Véhicule | Moyen de transport affecté au trajet |
| Client | Réserver une place ; catégorie STANDARD, ABONNE ou VIP sans avantage présumé |
| Trajet | Départ, arrivée, horaires, type PUBLIC/PRIVE, chauffeur obligatoire et véhicule |
| Réservation | Relier un passager à un trajet et porter l'état d'occupation d'une place |
| Paiement et pénalité | Concepts éventuels dont les règles doivent être validées avant développement |

## Capacité et identité

Chaque trajet a exactement un chauffeur non nul dès sa création. Le chauffeur n'est jamais un passager du même trajet et ne consomme aucune place passager. Si une personne peut posséder à la fois un profil client et chauffeur, le contrôle doit comparer une identité de personne commune, et non des identifiants de tables différentes ; le modèle d'identité reste à préciser.

`occupation = nombre de réservations occupant une place`, avec `0 <= occupation <= 4`.

| Situation de réservation | Occupe une place |
|---|---|
| Confirmée | Oui |
| En attente avec blocage de place | Oui |
| En attente sans blocage | Non ; obtention de place soumise à un nouveau contrôle atomique |
| Annulée ou expirée | Non |

La politique d'attente bloquante et son délai restent ouverts. Aucun parcours ne doit confirmer une réservation sans obtenir une place. Une réservation annulée ou expirée ne peut pas être réactivée sans contrôle de capacité. Annulation et expiration libèrent une place une seule fois, même si elles sont déclenchées plusieurs fois ou simultanément.

## Contrôle transactionnel proposé

Le service de réservation ouvre une transaction et verrouille la ligne du trajet en écriture, par exemple via un verrou JPA PESSIMISTIC_WRITE sur PostgreSQL. Sous ce verrou, il vérifie le chauffeur affecté, l'identité du passager, les autorisations et le nombre des réservations occupant une place. Si ce nombre vaut quatre, la nouvelle occupation est refusée. Sinon la réservation et son état sont enregistrés avant la validation de la transaction.

Tous les chemins qui changent l'occupation utilisent le même verrou de trajet : création, confirmation d'une attente non bloquante, annulation, expiration et éventuelle réactivation. Les modifications du chauffeur et de la capacité doivent suivre le même protocole. Les lectures de capacité se font après acquisition du verrou, depuis la base, sans compteur périmé. Un échec provoque un rollback ; un conflit de verrou ne doit jamais être traité comme une réservation réussie.

Un simple comptage suivi d'une insertion sans verrou laisse une course possible. Une contrainte CHECK sur une ligne ne suffit pas à limiter le nombre de réservations liées. Les contraintes de référence, la stratégie d'identité et les protections complémentaires en base seront conçues à l'étape d'implémentation, sans migration dans ce cadrage.

Test attendu : partir de trois places occupées et lancer deux transactions concurrentes ; une seule obtient la dernière place, l'autre est refusée, l'occupation finale vaut quatre. Tester également la concurrence entre confirmation, expiration, annulation et réaffectation du chauffeur.

## Packages et responsabilités proposés

Conserver pour l'instant la racine existante `com.gestion.GestionTransport`. Sous cette racine, chaque module `agence`, `client`, `chauffeur`, `vehicule`, `trajet`, `reservation` comporte :

| Sous-package | Responsabilité |
|---|---|
| controller | Contrats HTTP, validation des entrées et codes de réponse |
| service | Cas d'utilisation, transactions, autorisations et invariants métier |
| repository | Requêtes JPA et méthodes de verrouillage ; accès interne au module |
| dto | Requêtes et réponses séparées des entités, sans données sensibles |
| mapper | Conversions explicites DTO/entités, sans décision métier |
| entity | État persistant et relations du module |

Les contrôleurs appellent les services ; les services utilisent leurs repositories et les services exposés des autres modules. Éviter les accès directs aux repositories d'un autre module et les dépendances circulaires. Le module reservation orchestre l'occupation avec le service trajet et son protocole de verrouillage. Ne pas créer de couches vides ni d'interface de service systématique sans besoin.

| Module transversal | Responsabilité |
|---|---|
| configuration | Configuration technique et profils d'environnement |
| securite | Authentification, principal et autorisations GENERAL/LOCAL ; catégories clients séparées des droits |
| erreur | Erreurs métier, traduction HTTP centralisée et format de réponse stable |

## Décisions ouvertes

Droits précis GENERAL/LOCAL, avantages éventuels STANDARD/ABONNE/VIP, identité commune client/chauffeur, rattachement aux agences, disponibilité des ressources, remplacement du véhicule/chauffeur, transitions d'état, délais d'expiration, politique tarifaire et pénalités. Aucun choix ouvert n'assouplit les invariants de chauffeur obligatoire et de quatre places maximum.
