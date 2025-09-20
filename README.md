# Airbnb

## 1 Getting Started

To run the application `Neo4j`, follow these steps:
1. Install Docker compose in your system
2. Clone this repository to your local machine 
```bash 
git clone <repository-url>
```
3. Navigate to the root project directory 
```bash 
cd <project-directory>
```
4. Build and start the Docker containers using Docker Compose 
```bash 
docker-compose up
```
5. To stop the containers, use the following command 
```bash 
docker-compose down
```

## 2. Clean data

### 2.1 Install virtualenv
```bash
pip install virtualenv 
```

### 2.2 Create virtualenv
```bash
virtualenv env_name 
```

### 2.3 Activate virtualenv
```bash
source env_name/bin/activate
```

### 2.4 Install packages
```bash
pip install pandas
```

### 2.5 Run Python script
```bash
python main.py --url=http://data.insideairbnb.com/canada/qc/montreal/2023-05-10/data/listings.csv.gz --output=data/listings.csv
```
<br />
<br />

## 3 csvkit (optional)
### 3.1 Install csvkit
```bash
sudo apt-get update -y
sudo apt-get install -y csvkit
```

### 3.2 Airbnb: Downlaod listings
```bash
wget -O data/listings.csv.gz http://data.insideairbnb.com/canada/qc/montreal/2023-05-10/data/listings.csv.gz
gunzip data/listings.csv.gz
```

### 3.3 Airbnb: Prepare listings 
```bash
csvcut -C description,host_about,amenities,host_verifications data/listings.csv | csvgrep -c host_location -r ".*Montreal.*" > data/listings-clean.csv
csvstat data/listings-clean.csv --count
```

### 3.4 Copy listings into Neo4j container
```bash
docker cp data/listings-clean.csv <container_id>:/var/lib/neo4j/import 
```

### 3.5 Airbnb: Download reviews
```bash
wget -O data/reviews.csv.gz http://data.insideairbnb.com/canada/qc/montreal/2023-05-10/data/reviews.csv.gz
gunzip data/reviews.csv.gz
```

### 3.6 Airbnb: Prepare reviews
```bash
csvcut -C comments data/reviews.csv > data/reviews-clean.csv
csvstat data/reviews-clean.csv --count 
```

### 3.7 Copy reviews into Neo4j container
```bash
docker cp data/reviews-clean.csv <container_id>:/var/lib/neo4j/import 
```
<br />
<br />

## 4. Cypher: Listings
### 4.1 Create index
```bash
CREATE INDEX FOR (h:Host) ON h.host_id;
CREATE INDEX FOR (a:Amenity) ON a.name;
CREATE INDEX FOR (l:Listing) ON l.listing_id;
CREATE INDEX FOR (n:Neighborhood) ON n.neighborhood_id;
```
### 4.2 Create constraints (optional)
```bash
CREATE CONSTRAINT FOR (h:Host) REQUIRE h.host_id IS UNIQUE;
CREATE CONSTRAINT FOR (a:Amenity) REQUIRE a.name IS UNIQUE;
CREATE CONSTRAINT FOR (l:Listing) REQUIRE l.lising_id IS UNIQUE;

SHOW CONSTRAINTS;       # retrieves constraint_44079ef1
DROP CONSTRAINT constraint_44079ef1
```

### 4.3 Create Listing
```bash
LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.id IS  NOT NULL
MERGE (l:Listing {listing_id: row.id})
ON CREATE SET 
    l.name                        = row.name,
    l.latitude                    = toFloat(row.latitude),
    l.longitude                   = toFloat(row.longitude),
    l.neighborhood_cleansed       = row.neighborhood_cleansed,
    l.reviews_per_month           = toFloat(row.reviews_per_month),
    l.instant_bookable            = row.instant_bookable,
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
```

### 4.4 Create Host
```bash
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
    h.superhost       = row.host_is_superhost,
    h.location        = row.host_location,
    h.verified        = row.host_identity_verified,
    h.image           = row.host_picture_url
```

### 4.5 Create Neighborhood
```bash
LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.id IS  NOT NULL
MATCH (l:Listing {listing_id: row.id})
MERGE (n:Neighborhood {neighborhood_id: COALESCE(row.neighbourhood_cleansed, "NA")})
SET n.name = row.neighbourhood
MERGE (l)-[:IN_NEIGHBORHOOD]->(n);
```

