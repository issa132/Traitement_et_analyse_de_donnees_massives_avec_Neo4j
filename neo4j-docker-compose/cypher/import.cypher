// # # # # #
// indexes
// # # # # #
CREATE INDEX FOR (h:Host) ON h.host_id;
CREATE INDEX FOR (l:Listing) ON l.listing_id;
CREATE INDEX FOR (n:Neighborhood) ON n.neighborhood_id;

CREATE INDEX FOR (u:User) ON u.user_id;
CREATE INDEX FOR (r:Review) ON r.review_id;

// # # # # # # #
// constraints
// # # # # # # #
CREATE CONSTRAINT FOR (h:Host) REQUIRE h.host_id IS UNIQUE;
CREATE CONSTRAINT FOR (a:Amenity) REQUIRE a.name IS UNIQUE;
CREATE CONSTRAINT FOR (l:Listing) REQUIRE l.lising_id IS UNIQUE;

CREATE CONSTRAINT FOR (u:User) REQUIRE u.user_id IS UNIQUE;
CREATE CONSTRAINT FOR (r:Review) REQUIRE r.review_id IS UNIQUE;

// # # # # # # # # #
// import listings
// # # # # # # # # #

LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.id IS  NOT NULL
MERGE (l:Listing {listing_id: row.id})
ON CREATE SET 
    l.name                        = row.name,
    l.latitude                    = toFloat(row.latitude),
    l.longitude                   = toFloat(row.longitude),
    l.neighborhood_cleansed       = row.neighborhood_cleansed,
    l.reviews_per_month           = toFloat(row.reviews_per_month),
    l.instant_bookable            = CASE WHEN row.instant_bookable = "t" THEN true ELSE false END,
    l.review_scores_value         = toFloat(row.review_scores_value),
    l.review_scores_location      = toFloat(row.review_scores_location),
    l.review_scores_communication = toFloat(row.review_scores_communication),
    l.review_scores_checkin       = toFloat(row.review_scores_checking),
    l.review_scores_cleanliness   = toFloat(row.review_scores_cleanliness),
    l.review_scores_rating        = toFloat(row.review_scores_rating),
    l.availability_365            = toInteger(row.availability_365),
    l.availability_90             = toInteger(row.availability_90),
    l.availability_60             = toInteger(row.availability_60),
    l.availability_30             = toInteger(row.availability_30),
    l.price                       = toInteger(substring(row.price, 1)),
    l.beds                        = toInteger(row.beds),
    l.bedrooms                    = toInteger(row.bedrooms),
    l.bathrooms                   = toInteger(row.bathrooms),
    l.accommodates                = toInteger(row.accommodates),
    l.room_type                   = row.room_type,
    l.property_type               = row.property_type;


LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.id IS  NOT NULL
MATCH (l:Listing {listing_id: row.id})
MERGE (n:Neighborhood {neighborhood_id: COALESCE(row.neighbourhood_cleansed, "NA")})
SET n.name = row.neighbourhood
MERGE (l)-[:IN_NEIGHBORHOOD]->(n);


LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.id IS  NOT NULL
MATCH (l:Listing {listing_id: row.id})
UNWIND split(row.amenities,'|') as amenity
MERGE (a:Amenity {name: trim(amenity)})
MERGE (l)-[:HAS]->(a);

// # # # # # # #
// import hosts
// # # # # # # #
LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.host_id IS NOT NULL
MERGE (h:Host {host_id: row.host_id})
ON CREATE SET 
    h.name            = row.host_name,
    h.verifications   = row.host_verifications,
    h.listings_count  = toInteger(row.host_listings_count),
    h.acceptance_rate = toFloat(row.host_acceptance_rate),
    h.host_since      = row.host_since,
    h.url             = row.host_url,
    h.response_rate   = row.host_response_rate,
    h.superhost       = CASE WHEN row.host_is_superhost = "t" THEN true ELSE false END,
    h.location        = row.host_location,
    h.verified        = CASE WHEN row.host_identity_verified = "t" THEN true ELSE false END,
    h.image           = row.host_picture_url


LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.host_id IS NOT NULL
MATCH (h:Host {host_id: row.host_id})
MATCH (l:Listing {listing_id: row.id})
MERGE (h)-[:HOSTED_BY]->(l);

// # # # # # # # # # # # #
// import reviews & users
// # # # # # # # # # # # #
LOAD CSV WITH HEADERS FROM "file:///reviews.csv" AS row FIELDTERMINATOR ','
WITH row LIMIT 100000

MERGE (u:User {user_id: row.reviewer_id})
SET u.name = row.reviewer_name

MERGE (r:Review {review_id: row.id})
SET r.date = row.date

WITH row, u, r
MATCH (l:Listing {listing_id: row.listing_id})
MERGE (u)-[:WROTE]->(r)
MERGE (r)-[:REVIEWS]->(l);

// LOAD CSV WITH HEADERS FROM "file:///reviews.csv" AS row FIELDTERMINATOR ',' 
// WITH row LIMIT 10
// MERGE (u:User {user_id: row.reviewer_id})
// SET u.name = row.reviewer_name;

// LOAD CSV WITH HEADERS FROM "file:///reviews.csv" AS row FIELDTERMINATOR ','
// WITH row LIMIT 10
// MERGE (r:Review {review_id: row.id})
// SET r.date = row.date;

// LOAD CSV WITH HEADERS FROM "file:///reviews.csv" AS row FIELDTERMINATOR ','
// WITH row LIMIT 10
// MATCH (u:User {user_id: row.reviewer_id})
// WITH u, row
// MATCH (r:Review {review_id: row.id})
// WITH u, r, row
// MATCH (l:Listing {listing_id: row.listing_id})
// MERGE (u)-[:WROTE]->(r)
// MERGE (r)-[:REVIEWS]->(l);
