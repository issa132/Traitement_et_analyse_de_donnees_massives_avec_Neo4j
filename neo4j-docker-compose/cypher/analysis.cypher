MATCH (l:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
WITH n.neighborhood_id AS neighborhood, COALESCE(AVG(l.review_scores_rating), 0) AS average_rating
WHERE average_rating > 0
RETURN neighborhood, average_rating
ORDER BY average_rating DESC
LIMIT 10


MATCH (u:User {user_id: "97763164"})
RETURN count(*) AS user_data_count


MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
RETURN n.neighborhood_id, COUNT(*) AS visit_count
ORDER BY visit_count DESC


MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:HAS]->(a:Amenity)
RETURN a.name, COUNT(*) AS amenity_count
ORDER BY amenity_count DESC
LIMIT 10


MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
WITH u, COLLECT(DISTINCT n) AS neighborhoods
MATCH (u2:User)-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n2:Neighborhood)
WHERE n2 IN neighborhoods AND u2 <> u
RETURN DISTINCT u2.user_id
LIMIT 10


MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:IN_NEIGHBORHOOD]->(:Neighborhood)
WITH u, COLLECT(DISTINCT l) AS user_listings
MATCH (u)-[:WROTE]->(:Review)-[:REVIEWS]->(l2:Listing)-[:IN_NEIGHBORHOOD]->(:Neighborhood)
WHERE l2 IN user_listings
MATCH (l2)-[:HAS]->(a:Amenity)
RETURN DISTINCT a.name AS amenity
LIMIT 10


MATCH (u:User)-[:WROTE]->(r:Review)-[:REVIEWS]->(l:Listing)
WITH u.user_id AS user_id, COUNT(r) AS reviewCount
RETURN user_id, reviewCount
ORDER BY reviewCount DESC
LIMIT 10
