# Modèle SiteTransport — Étape 10

## Traçabilité et base

Issue #10 : https://github.com/Zoom225/GestionTransportPublicPrive/issues/10. Branche existante `feature/backend-10-modele-site-transport`, initialement identique à origin/master au commit `0584785515da3963c7a4ea92322b7eb82f4a2461`. Travail dans un worktree isolé ; tous les fichiers locaux suivis et non suivis du dossier principal restent préservés.

Cette tranche crée uniquement une entité dans `com.gestion.GestionTransport.sitetransport`, deux classes de tests et cette documentation. Aucun Controller, Service, Repository, DTO, Mapper, package vide, initialiseur ou trajet. Aucun POM, profil ou fichier d'une autre branche modifié.

Les PR #5, #7 et #9 ont été vérifiées ouvertes en brouillon, non fusionnées. Les PR #5 et #7 ajoutent des contenus différents au même audit : consolidation à revoir avant intégration successive, sans écraser l'un des historiques. La note présente a un chemin propre et ne modifie aucun de ces audits ni le modèle de la PR #9.

## Analyse effective du code de référence

Le backend distant indiqué dans la mission a été lu via le connecteur GitHub le 11 novembre 2026. Commit de référence : `2b6edae4e70711a3a4ed64609741ebde70189fe5`. Les identifiants ci-dessous permettent de vérifier les fichiers externes sans reprendre leurs règles spécialisées dans le domaine cible.

| Fichier externe étudié | Preuve dans le code | Adaptation retenue |
|---|---|---|
| Site.java, blob `4a439d4a5a21e53fbc91e7ebd288ddafbf2e2e43` | Nom et adresse non nuls, horaires, durées, année et collections avec cascades ; aucune unicité déclarée sur le nom | Garder uniquement une identité et un nom de ville ; aucune collection ni cascade de suppression |
| BaseEntity.java, blob `48ce4f5d2689a7bc5a34eaad7abcc6561e2ff6cd` | Identifiant généré et dates d'audit via listener | UUID stable propre à cette seule entité ; pas de superclasse abstraite ni dates non justifiées |
| SiteRequest.java, blob `d5a9f632441a8fe8319ab939751d66052e196114` | Nom/adresse non blancs ; champs temporels obligatoires et bornes sur durées | Nom obligatoire et validé au constructeur ; aucun DTO ni durée ajoutée |
| SiteRepository.java, blob `387f788a60469cb54fc9f0cbb229a2b96e6b8b17` | Interface JPA standard sans recherche par nom normalisé | Ne pas déduire une protection anti-doublon ; aucun repository dans cette tranche |
| SiteServiceImpl.java, blob `879781537a121bf838b615a355a6d8981c28f59a` | create sauvegarde directement, update recopie les propriétés ; pas de contrôle d'unicité | L'unicité cible est définie explicitement sur le nom canonique et protégée en base |
| DataInitializer.java, blob `1267f78681dacea60a8f1a6777db068ea5136b2a` | CommandLineRunner composant, count() > 0 court-circuite la création des lieux, crée aussi des données d'exemple et modifie des comptes existants | Ne rien copier ni exécuter ; le contrôle de quantité ne répare pas une liste partielle de villes et ne garantit pas l'idempotence par ville |

Aucune unicité du nom n'a été trouvée dans ces composants. Les dates d'ouverture, les durées et les données d'exemple ne constituent pas des exigences de transport validées. Aucun secret, compte de démonstration ou code de l'initialiseur de référence repris dans le dépôt cible.

## Modèle effectivement obtenu

Table JPA `sites_transport` avec deux champs seulement :

| Champ | Rôle et contrainte |
|---|---|
| id | UUID aléatoire attribué une seule fois au constructeur, clé primaire, non modifiable par l'API du modèle et colonne non mise à jour ; conservé après persistance et relecture |
| nomVille | Nom canonique obligatoire, longueur technique maximale 120, colonne nom_ville non nulle, contrainte unique uk_site_transport_nom_ville |

Le constructeur sans argument protégé est réservé à JPA. Aucun setter public : ni identifiant ni nom ne sont modifiables par le code appelant. Le renommage, les fusions de villes et les relations futures ne sont pas développés. L'identifiant ne dépend pas de l'orthographe du nom et deux objets nouveaux ont des UUID distincts ; la contrainte de ville empêche leur persistance en double.

## Normalisation et unicité

Le constructeur applique une seule fonction de normalisation :

1. Refuser null.
2. Normaliser Unicode en NFKC (y compris caractères de largeur différente et accents combinés).
3. Remplacer les suites d'espaces Unicode et de blancs Java par un espace simple, puis retirer les espaces aux extrémités.
4. Passer en minuscules avec Locale.ROOT.
5. Refuser le résultat vide ou dépassant 120 unités UTF-16 (limite Java conservatrice).

Exemples : ` Namur `, `NAMUR`, les espaces insécables entourant le nom et sa variante Unicode pleine largeur sont stockés sous `namur`. Les accents sont conservés : une autre orthographe sans accent n'est pas automatiquement la même ville. Les synonymes, noms traduits et villes homonymes dans plusieurs pays restent des décisions futures. Pour cette tranche, une ville canonique correspond à un seul site de transport ; plusieurs agences dans une ville ne sont pas représentées.

Pour rester minimal, aucun second champ d'affichage n'est conservé : les noms persistés sont canoniques en minuscules. La présentation des libellés humains sera traitée seulement si le besoin est validé.

