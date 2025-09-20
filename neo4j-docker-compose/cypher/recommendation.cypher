MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
WITH u, COLLECT(DISTINCT n) AS neighborhoods
MATCH (u)-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
WHERE n IN neighborhoods
MATCH (l:Listing)-[:HAS]->(a:Amenity)
WITH l, COLLECT(DISTINCT a) AS userAmenities
MATCH (rec:Listing)-[:HAS]->(recAmenity:Amenity)
WHERE recAmenity IN userAmenities AND rec <> l
RETURN rec.listing_id, COUNT(DISTINCT recAmenity) AS score
ORDER BY score DESC
LIMIT 5


// //  Recommandation basée sur les quartiers similaires et les amenités communes
// //  Cette requête récupère les quartiers associés aux critiques écrites par l'utilisateur "2011754". Ensuite, elle trouve les annonces situées
// //  dans les mêmes quartiers et calcule un score basé sur le nombre d'amenités communes entre ces annonces et les critiques de l'utilisateur. 
// //  Les résultats sont ensuite triés par score décroissant et limités aux 5 premiers.
// MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WITH u, COLLECT(DISTINCT n) AS neighborhoods
// MATCH (u)-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WHERE n IN neighborhoods
// MATCH (l)-[:HAS]->(a:Amenity)
// WITH l, COLLECT(DISTINCT a) AS amenities, COUNT(DISTINCT a) AS score
// RETURN l.listing_id, score
// ORDER BY score DESC
// LIMIT 5


// //  Recommandation basée sur les aménités similaires
// //  Cette requête recherche les listings qui partagent des aménités similaires avec les listings notés par l'utilisateur "97763164". 
// //  Elle retourne les ID des listings et un score de similarité basé sur le nombre d'aménités communes, triés par ordre décroissant du score.
// MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WITH u, COLLECT(DISTINCT n) AS neighborhoods
// MATCH (u)-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WHERE n IN neighborhoods
// MATCH (l)-[:HAS]->(a:Amenity)
// WITH l, COLLECT(DISTINCT a) AS userAmenities
// MATCH (rec:Listing)-[:HAS]->(a:Amenity)
// WHERE rec <> l
// WITH rec, userAmenities, COLLECT(DISTINCT a) AS recAmenities
// RETURN rec.listing_id, SIZE([x IN userAmenities WHERE x IN recAmenities]) AS similarityScore
// ORDER BY similarityScore DESC
// LIMIT 5


// //  Recommandation basée sur les aménités similaires 
// MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WITH u, COLLECT(DISTINCT n) AS neighborhoods
// MATCH (u)-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WHERE n IN neighborhoods
// MATCH (l:Listing)-[:HAS]->(a:Amenity)
// WITH l, COLLECT(DISTINCT a) AS userAmenities
// MATCH (rec:Listing)-[:HAS]->(recAmenity:Amenity)
// WHERE recAmenity IN userAmenities AND rec <> l
// RETURN rec.listing_id, COUNT(DISTINCT recAmenity) AS score
// ORDER BY score DESC
// LIMIT 5


// //  Recommandation basée sur les hôtes similaires
// MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WITH u, COLLECT(DISTINCT n) AS neighborhoods
// MATCH (u)-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
// WHERE n IN neighborhoods
// MATCH (l:Listing)<-[:HOSTED_BY]-(h:Host)-[:HOSTED_BY]->(rec:Listing)
// WHERE h <> u
// RETURN rec.listing_id
// LIMIT 5
