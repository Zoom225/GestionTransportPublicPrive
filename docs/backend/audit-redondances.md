# Audit des redondances — Gestion de Transport

## Base et protection du travail local

Issue #4 : https://github.com/Zoom225/GestionTransportPublicPrive/issues/4. Branche `feature/backend-04-nettoyage-redondances`, créée depuis `origin/master` au commit `0584785` après fetch. PR #2 fusionnée, issue #1 terminée, aucune PR ouverte lors de l'audit. Le numéro 2 désigne une PR ; l'architecture technique est suivie par l'issue #3, laissée ouverte et distincte du nettoyage.

Le dossier principal reste sur `feature/backend-02-architecture` avec ses modifications suivies et non suivies. Aucun reset, stash, nettoyage ou suppression de ces fichiers. Travail effectué dans un worktree séparé ; empreintes SHA-256 du travail local contrôlées avant publication. Les 36 fichiers package-info.java préparés localement pour les six modules ne sont pas sur master : ils sont préservés, non importés et non supprimés. Leur création systématique est une structure prématurée à réexaminer avec accord dans l'issue #3.

## Inventaire réellement inspecté

Les 12 fichiers suivis de la base ont été lus intégralement :

- `pom.xml` : Java 21, Boot 4.1.2, JPA, sécurité, validation, MVC, pilotes H2/PostgreSQL, Lombok, quatre starters de tests et plugins Maven.
- `.gitignore`, `.gitattributes` : exclusions des sorties de compilation/IDE, règles de fins de ligne.
- `.mvn/wrapper/maven-wrapper.properties`, `mvnw`, `mvnw.cmd` : wrapper 3.3.4, Maven 3.10.0 ; scripts de lancement pour deux plateformes différentes.
- `src/main/resources/application.properties` : uniquement le nom d'application. Aucun profil suivi.
- `src/main/java/com/gestion/GestionTransport/GestionTransportApplication.java` : seul point d'entrée Spring Boot.
- `src/test/java/com/gestion/GestionTransport/GestionTransportApplicationTests.java` : seul test, chargement du contexte.
- `docs/backend/cadrage-gestion-transport.md`, `docs/backend/modele-metier.md`, `docs/backend/feuille-de-route.md` : invariants, modèle proposé et plan de travail.

Aucune entité, DTO, repository, service, contrôleur, exception personnalisée ou migration SQL sur cette base. Aucun fichier généré suivi, aucune méthode dupliquée, aucun package vide suivi détecté. Aucun ancien nom métier détecté dans les sources et documents suivis. Les couches proposées ont des responsabilités différentes, elles ne sont pas qualifiées de doublons.

## Classement et preuves

A = suppression/correction sûre appliquée ; B = refactoring ultérieur avec tests ; C = conserver ; D = accord requis. Les recherches portent sur les fichiers suivis, pas sur les secrets ou les fichiers locaux à préserver.

| Chemin exact | Classe | Problème | Preuve | Risque | Correction proposée / effectuée | Test à exécuter |
|---|---|---|---|---|---|---|
| `pom.xml` | A | Métadonnées vides sans information | name, description, url, licenses, developers et scm ne contiennent que des éléments vides ; aucune référence à project.name/description/url/licenses/developers/scm dans les fichiers suivis | Faible : affichage des métadonnées Maven ; aucun changement de dépendance, plugin ou coordonnée | Retrait de ces seuls éléments vides, sans inventer de licence ni identité | clean verify et revue du diff POM |
| `docs/backend/cadrage-gestion-transport.md` | A | Statut PR #2 périmé | Documentation disant brouillon sans fusion, API GitHub merged=true | Confusion de traçabilité | Remplacer le statut par fusionnée, issue #1 clôturée | Comparer à GitHub et diff --check |
| `docs/backend/feuille-de-route.md` | A | Même statut historique périmé | Deux mentions de statut de PR incompatibles avec la fusion constatée | Confusion sur la reprise | Actualiser ces mentions, conserver la règle des futures PR brouillon | Relecture et diff --check |
| `pom.xml` | B | Configuration du processeur répétée dans deux exécutions | Deux blocs annotationProcessorPaths identiques pour compile/testCompile | Une factorisation peut changer l'héritage ou le traitement des annotations | Conserver ici ; comparer les POM effectifs et compiler les deux phases avant une future factorisation | effective-pom, clean verify |
| `pom.xml` | D | Lombok déclaré mais sans usage actuel | Recherche lombok : uniquement dépendance et chemins de processeur dans le POM ; deux classes sans annotations Lombok | Retrait simultané de dépendance/processeur à décider selon conventions du TFE | Aucun retrait dans cette issue ; décider sans anticiper des entités | Compilation principale et tests, dependency:tree |
| `pom.xml` | D | Console H2 déclarée sans activation explicite | spring-boot-h2console présent ; application.properties ne configure pas de console | Auto-configuration et politique des profils à analyser | Décider avec les profils dans l'issue #3, ne pas déclarer innocuité par simple absence d'import Java | Tests de contexte et sécurité par profil |
| `pom.xml` | C | H2/PostgreSQL et starters peuvent sembler inutilisés | Le test démarre sur H2 ; PostgreSQL et les starters appartiennent au socle prévu | Supprimer les starters affecterait l'auto-configuration ; H2 seul ne valide pas PostgreSQL | Conserver, revoir H2 en portée test dans l'issue de configuration | Contexte isolé, futur test PostgreSQL |
| `src/main/resources/application.properties` | B | Absence de profils et Open Session in View par défaut | Un seul nom d'application ; avertissement Open Session in View dans Maven | Configuration non prête pour une base persistante | Traiter séparément dans l'issue #3, aucune configuration regroupée sans doublon démontré | Tests dev/test/prod sans données réelles |
| `src/main/java/com/gestion/GestionTransport/GestionTransportApplication.java` | C | Aucun doublon ni code mort démontré | Classe @SpringBootApplication et méthode main utilisées au démarrage | Suppression casse le démarrage | Conserver | contextLoads et packaging |
| `src/test/java/com/gestion/GestionTransport/GestionTransportApplicationTests.java` | C | Test minimal mais utile | Un seul contextLoads, exécuté avec succès | Suppression retire la seule preuve de démarrage | Conserver ; enrichissement dans une autre issue | clean verify |
| `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties` | C | Scripts similaires mais plateformes différentes | Lancement shell et Windows ; version commune du wrapper | Retrait empêche le lancement sur une plateforme | Conserver les scripts officiels et leurs en-têtes de licence | mvnw.cmd -v ; verify Windows exécuté, shell non testé |
| `.gitignore`, `.gitattributes` | C | Aucun doublon nuisible prouvé | Exclusions IDE/sorties et règles de fins de ligne distinctes | Suppression peut faire suivre des fichiers générés | Conserver | git ls-files et diff --check |
| `docs/backend/modele-metier.md` | C | Rappels des invariants aussi présents dans le cadrage | Documents avec objectifs différents : exigences et modèle | Une déduplication stricte rendrait le modèle incomplet | Conserver, pas de changement de règle | Relecture croisée chauffeur/capacité |
| `docs/backend/feuille-de-route.md` | D | Préparation de toutes les couches trop large pour la nouvelle priorité | Lot 2.3 envisage les six modules avant les besoins métier | Structure prématurée | Le plan incrémental ci-dessous prime pour la reprise ; revoir l'issue #3 avant implémentation | Revue utilisateur, aucune nouvelle couche dans #4 |

