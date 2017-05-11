# Australian City Analytics

COMP90024 Cluster and Cloud Computing (2017 Semester 1)

Team 38

<b>We aimed on real-time response with very low latency!

Every time you refresh the page / click the "update" button, you always get the  latest statistics from CouchDB!</b>

Featuring:

* Twitter harvesting by streaming and search api
* CouchDB storage
* Data processing with CouchDB built-in MapReduce, NLP and AURIN support
* Web application as front-end based on Spring
* Boto & Ansible deployment

Project Structure:

* Deployment - Boto & Ansible deployment scripts
* Lighter - Some useful code snippets there (e.g. reading AURIN data files and upload them to CouchDB)
* RegularCurl - A tiny program aiming to resist CouchDB Lazy-MapReduce
* TweetHarvester - Tweet harvesting (pre-processing) and data (already in CouchDB) post-processing
* WebApplication - Web application based on Spring
