# Modèle métier initial — Gestion de Transport

Ce document est un **cadrage**, non une implémentation définitive.

| Concept | Responsabilité principale |
|---|---|
| Agence | Regrouper les moyens et la gestion locale |
| Administrateur | Administrer selon le périmètre GLOBAL ou LOCAL |
| Chauffeur | Conduire un trajet auquel il est affecté |
| Véhicule | Moyen de transport affecté à un trajet |
| Client | Réserver une place selon sa catégorie |
| Trajet | Départ, arrivée, horaire, type PUBLIC/PRIVÉ, chauffeur et véhicule |
| Réservation | Occuper une des quatre places passagers |
| Paiement | Enregistrer le règlement d'une réservation, selon les règles validées |
| Pénalité | Éventuelle conséquence métier, à définir avant implémentation |

## Invariants techniques envisagés
- Un trajet ouvert possède un chauffeur non nul et valide.
- Une réservation passager ne peut pas désigner le chauffeur du même trajet.
- Nombre de places passagers confirmées ≤ 4.
- Contrôle transactionnel et verrouillage adaptés pour éviter la surréservation concurrente.
- Les permissions LOCAL sont bornées au périmètre attribué.

## Points de conception à arbitrer
- Le conducteur est-il toujours lié à une agence ?
- Le véhicule peut-il être remplacé après ouverture du trajet ?
- Qui peut créer, annuler ou modifier un trajet ?
- Une place en attente de paiement bloque-t-elle la capacité ?
- Quelles conditions déclenchent une pénalité ?
