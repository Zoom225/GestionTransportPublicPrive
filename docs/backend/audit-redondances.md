# Audit des redondances — Retrait des packages métier vides

## Traçabilité et périmètre

Issue #6, branche `feature/backend-06-retrait-packages-vides` depuis `origin/master` au commit `0584785`. Autorisation explicite de retirer uniquement 36 fichiers locaux non suivis. Aucun développement métier, aucun ajout de couche, aucune fusion.

La PR #5 de l'audit #4 reste distincte et en brouillon. Elle ajoute également un fichier audit-redondances.md : les deux comptes rendus devront être réunis lors de leur intégration après revue ; aucune fusion n'est effectuée ici. Le présent document n'applique ni les modifications POM ni les corrections documentaires de cette autre PR.

La suppression effective concerne le dossier principal de travail, resté sur la branche locale `feature/backend-02-architecture`. La branche dédiée de l'issue #6, dans un worktree isolé, publie uniquement les preuves documentaires : les 36 fichiers n'ont jamais été suivis, leur retrait ne peut donc pas figurer comme suppression dans un diff Git. Une extraction neuve de master ne les contient déjà pas.

## Preuve d'inutilité

Les 36 chemins ont été générés depuis une liste fermée de six modules et six sous-packages, sans recherche destructive globale. Pour chaque fichier, contrôle exact du contenu : un commentaire « Couche … du module … de Gestion de Transport. » et une déclaration package. Aucune annotation, aucune classe, aucune méthode, aucune documentation spécifique nécessaire. Vérification de l'absence de suivi Git, contrôle SHA-256 avant retrait, validation de chaque chemin absolu dans le répertoire autorisé.

Classe A : retrait sûr de structures prématurées. Aucun dossier supprimé, aucun git clean, aucune suppression récursive. Les éventuels répertoires vides peuvent rester visibles sur le disque mais ne font pas partie du code compilé.

## Sauvegarde et préservation

Copie hors src et hors dépôt dans le dossier temporaire local ci-dessous ; ce dossier est soumis à la politique de nettoyage des fichiers temporaires de la machine. Conserver la sauvegarde tant que la validation est en attente.

Sauvegarde : `C:\Users\user\AppData\Local\Temp\GestionTransport-sauvegarde-packages-20261111-143319-631`.

Le fichier manifest.json de la sauvegarde contient les 36 chemins absolus, chemins de copie et SHA-256. Chaque copie a été comparée à son original avant suppression. Le fichier preserved.json contient les empreintes des 25 autres fichiers suivis et non suivis : toutes sont identiques après retrait.

Sont conservés notamment le fichier configuration/package-info.java, les configurations et profils, la sécurité, les erreurs REST, les tests, les documents locaux non suivis et les modifications locales du POM. Aucun secret ou donnée applicative copié dans cet audit.

## Registre exact des 36 retraits

Pour chaque ligne : fichier non suivi, commentaire générique et déclaration seuls, sans annotation ; sauvegardé puis supprimé uniquement par son chemin explicite.

