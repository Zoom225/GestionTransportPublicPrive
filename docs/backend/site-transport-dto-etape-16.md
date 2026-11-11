# Étape 16 — DTO SiteTransport

## Contrats préparés

- `CreationSiteTransportRequest(String nomVille)` : corps de création futur. Ne réalise pas de normalisation.
- `SiteTransportResponse(UUID id, String nomVille)` : projection de l'entité pour une réponse HTTP, créée via `SiteTransportResponse.depuis(SiteTransport)`.

Les propriétés JSON prévues sont `nomVille` en entrée et `id` / `nomVille` en sortie. La sérialisation HTTP et la validation des réponses n'ont pas été exécutées à cette étape, car aucun Controller n'est présent.

## Responsabilités

L'entité `SiteTransport` reste la seule source de normalisation et de validation métier. Le `SiteTransportService` reste responsable des opérations et des doublons. Les DTO ne sont pas utilisés directement par JPA.

## Tests

Quatre tests unitaires ciblent les records, la projection et le passage de validation au modèle. L'exécution Maven complète reste à vérifier dans l'environnement local Java 21.

## Dépendances et suites

Branche empilée sur `feature/backend-14-site-transport-service`, PR #15. Ne pas fusionner avant les PR #11, #13 et #15. Prochaine étape : Controller HTTP et gestion contrôlée des erreurs REST, avec tests MockMvc, dans une issue distincte.
