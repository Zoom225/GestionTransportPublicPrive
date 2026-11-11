# Cadrage — Gestion de Transport Public et Privé

## Périmètre de l'étape 1
Backend uniquement : Java 21, Spring Boot 4.1.2, Maven, Spring Security, Spring Data JPA, PostgreSQL. Le frontend est hors périmètre.

## Objectif
Concevoir une API de gestion des trajets, chauffeurs, véhicules, clients et réservations, avec des règles simples, vérifiables et adaptées à un TFE.

## Invariants validés
1. Un trajet ouvert aux réservations possède un chauffeur affecté.
2. Le chauffeur est distinct des passagers et ne consomme aucune place passager.
3. Chaque trajet dispose de quatre places passagers maximum.
4. La réservation d'une cinquième place doit être refusée, y compris en cas de demandes concurrentes.
5. Un trajet est de type PUBLIC ou PRIVÉ.

## Hypothèses à confirmer
- Les droits précis des administrateurs GLOBAL et LOCAL.
- Les avantages des catégories STANDARD, ABONNÉ et VIP.
- Les règles de création des trajets, tarifs, paiements, annulations et pénalités.
- Le nombre de véhicules par agence et les contrôles de disponibilité des chauffeurs.
- Les conditions de passage d'un trajet de brouillon à ouvert.

## Contraintes de développement
- Architecture en couches Controller / Service / Repository, avec DTO et mapper.
- Validation des entrées, contrôle des autorisations, gestion cohérente des erreurs.
- Tests unitaires et d'intégration sur les invariants critiques.
- Pas de secrets dans Git ; pas de migration destructrice.
- Une issue, une branche, des commits et une pull request pour chaque évolution.