## Nettoyage effectué et limites

Aucun fichier supprimé. Suppression uniquement de balises vides dans le POM, après recherche de références et examen des plugins ; correction de deux documents obsolètes. Aucun code Java, profil, dépendance, frontend, SQL, secret ou donnée changé dans cette branche. L'audit est ajouté comme quatrième fichier du diff. Aucune nouvelle couche métier créée.

Les règles restent : GENERAL et LOCAL ; chauffeur obligatoire dès la création, distinct des quatre passagers maximum ; attentes bloquantes incluses dans les quatre places ; annulation et expiration libératrices. Elles sont documentées, pas encore implémentées sur master.

## Vérifications

Environnement : Windows PowerShell, Java 21.0.9, Maven 3.10.0, Boot 4.1.2.

- État initial du dossier principal avec travail local : clean verify réussi, 10 tests, aucun échec/erreur/ignoré ; ce résultat comprend des changements non publiés de l'issue #3.
- Base isolée origin/master avant nettoyage : clean verify réussi, 1 test, aucun échec/erreur/ignoré, 26,022 s.
- Après nettoyage : clean verify réussi, 1 test, aucun échec/erreur/ignoré, 25,711 s. diff --check réussi ; quatre fichiers uniquement dans le diff. Un premier contrôle a signalé des fins de ligne CRLF comme espaces de fin ; normalisation des seuls fichiers modifiés en LF, puis contrôle réussi.
- Tests de contexte sur H2 en mémoire. PostgreSQL non testé. Avertissements existants : Open Session in View actif, sécurité automatique de développement et chargement dynamique de l'agent Mockito. Aucun de ces sujets n'est masqué par le nettoyage.
- Aucun secret en dur dans les fichiers ajoutés ou modifiés ; les journaux Maven restent hors Git, aucun mot de passe généré recopié.

## Reprise incrémentale proposée, sans implémentation

Chaque ligne suivra le même cycle : issue dédiée ou réutilisée après vérification, branche depuis master à jour, implémentation minimale, tests, commits liés à l'issue, PR brouillon, revue, fusion uniquement après accord. Les numéros futurs ne sont pas inventés ; une seule étape active à la fois.

| Ordre | Issue / branche future | Minimum et tests attendus |
|---|---|---|
| 1 | Réexaminer #3 / feature/backend-02-architecture | Configuration et profils uniquement, réduire le périmètre avant reprise ; contexte isolé, PostgreSQL si disponible |
| 2 | Nouvelle issue agences / branche dédiée | Agence minimale et tests validation/persistance |
| 3 | Nouvelle issue administrateurs / branche dédiée | GENERAL et LOCAL, périmètres validés et tests de refus |
| 4 | Nouvelle issue clients / branche dédiée | Identité et catégories validées, tests de validation |
| 5 | Nouvelle issue chauffeurs / branche dédiée | Identité/disponibilité minimales et tests |
| 6 | Nouvelle issue véhicules / branche dédiée | Affectation et capacité, tests de cohérence |
| 7 | Nouvelle issue trajets / branche dédiée | PUBLIC/PRIVE, chauffeur dès création, tests d'identité et d'affectation |
| 8 | Nouvelle issue réservations / branche dédiée | Quatre places incluant attentes bloquantes, libération, tests concurrents PostgreSQL |
| 9 | Nouvelle issue paiements et pénalités / branche dédiée | Règles préalablement validées, tests montants/transitions |
| 10 | Nouvelle issue JWT et autorisations / branche dédiée | Authentification complète, tests 401/403 et périmètres ; ne jamais ouvrir la production en attendant |
| 11 | Nouvelle issue intégration, CI et documentation / branche dédiée | Scénarios complets et preuves ; les tests de chaque étape restent obligatoires dès son développement |

Seule prochaine étape recommandée après validation du nettoyage : recadrer l'issue #3 sur la configuration et les profils. Aucun démarrage de cette étape dans l'issue #4. Arrêt après publication de la PR brouillon de nettoyage.