| Chemin relatif au dépôt | SHA-256 avant retrait |
|---|---|
| `src/main/java/com/gestion/GestionTransport/agence/controller/package-info.java` | `1A8C1803C24B1CF5459D49981926ED56FE94736371A9842CAABC339141638AF4` |
| `src/main/java/com/gestion/GestionTransport/agence/service/package-info.java` | `A0C9AF17CAA45007423A133A8EA4C136C6CE1D50B169C0EEA7E45CED9E26953C` |
| `src/main/java/com/gestion/GestionTransport/agence/repository/package-info.java` | `20494CEE12180395F0CFF6AF19FA5A59BDF1EF6CF6A65DC6E8CD8C8B80A602E2` |
| `src/main/java/com/gestion/GestionTransport/agence/dto/package-info.java` | `D108007376CFD345D89556821EAFF27A6E6AF5C9D794F9A7A7E7D7265BB0F213` |
| `src/main/java/com/gestion/GestionTransport/agence/mapper/package-info.java` | `EC27CBB0212CEC68E727DBFA69D58F01BD59E7F9401B104B9918C4EA4D7C319D` |
| `src/main/java/com/gestion/GestionTransport/agence/entity/package-info.java` | `3E0A8ED43743516F3C2D1907DD453620EDF3A55D4D2256F45DF7EEE1FC6CBDE0` |
| `src/main/java/com/gestion/GestionTransport/client/controller/package-info.java` | `54DF4F091F9F48717FDE73B45646B3765781AAB7CFDCE4103AB35C63198F698D` |
| `src/main/java/com/gestion/GestionTransport/client/service/package-info.java` | `F7BC2E6048AFF40DD82250E101A945E069B0FCC8F5762FB9FAEE33B8A20C94ED` |
| `src/main/java/com/gestion/GestionTransport/client/repository/package-info.java` | `1146D1B7C2DB5211D28B0E6083056A920F04E0EF116421BDC318E41F7567DFA1` |
| `src/main/java/com/gestion/GestionTransport/client/dto/package-info.java` | `3B439261DA3A452C0AEBE8630F7810AD0B1038DCC004B7FE47E4DF0398B3409A` |
| `src/main/java/com/gestion/GestionTransport/client/mapper/package-info.java` | `B1B0E34D4676A430A32BEBE224D9F61B9B75E0D31F9CDDBADBBF46A55443846D` |
| `src/main/java/com/gestion/GestionTransport/client/entity/package-info.java` | `B21C2195CB49614BA971B62C8FE22C5491B161BCA5A0E864AC7FE362862701D3` |
| `src/main/java/com/gestion/GestionTransport/chauffeur/controller/package-info.java` | `64D28C0BF1B4F91FCCA8024C3038771A9A0FE784253A3670E8255A8DF292102B` |
| `src/main/java/com/gestion/GestionTransport/chauffeur/service/package-info.java` | `9D3F1F3E3CCEE785EEEC2A52101E3054CC15B04C9CA0997F19CE41FEC8779053` |
| `src/main/java/com/gestion/GestionTransport/chauffeur/repository/package-info.java` | `EED7F81361522892D93CEBF15FC9B8D6D24207EAC68C5B5029D4E468162434BC` |
| `src/main/java/com/gestion/GestionTransport/chauffeur/dto/package-info.java` | `90A9905365F30C50DB78776D1B57CBBE322AC82A33013AA08F46D54F009FF106` |
| `src/main/java/com/gestion/GestionTransport/chauffeur/mapper/package-info.java` | `152C0C7384CB2D055798B863D6B0E37FFF7D08CFE08BB24E5FDFF80922CBBECD` |
| `src/main/java/com/gestion/GestionTransport/chauffeur/entity/package-info.java` | `4B5303FFE8AA92EFB1D1F5A07F8CBCC204134E0526AAB1C89FFB969FA4122AB9` |
| `src/main/java/com/gestion/GestionTransport/vehicule/controller/package-info.java` | `34A66A5BDC845945AA9C1E978920EDB9BE5099C050747AB6949FA9958303C7B6` |
| `src/main/java/com/gestion/GestionTransport/vehicule/service/package-info.java` | `B693702202BEF958BD3CA1BC8606B359323F5B666ED455F63632E5FB0612B5CC` |
| `src/main/java/com/gestion/GestionTransport/vehicule/repository/package-info.java` | `973F5D0852B5E940ADF88C779E387857308054FC3C1E657F643DDD553E475602` |
| `src/main/java/com/gestion/GestionTransport/vehicule/dto/package-info.java` | `EBBF835DB087854AB7C3812F438DE5B6A7EBE6293E865CB8048F3C5E6191696A` |
| `src/main/java/com/gestion/GestionTransport/vehicule/mapper/package-info.java` | `52D27391F6278D7C4307D1A8B3DCA1F1675BF44184964BF271261AD76FA048FB` |
| `src/main/java/com/gestion/GestionTransport/vehicule/entity/package-info.java` | `80E621CEBAD8F161E4C6ACCD2C7B922F948231030DF3DB0E5C0D11912E5EBFAA` |
| `src/main/java/com/gestion/GestionTransport/trajet/controller/package-info.java` | `6C8B6BE5235E0D664DDDCD8C00D73809B4A2DD3FB64EE9B8B123D3E4FF7557A1` |
| `src/main/java/com/gestion/GestionTransport/trajet/service/package-info.java` | `4B4BC224165A8A23F07D0F4A3C3F01CBB4FA0FF6458E7DB774BC76B4A54573B8` |
| `src/main/java/com/gestion/GestionTransport/trajet/repository/package-info.java` | `B44CEFB1CC133A9667D8CC4EB27FCF49E8CABC188D06A5131994B0BE6CC45FEC` |
| `src/main/java/com/gestion/GestionTransport/trajet/dto/package-info.java` | `2E0B9D8EFAEA4DBF5E517A2C8F308C6E813B77963DA8C7D2B46E6C5A7FD2DC87` |
| `src/main/java/com/gestion/GestionTransport/trajet/mapper/package-info.java` | `9B7F4904B0634470B0B71FD7BB5AB4DE002E38B197936196E1520B3AEBC1BE4D` |
| `src/main/java/com/gestion/GestionTransport/trajet/entity/package-info.java` | `AC3E85136219BBB04D8CB328F33930C830AE4839C167C1213A1856A35B2E8B88` |
| `src/main/java/com/gestion/GestionTransport/reservation/controller/package-info.java` | `DB3E9FD6996D29F1CED5A86C56E7A1FB09B77B4C48C32415638C688D052942F8` |
| `src/main/java/com/gestion/GestionTransport/reservation/service/package-info.java` | `213D2635B87C1F70E7F1C2C64FC7AE7AA93645E78AD1A0E3F30FCA590BDC1D47` |
| `src/main/java/com/gestion/GestionTransport/reservation/repository/package-info.java` | `EBF39FFDD6032F722022B06FB1E6367DD9D678AD67342EF6AB6C960DACD8EDF7` |
| `src/main/java/com/gestion/GestionTransport/reservation/dto/package-info.java` | `7E039269916724468B17C72294E3213AD9B804AC2E9BF090A7E9E9E0113E14BC` |
| `src/main/java/com/gestion/GestionTransport/reservation/mapper/package-info.java` | `3E7A377353D0C017C6B3FE12B9D123BD504DC4B04577390810F6E3612BD72C36` |
| `src/main/java/com/gestion/GestionTransport/reservation/entity/package-info.java` | `147233A171C62DAC634B1DB1DAA4674796A83D3C49A9094758C28A5656399F68` |