### 4.6 Create Amenity
```bash
LOAD CSV WITH HEADERS FROM "file:///listings.csv" AS row FIELDTERMINATOR ','
WITH row WHERE row.id IS  NOT NULL
MATCH (l:Listing {listing_id: row.id})
UNWIND split(row.amenities,'|') as amenity
MERGE (a:Amenity {name: trim(amenity)})
MERGE (l)-[:HAS]->(a);
```
<br />
<br />

## 5. Cypher: Reviews
### 5.1 Create index
```bash
CREATE INDEX FOR (u:User) ON u.user_id;
CREATE INDEX FOR (r:Review) ON r.review_id;
```
### 5.2 Create constraints (optional)
```bash
CREATE CONSTRAINT FOR (u:User) REQUIRE u.user_id IS UNIQUE;
CREATE CONSTRAINT FOR (r:Review) REQUIRE r.review_id IS UNIQUE;
```
### 5.3 Create User
```bash
LOAD CSV WITH HEADERS FROM "file:///reviews.csv" AS row FIELDTERMINATOR ',' 
WITH row LIMIT 100
MERGE (u:User {user_id: row.reviewer_id})
SET u.name = row.reviewer_name;
```

### 5.4 Create Review
```bash
LOAD CSV WITH HEADERS FROM "file:///reviews.csv" AS row FIELDTERMINATOR ','
WITH row LIMIT 100
MERGE (r:Review {review_id: row.id})
SET r.date = row.date;
```

### 5.5 Create relations
```bash
LOAD CSV WITH HEADERS FROM "file:///reviews.csv" AS row FIELDTERMINATOR ','
WITH row LIMIT 100
MATCH (u:User {user_id: row.reviewer_id})
WITH u, row
MATCH (r:Review {review_id: row.id})
WITH u, r, row
MATCH (l:Listing {listing_id: row.listing_id})
MERGE (u)-[:WROTE]->(r)
MERGE (r)-[:REVIEWS]->(l);
```
<br />
<br />

## 6. Cypher: Recommendation
### 6.1 Analysis
```bash
MATCH (l:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
WITH n.neighborhood_id AS neighborhood, COALESCE(AVG(l.review_scores_rating), 0) AS average_rating
WHERE average_rating > 0
RETURN neighborhood, average_rating
ORDER BY average_rating DESC
LIMIT 10
```

```bash
MATCH (u:User {user_id: "97763164"})
RETURN count(*) AS user_data_count
```

```bash
MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
RETURN n.neighborhood_id, COUNT(*) AS visit_count
ORDER BY visit_count DESC
```

```bash
MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:HAS]->(a:Amenity)
RETURN a.name, COUNT(*) AS amenity_count
ORDER BY amenity_count DESC
LIMIT 10
```

```bash
MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n:Neighborhood)
WITH u, COLLECT(DISTINCT n) AS neighborhoods
MATCH (u2:User)-[:WROTE]->(:Review)-[:REVIEWS]->(:Listing)-[:IN_NEIGHBORHOOD]->(n2:Neighborhood)
WHERE n2 IN neighborhoods AND u2 <> u
RETURN DISTINCT u2.user_id
LIMIT 10
```

```bash
MATCH (u:User {user_id: "97763164"})-[:WROTE]->(:Review)-[:REVIEWS]->(l:Listing)-[:IN_NEIGHBORHOOD]->(:Neighborhood)
WITH u, COLLECT(DISTINCT l) AS user_listings
MATCH (u)-[:WROTE]->(:Review)-[:REVIEWS]->(l2:Listing)-[:IN_NEIGHBORHOOD]->(:Neighborhood)
WHERE l2 IN user_listings
MATCH (l2)-[:HAS]->(a:Amenity)
RETURN DISTINCT a.name AS amenity
LIMIT 10
```

```bash
MATCH (u:User)-[:WROTE]->(r:Review)-[:REVIEWS]->(l:Listing)
WITH u.user_id AS user_id, COUNT(r) AS reviewCount
RETURN user_id, reviewCount
ORDER BY reviewCount DESC
LIMIT 10
```

### 6.2 Recommendations

```bash
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
```
### References
http://insideairbnb.com/get-the-data    
https://csvkit.readthedocs.io/en/latest/    
https://csvkit.readthedocs.io/en/latest/scripts/csvcut.html     
https://csvkit.readthedocs.io/en/latest/scripts/csvstat.html  
https://csvkit.readthedocs.io/en/latest/scripts/csvgrep.html   