L'unicité porte sur nom_ville en base, pas sur un simple exists préalable : deux UUID distincts ne permettent pas deux lignes canoniques identiques. Les tests provoquent réellement des violations SQL et vérifient qu'une seule ligne subsiste. Aucun test concurrent multithread n'est revendiqué.

Limite importante : la normalisation est assurée par le constructeur Java, pas par un trigger SQL. Un accès SQL direct qui contourne le modèle pourrait insérer des variantes non canoniques distinctes ; une future stratégie d'import/migration devra utiliser la même convention et, si nécessaire, ajouter des protections PostgreSQL dédiées après revue. Il n'est pas annoncé de normalisation universelle au niveau SQL.

## Villes initiales cibles et stratégie différée

Villes envisagées : **Bruxelles, Namur, Mons, Charleroi** ; clés canoniques respectives `bruxelles`, `namur`, `mons`, `charleroi`. Elles sont documentées et utilisées comme fixtures de tests uniquement. Aucune donnée initiale de production créée.

Stratégie idempotente proposée pour une étape ultérieure, à valider :

- Exécution explicite contrôlée, hors démarrage automatique de production.
- Normaliser chaque ville selon le modèle ; rechercher individuellement les clés existantes, pas count() > 0.
- Insérer uniquement les villes manquantes et conserver les UUID des lignes déjà présentes.
- Garder la contrainte unique comme arbitre final ; pour un futur mécanisme PostgreSQL, envisager un INSERT ... ON CONFLICT sur la clé unique ou un traitement transactionnel approprié du conflit, sans modifier les données existantes.
- Tester une base vide, une base partielle, deux exécutions successives et des insertions concurrentes avant validation.

Aucun script d'initialisation, repository, service ou migration ajouté pour anticiper ce mécanisme. Aucun trajet, chauffeur ou véhicule créé.

## Tests, schéma et sécurité des données

Le modèle est testé sans repository, via EntityManager et transactions dans une base H2 en mémoire explicitement dédiée : jdbc:h2:mem:site_transport_tests. Le create-drop figure uniquement dans l'annotation de cette classe de test avec cette URL mémoire ; aucun profil de base persistante modifié. Les suppressions de lignes du test concernent uniquement cette base dédiée.

Les tests couvrent : normalisation des quatre villes, Unicode compatible et composé, espaces Unicode, valeurs absentes ou vides, borne 120/121, identité stable et distincte, persistance/relecture, quatre tentatives de doublons normalisés refusées par la contrainte unique et coexistence des quatre villes distinctes.

Avant modification : clean verify réussi, 1 test, 0 échec, 0 erreur, 0 ignoré, 32,881 s. Premier passage après ajout : 21 tests dont 4 échecs d'assertion, car H2 rend le nom de contrainte en majuscules. Le rejet SQL d'un doublon était correct. Une première correction du test a échoué à compiler : méthode hasMessageContainingIgnoringCase absente de la version AssertJ gérée (cannot find symbol). La comparaison a ensuite été faite via exception.getMessage() et containsIgnoringCase, sans modifier de dépendance ou de règle métier. Relance finale : clean verify réussi, 21 tests (14 modèle, 6 JPA, 1 contexte), 0 échec, 0 erreur, 0 ignoré, 30,123 s. Les violations SQL journalisées dans les quatre cas de doublons sont attendues et vérifiées par les tests.

Les annotations JPA préparent une nouvelle table, sa clé primaire UUID et son unicité. Aucun schéma PostgreSQL réel n'a été inspecté ni modifié ; aucune migration destructive, aucun Flyway ajouté. L'existence éventuelle d'une table ou de noms de contrainte concurrents sur une base persistante devra être vérifiée avant migration. PostgreSQL non testé ; les tests H2 ne prouvent pas les collations, le comportement du pilote PostgreSQL ou la normalisation des écritures SQL directes.

La configuration suivie de master reste un squelette sans profils explicites, avec H2 déclaré au runtime. Elle n'est pas présentée comme une configuration de production finalisée ; son organisation relève de l'issue technique existante, hors de cette tranche. Les avertissements de sécurité automatique et Open Session in View ne sont pas masqués.

## Répertoires sur GitHub

Inspection des arbres suivis de master et des branches des PR #7/#9 ainsi que de la branche #10 : aucun package vide décoratif ou fichier de maintien artificiel présent. Git ne suit pas les répertoires réellement vides ; il n'y a donc rien à supprimer sur GitHub dans ce périmètre. Le package de type de trajet de la PR #9 contient une vraie enum et son test : il est utile, conservé et hors périmètre. Aucun fichier d'une autre branche supprimé.

## Règles futures et arrêt

Le site ne confère aucune disponibilité de trajet. Toute création future de trajet exigera un chauffeur affecté dès sa création, distinct des passagers ; maximum quatre places passagers et prise en compte des attentes bloquantes. Le client pourra demander un départ PUBLIC ou PRIVE, sans créer de trajet fictif ou réservable sans chauffeur. Droits GENERAL et LOCAL inchangés.

Dépendances non implémentées : modèle Chauffeur, Véhicule, Trajet complet, relations au site, réservations, disponibilité, autorisations, initialisation de production et migration PostgreSQL. Seule prochaine tranche recommandée après validation : modèle minimal Chauffeur et identité, sans autres couches anticipées. Arrêt après PR brouillon de l'issue #10.
