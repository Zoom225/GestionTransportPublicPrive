# Première tranche du modèle Trajet — Gestion de Transport

## Périmètre réel et base

Issue #8 : https://github.com/Zoom225/GestionTransportPublicPrive/issues/8. Branche existante `feature/backend-08-modele-trajet`, initialement au même commit que origin/master : `0584785515da3963c7a4ea92322b7eb82f4a2461`. Travail dans un worktree isolé ; aucun changement local suivi ou non suivi du dossier principal importé, modifié ou supprimé.

Deux classes seulement sont ajoutées : `trajet/TypeTrajet.java` et son test JSON. Aucun Controller, Service, Repository, DTO, Mapper, package vide ou package-info.java. Le package racine `com.gestion.GestionTransport` est conservé. Aucune dépendance, configuration, migration, donnée ou interface utilisateur modifiée.

La branche suivie contient uniquement le démarrage et son test de contexte. Les configurations, la sécurité et les tests supplémentaires présents dans le dossier principal sont du travail local non publié : ils sont conservés, pas présumés intégrés à master.

## Étude effective de la référence distante

Analyse via le connecteur GitHub de la version distante, le 11 novembre 2026 ; HEAD de la référence relevé à `2b6edae4e70711a3a4ed64609741ebde70189fe5`. Les liens ci-dessous désignent les fichiers externes et leurs classes exactes ; seuls leurs chemins de provenance conservent les identifiants du dépôt source. Aucun code ni nom de classe source n'est transposé dans le domaine cible.

Références de preuve :

