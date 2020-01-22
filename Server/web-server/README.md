### Install Couchbase
```
docker run -d --name couchbase -p 8091-8094:8091-8094 -p 11210:11210 couchbase:community-6.0.0
```
- follow instruction from https://hub.docker.com/_/couchbase?tab=description.
After cluster setup is done, create bucket, call it `assignments`. You may tick checkbox "enable flush", it's useful for test purposes to clean bucket quickly.
- create the following indexes:
```n1ql
CREATE PRIMARY INDEX ix_assignmentAttempt_primary ON `assignments` WITH {"defer_build": true};
CREATE INDEX ix_assignmentAttempt_projectName ON `assignments`(projectName) WITH {"defer_build": true};
BUILD INDEX ON `assignments`(ix_assignmentAttempt_primary, ix_assignmentAttempt_projectName);
```