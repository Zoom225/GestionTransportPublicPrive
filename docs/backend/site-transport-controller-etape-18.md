# Étape 18 — Controller REST SiteTransport

Adaptation du principe de séparation Controller / Service / DTO constaté dans le projet source fourni, sans copier ses classes ni ses noms métier.

## API

- `GET /api/sites` : 200 et tableau de `SiteTransportResponse`.
- `GET /api/sites/{id}` : 200 ou 404 ; UUID mal formé : 400.
- `POST /api/sites` : 201, `Location: /api/sites/{id}`, réponse DTO ; 400 si entrée invalide, 409 si doublon.

Le Controller délègue toutes les opérations au Service. Le gestionnaire HTTP utilise `ProblemDetail` et ne renvoie aucune cause SQL.

## Sécurité

La configuration Spring Security existante est préservée. Aucune autorisation publique n'est introduite ici. Les tests MVC utilisent un utilisateur simulé et un jeton CSRF pour les POST. Les politiques d'accès par rôle GENERAL / LOCAL feront l'objet d'une étape séparée, après cadrage.

## Validation

Onze tests HTTP sont écrits ; leur exécution réelle via Maven doit être effectuée localement. Cette PR ne garantit pas que la compilation et les tests passent tant que le résultat de `.\\mvnw.cmd clean verify` n'est pas fourni.

## Dépendances

PR #11 → #13 → #15 → #17 → cette étape. Pas de fusion automatique.
