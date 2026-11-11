# SiteTransport Service — Étape 14

## Périmètre et base

Issue existante #14 : https://github.com/Zoom225/GestionTransportPublicPrive/issues/14. Branche existante feature/backend-14-site-transport-service, créée à distance sur la base feature/backend-12-site-transport-repository au commit `84275fe9dc98a08502622afd253caed7f90060c5`. PR empilée vers cette branche Repository, sans fusion ni création d'issue supplémentaire.

Le modèle et le Repository ont été lus avant implémentation : UUID attribué au constructeur, nomVille normalisé par le modèle, contrainte unique uk_site_transport_nom_ville et méthode de recherche exacte findByNomVille. Aucun de ces fichiers n'est modifié. Aucun POM, profil, schéma SQL, donnée, frontend ou autre module métier modifié. Travail dans un worktree isolé ; les 25 fichiers locaux suivis et non suivis du dossier principal sont préservés avec contrôle des empreintes SHA-256.

## Erreurs et configurations réellement disponibles

La branche de base ne contient aucune exception métier personnalisée ni gestion centralisée d'erreurs REST. Sa configuration suivie se limite au nom d'application. Les fichiers locaux de configuration, sécurité et erreurs du dossier principal appartiennent au travail non publié de l'étape technique : ils ont été examinés mais ne sont ni importés ni modifiés. Le gestionnaire REST local prépare des erreurs techniques génériques, pas des erreurs métier de site.

Une seule classe SiteTransportException est donc introduite dans le module courant, avec deux codes stables : SITE_TRANSPORT_INTROUVABLE et SITE_TRANSPORT_DEJA_EXISTANT, et des messages français. Aucun statut HTTP ni nouveau gestionnaire REST n'est ajouté à cette couche. La validation du nom continue à utiliser l'IllegalArgumentException du modèle, sans validation dupliquée ni multiplication de classes d'erreur. UUID null est refusé par IllegalArgumentException avant accès au Repository.

Les causes techniques sont conservées pour le diagnostic, mais ne constituent pas une réponse publique : une future couche HTTP devra exposer uniquement le code et le message public appropriés, jamais les détails SQL ou stack traces. Aucun endpoint ni contrat HTTP n'est livré dans #14.

## Service effectivement livré

Une seule classe concrète SiteTransportService, sans interface décorative, injectée par constructeur :

| Méthode | Comportement |
|---|---|
| lister() | Retourne les sites existants via findAll ; liste vide si aucun, sans ordre métier ou pagination inventé |
| rechercher(UUID) | Retourne le site, refuse null et signale explicitement un UUID absent |
| creer(String) | Construit le vrai modèle, réutilise son nom canonique, détecte le doublon et tente une insertion avec contrôle SQL final |

La classe est transactionnelle en lecture seule pour les consultations ; creer ouvre une transaction d'écriture. Aucun DTO, Mapper, Controller, initialiseur ni autre méthode métier. Aucun renommage ou suppression de site ajouté.

## Anti-doublons et transaction

1. Construire SiteTransport avec le nom demandé : c'est le modèle, et lui seul, qui valide et normalise. Aucune normalisation ni expression régulière recopiée dans le Service.
2. Rechercher le nom canonique déjà porté par cet objet. Si présent, lever immédiatement SITE_TRANSPORT_DEJA_EXISTANT sans sauvegarder.
3. Sinon utiliser saveAndFlush : le flush force l'exécution SQL avant la sortie de la méthode, pour pouvoir traiter la contrainte unique dans le Service.
4. Si une autre transaction a créé la même ville entre lecture et insertion, reconnaître la violation d'unicité SQLState 23505 et la contrainte nommée uk_site_transport_nom_ville dans la chaîne Hibernate. La comparaison du nom tolère casse et qualification du moteur ; aucun parsing du message utilisateur.
5. Traduire uniquement ce conflit en SiteTransportException avec le même code métier que le précontrôle ; les autres erreurs d'intégrité, autres contraintes et erreurs SQL inconnues sont propagées telles quelles, sans être présentées comme un doublon.