## Vérifications

- Projet local après les 36 retraits : .\mvnw.cmd clean verify — BUILD SUCCESS, 10 tests, 0 échec, 0 erreur, 0 ignoré, 50,807 s. Java 21.0.9, Spring Boot 4.1.2.
- Les 36 chemins sont absents ; les 36 sauvegardes ont été vérifiées et les 25 autres fichiers inventoriés sont inchangés.
- Branche documentaire isolée : clean verify — BUILD SUCCESS, 1 test, 0 échec, 0 erreur, 0 ignoré, 38,661 s. Ce test distinct ne constitue pas une preuve de suppression locale : celle-ci est fournie par le manifeste et les 10 tests du dossier principal.
- H2 en mémoire testé ; PostgreSQL non testé. Aucun changement de dépendance ou de configuration dans cette issue.
- Contrôle du diff documentaire et de la terminologie avant commit. Aucun Java suivi, frontend, schéma SQL ou secret publié.

## Règle de reprise

Une seule fonctionnalité métier à la fois. Créer uniquement les classes et couches requises par le besoin validé ; aucun DTO, mapper, service ou repository systématique. Préserver GENERAL et LOCAL, le chauffeur obligatoire dès la création et distinct des quatre passagers maximum ; compter toutes les réservations occupant une place, y compris les attentes bloquantes.

Arrêt après la PR brouillon ; attendre la validation avant toute reprise. Aucun début de fonctionnalité dans cette issue.
