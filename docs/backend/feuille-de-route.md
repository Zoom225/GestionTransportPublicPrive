# Feuille de route — Backend Gestion de Transport

## Correction de l'étape 1

Issue #1, branche existante `feature/backend-01-cadrage-gestion-transport`, PR #2 brouillon vers master. Cette correction porte uniquement sur les trois documents de cadrage. Aucune fonctionnalité, donnée, migration ou interface utilisateur n'est modifiée. Aucune nouvelle issue ni branche distante n'est nécessaire pour cette correction. Les futures issues et branches attendent l'accord du responsable du projet.

## Étape 2 proposée : structure et configuration

| Lot | Périmètre proposé | Validation attendue |
|---|---|---|
| 2.1 Décisions et contrats | Valider les droits GENERAL/LOCAL, les questions ouvertes et les frontières des six modules métier | Décisions explicites ; catégories STANDARD/ABONNE/VIP sans avantages inventés |
| 2.2 Environnements | Séparer configuration de développement et tests, PostgreSQL isolé, secrets hors Git ; prévoir les migrations non destructives | Démarrage reproductible, aucune connexion de test aux données réelles |
| 2.3 Structure | Préparer les modules agence, client, chauffeur, vehicule, trajet, reservation et configuration, securite, erreur ; respecter controller/service/repository/dto/mapper/entity selon les besoins | Dépendances de modules cohérentes, aucun accès HTTP direct aux repositories |
| 2.4 Contrats transversaux | Définir validation DTO, format d'erreur et politique d'autorisation ; décider le mécanisme d'authentification avant développement | Contrats revus et scénarios 401/403 définis |
| 2.5 Vérification du socle | Compilation Java 21 / Boot 4.1.2 et tests de contexte par profil, stratégie de tests PostgreSQL | clean verify reproductible ; limites H2 explicitement reconnues |

Cette étape prépare le socle et les contrats. Elle n'implémente pas les réservations, paiements ou pénalités. Le protocole transactionnel sera implémenté avec les trajets et réservations, puis démontré par des tests concurrents.

## Étapes métier ultérieures proposées

| Étape | Lot | Critère principal |
|---|---|---|
| 3 | Agences et administrateurs | Droits GENERAL/LOCAL validés et testés |
| 4 | Clients et catégories | STANDARD/ABONNE/VIP, sans règle non approuvée |
| 5 | Véhicules et chauffeurs | Affectations et disponibilités validées |
| 6 | Trajets PUBLIC/PRIVE | Un chauffeur dès la création, distinct des passagers |
| 7 | Réservations | Quatre places occupées maximum, attentes bloquantes incluses, annulation/expiration libératrices et refus concurrent du cinquième passager |
| 8 | Paiements et pénalités | Seulement après validation de leurs règles |
| 9 | Validation de sécurité et erreurs | Revue complète des autorisations et contrats déjà introduits dans le socle |
| 10 | Qualité et documentation | Tests, CI et preuves pédagogiques |

## Matrice AA1–AA11 provisoire

| Acquis | Axe de travail proposé | Preuve envisagée |
|---|---|---|
| AA1 | Analyse | Périmètre et acteurs |
| AA2 | Cahier des charges | Invariants et décisions validées |
| AA3 | Modèle conceptuel | Relations agence, véhicule, chauffeur, trajet et client |
| AA4 | Modèle relationnel | Contraintes et migrations revues lors de l'implémentation |
| AA5 | Architecture | Modules et séparation des responsabilités |
| AA6 | Sécurité | Tests des droits et de la propriété des ressources |
| AA7 | API REST | Contrats DTO et tests HTTP |
| AA8 | Interface utilisateur | Hors périmètre backend actuel |
| AA9 | Gestion des erreurs | Format d'erreur et tests des refus métier |
| AA10 | Tests et intégration continue | Vérification Maven et tests PostgreSQL concurrents |
| AA11 | Documentation et défense | Décisions traçables et preuves reproductibles |

Cette matrice n'est pas un référentiel officiel : les libellés doivent être rapprochés des acquis fournis par l'établissement.

## Vérifications de la correction

Contrôler le diff documentaire, la cohérence des invariants entre les trois documents, l'absence de terminologie étrangère à la Gestion de Transport et la préservation des fichiers locaux non suivis. Exécuter `mvnw.cmd clean verify` pour confirmer l'absence de régression du socle ; cela ne valide pas les fonctionnalités métier qui restent à développer. Commit et push limités aux trois documents ; description de la PR #2 mise à jour avec `Closes #1`, PR maintenue en brouillon sans fusion.
