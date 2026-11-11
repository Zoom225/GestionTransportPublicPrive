# Feuille de route — Backend Gestion de Transport

Chaque étape requiert une issue GitHub, une branche `feature/backend-XX-...`, des tests adaptés, un commit et une pull request vers la branche cible. Pas de fusion automatique.

| Étape | Lot | Critère principal |
|---|---|---|
| 1 | Cadrage et architecture | Invariants et décisions documentés |
| 2 | Structure et configuration backend | Compilation et tests de base |
| 3 | Agences et administrateurs | Permissions GLOBAL/LOCAL testées |
| 4 | Clients et catégories | Validation et autorisations testées |
| 5 | Véhicules et chauffeurs | Affectations cohérentes |
| 6 | Trajets publics et privés | Chauffeur préaffecté et 4 places |
| 7 | Réservations | Aucune surréservation concurrente |
| 8 | Paiements et pénalités | Règles validées et testées |
| 9 | Sécurité et erreurs API | Authentification, autorisations, erreurs |
| 10 | Qualité et documentation | Tests, CI et preuves AA1–AA11 |

## Matrice pédagogique initiale
- AA1–AA2 : analyse et cahier des charges.
- AA3–AA4 : modèle conceptuel, relationnel et contraintes.
- AA5 : architecture en couches.
- AA6–AA7 : sécurité et API REST.
- AA8 : interface utilisateur, **hors périmètre backend actuel**.
- AA9 : gestion des erreurs.
- AA10 : tests et intégration continue.
- AA11 : documentation et défense.

Ces intitulés sont des axes de travail à rapprocher du libellé officiel des acquis.