- [S1 — service de création](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/main/java/com/padell/padell/service/impl/MatchServiceImpl.java), blob `98390117ce5995b52471c715689b7faf78a740f5`.
- [S2 — service de réservation](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/main/java/com/padell/padell/service/impl/ReservationServiceImpl.java), blob `30fc5f681b55f44a5ea5424809131d85496223d8`.
- [S3 — accès aux données et verrou](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/main/java/com/padell/padell/repository/MatchRepository.java), blob `187fe7961e02bffa7a20515675ac75ef76d11ce2`.
- [S4 — validation de création](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/main/java/com/padell/padell/dto/request/CreateMatchRequest.java).
- [S5 — états source](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/main/java/com/padell/padell/entity/enums/StatutMatch.java).
- [S6 — tests de création](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/test/java/com/padell/padell/service/MatchServiceTest.java).
- [S7 — tests de réservation](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/test/java/com/padell/padell/service/ReservationServiceTest.java).
- [S8 — mapping persistant source](https://github.com/Zoom225/padellAout/blob/2b6edae4e70711a3a4ed64609741ebde70189fe5/src/main/java/com/padell/padell/entity/Match.java).

Les tests source ont été lus, pas exécutés. Leur présence n'est pas une preuve de couverture complète. La version distante diffère de la copie locale étudiée précédemment ; les constats suivants portent exclusivement sur la version distante.

## Tableau des règles prouvées et adaptations

La colonne classe/méthode renvoie à la classe exacte du fichier externe numéroté et à son point de contrôle, sans reprendre ses noms de domaine dans le code cible.

| Règle source constatée | Classe / méthode ou point de contrôle | Adaptation transport | Décision ouverte |
|---|---|---|---|
| Création avec choix PUBLIC ou PRIVE, contrôle de chaîne, date future ou présente et ressource obligatoire | S4, contraintes du constructeur record ; S1, méthode de création transactionnelle | TypeTrajet PUBLIC/PRIVE uniquement dans cette tranche | Client autorisé, parcours de demande et autres champs à définir |
| Refus de création si impayé ou pénalité, horizons 21/14/5 jours selon catégorie, restrictions de périmètre | S1, création et validateBookingDelay ; S6, cas impayé/pénalité/horizon | Aucun transfert automatique de privilège ou sanction ; rôles GENERAL et LOCAL conservés comme exigences | Tarifs, catégories, horizons et droits précis à valider |
| Refus des chevauchements, prise en compte d'un intervalle paramétré, fermeture et horaires | S1, isSlotAvailable et contrôles de calendrier ; S3, requête avec début existant < fin demandée et fin existante > début demandé ; S6, créneau déjà pris | Vérifier ensuite véhicule et chauffeur, y compris repositionnement et fuseau | Disponibilité, marges, nuits et autorité de sélection à concevoir |
| Création à compteur zéro, réservation automatique de l'organisateur en attente et paiement associé | S1, création et méthode privée de réservation initiale ; S6, succès | Ne jamais faire du chauffeur un passager automatique | L'organisateur voyage-t-il ? Faut-il une réservation explicite ? |
| Public réservé pour soi, privé sur invitation de l'organisateur ; refus complet/annulé/doublon | S2, create et createForInvites ; S7, tests de réservation et d'invitation | Règles d'accès PUBLIC/PRIVE à confirmer séparément ; enum seul ne les applique pas | Qui peut demander, inviter, payer, réserver pour autrui ? |
| La création d'une réservation EN_ATTENTE n'incrémente pas le compteur dans cette version ; annulation ne décrémente que si CONFIRMEE ; confirm ne modifie pas ce compteur | S2, create/cancel/confirm ; S7, vérifications never sur les incréments | Quatre réservations occupant une place maximum ; inclure toute attente bloquante, libérer annulées/expirées | Politique d'attente, expiration et cohérence du compteur à concevoir ; ne pas copier ce comportement |
| Un incrément explicite refuse un compteur déjà à quatre et marque COMPLET à quatre ; états PLANIFIE, COMPLET, ANNULE | S1, incrementPlayers et decrementPlayers ; S5 | Maximum quatre passagers, chauffeur exclu ; disponibilité distincte de la seule valeur du compteur | Cycle d'état transport et réservation à valider |
| À J-1, un privé incomplet planifié peut être converti en public avec pénalité | S1, vérification périodique et convertToPublic | Aucun automatisme de conversion ni pénalité introduit | Politique transport explicitement à valider |
| Méthode de lecture avec PESSIMISTIC_WRITE disponible mais non appelée dans le chemin de création de réservation lu ; lecture via findById | S3, findByIdForUpdate ; S1, lecture par identifiant ; S2, create | Protection transactionnelle commune à concevoir pour toutes les occupations et affectations | Stratégie PostgreSQL et tests concurrents dans une tranche dédiée ; aucune garantie actuelle déduite de la seule annotation |
| Aucune relation de chauffeur dans le modèle source examiné | S8, champs et relations de la classe persistante | Chauffeur obligatoire dès création du trajet, distinct des passagers | Identité, qualifications et relation obligatoire à définir ; aucune affectation automatique héritée |

## Éléments effectivement implémentés

`com.gestion.GestionTransport.trajet.TypeTrajet` définit les deux types de départ demandés par un client. Il ne représente ni un trajet créé, ni une disponibilité, ni une réservation. Aucun état disponible n'est introduit.

Les tests utilisent le mapper JSON réellement auto-configuré par Spring Boot 4.1.2 : aller-retour des deux valeurs, noms JSON conservés, rejet d'un libellé inconnu, de la casse non prévue et d'une variante accentuée non contractuelle. Aucune configuration JSON ajoutée ni duplication de validation. Ces tests ne valident pas une requête complète, l'obligation de type non nul, le refus de représentations numériques ou un endpoint HTTP ; ces contrats ne sont pas définis dans cette tranche.

## Blocage explicite de l'entité Trajet

Aucune entité Chauffeur n'existe dans la base suivie, ni modèle d'identité commun client/chauffeur ou véhicule. Créer Trajet avec un identifiant arbitraire de chauffeur, un objet fictif ou une relation optionnelle masquerait le prérequis et contredirait l'invariant. L'entité JPA Trajet est donc reportée.

Ordre minimal proposé, une tranche après l'autre : définir et valider le modèle Chauffeur et son identité ; créer ensuite l'entité Trajet avec une véritable relation obligatoire et son test de persistance. Préciser les dépendances Client/Véhicule avant de les rendre obligatoires, sans créer leurs couches par anticipation. Une future FK NOT NULL ne suffira pas à démontrer la disponibilité ou à distinguer les profils d'une même personne.

L'affectation automatique reste une règle cible à concevoir et valider : aucun service ne sélectionne un chauffeur dans cette tranche. Sans chauffeur affectable, il faut décider entre refus de création et demande différée qui ne constitue pas encore un trajet créé. Il est interdit de persister un trajet sans chauffeur en attendant une affectation. Aucun choix arbitraire de chauffeur, aucune donnée fictive.

Restent non implémentés : création d'un trajet, choix du client via API, relation au chauffeur, exclusion chauffeur/passager, capacité incluant attentes bloquantes, disponibilité, concurrence, droits GENERAL/LOCAL, tarifs et paiements. Ces règles restent impératives ou à valider selon le cadrage, mais leur fonctionnement n'est pas revendiqué.

## JPA, PostgreSQL et données

Aucune annotation Entity, aucune relation persistante et aucun schéma SQL ajouté ou modifié : l'enum seul ne crée aucune table ni colonne. Aucun ddl-auto ajouté, aucun create-drop sur une base persistante. Le socle de test existant utilise H2 en mémoire ; PostgreSQL non testé. Pour une future persistance, évaluer un stockage textuel des types plutôt que leur ordinal et une migration non destructive, après analyse du schéma réel.

## Vérifications et limites

Avant modification : clean verify réussi, 1 test, 0 échec, 0 erreur, 0 ignoré, 24,925 s. Après ajout : clean verify réussi, 6 tests, 0 échec, 0 erreur, 0 ignoré, 28,981 s (5 tests JSON et le test de contexte existant). Le test de contexte et les tests de contrat JSON ne prouvent aucune règle d'affectation ou de réservation.

Les avertissements du socle restent hors de cette tranche : Open Session in View, sécurité automatique de développement et agent Mockito dynamique. Le travail local de configuration est préservé, mais pas intégré implicitement.

## Audits parallèles et arrêt

PR #5 et #7 ouvertes en brouillon, non fusionnées ; elles ajoutent deux versions différentes de docs/backend/audit-redondances.md et risquent un conflit ajout/ajout lors d'une intégration successive. Les deux audits sont inchangés ici. Leur consolidation conservant les deux historiques doit être revue avant toute fusion de l'issue #8.

Une seule prochaine couche recommandée : modèle métier minimal Chauffeur, après validation de l'identité et des conditions d'affectation, sans Controller, Service, Repository, DTO ou Mapper anticipé. Aucun développement de cette prochaine couche dans l'issue #8. Arrêt après PR brouillon pour validation.