La contrainte de base est la protection finale : le précontrôle seul ne garantit rien sous concurrence. Après une violation SQL, aucune lecture ni nouvelle écriture n'est tentée dans la transaction compromise. L'exception métier est une RuntimeException qui remonte hors de la méthode transactionnelle et déclenche le rollback. Un appelant qui participe à une transaction englobante doit laisser cette transaction se terminer en échec, pas attraper l'exception pour continuer à écrire dans la même transaction.

Cette stratégie repose sur une contrainte unique immédiate portant le nom défini par le mapping ; une future migration PostgreSQL devra préserver ce nom et vérifier la traduction Hibernate/pilote. Les erreurs non reconnues ne sont pas masquées. Aucune transaction REQUIRES_NEW ou logique de reprise automatique n'est ajoutée au Service.

## Tests et preuves

SiteTransportServiceTests : 18 tests unitaires Mockito couvrant liste pleine/vide, recherche présente/absente, UUID null, validation du nom vide/null/trop long sans Repository, nom normalisé issu du modèle, doublon préalable sans sauvegarde, conflit SQL après lecture négative, cause conservée, autres contraintes, autre SQLState et intégrité non identifiée.

SiteTransportServiceJpaTests : trois tests d'intégration avec DataJpaTest et import du Service, sur H2 mémoire automatiquement isolée. Aucun bloc de configuration JPA recopié. L'annotation NOT_SUPPORTED sur le test laisse les méthodes du Service définir et valider leurs propres transactions. Le nettoyage de fixtures via deleteAllInBatch ne concerne que cette base mémoire de test.

- Création puis recherche et liste après validation des transactions réelles.
- Refus d'un doublon existant, conservation du site initial et création ultérieure indépendante réussie.
- Deux appels concurrents depuis deux threads coordonnés au départ : un succès, un conflit métier, une seule ligne finale. Les threads sont arrêtés après le test. Une création indépendante réussit ensuite, ce qui vérifie l'absence de réutilisation d'une transaction compromise.

Le test concurrent ne fige pas l'ordonnancement des deux précontrôles : selon l'exécution, le refus peut venir du précontrôle ou de la base. Le cas lecture négative puis violation SQL est explicitement forcé dans le test unitaire ; lors de la vérification complète présente, le journal confirme aussi une vraie violation H2 23505 pour la ville concurrente et le test réussit avec traduction métier.

Résultats Windows PowerShell, Java 21.0.9 / Spring Boot 4.1.2 :

- Tests unitaires ciblés : BUILD SUCCESS, 18 tests, 0 échec, 0 erreur, 0 ignoré, 17,301 s.
- .\mvnw.cmd clean verify : BUILD SUCCESS, 48 tests, 0 échec, 0 erreur, 0 ignoré, 36,533 s ; inclut les 27 tests hérités du modèle/Repository et 21 nouveaux tests du Service.
- git diff --check et périmètre exact vérifiés avant publication.

Les violations SQL attendues dans les tests de doublons ne sont pas des échecs Maven. PostgreSQL non testé : aucune garantie d'intégration pilote ou de collation PostgreSQL n'est revendiquée. Aucun test sur une base persistante, aucune migration ni create-drop supplémentaire sur une telle base.

## Limites et arrêt

La normalisation et ses limites restent celles du modèle : écritures SQL directes à encadrer, pas de gestion d'alias ou d'homonymes internationaux. Le Service n'est pas encore exposé en HTTP et ne définit pas de droits administratifs ; GENERAL et LOCAL restent les rôles du cadrage, sans mécanisme d'autorisation nouveau ici.

Les branches modèle et Repository doivent encore être revues et intégrées selon leur chaîne de dépendances. Les audits parallèles ne sont pas modifiés. La PR de l issue #14 doit cibler la branche Repository, contenir Closes #14 et rester en brouillon, sans fusion.

Seule prochaine couche proposée après validation : Controller SiteTransport minimal et contrat HTTP, avec mapping des erreurs et contrôles de sécurité explicitement validés ; aucun début de cette couche, DTO, Mapper ou initialiseur dans cette issue. Une seule couche à la fois, arrêt après publication pour validation.
