# stream-processing

A clojure stream processing program hacked together for bbuzz stream hackathon.

Reads tweets from a database or a socket. Calulates top-k hashtags for the
entire set and also takes samples and uses standard score to try and find
trends.

## Installation

Install stream lib

    git clone https://github.com/addthis/stream-lib.git
    mvn install:install-file -Dfile=target/stream-2.7.0-SNAPSHOT.jar -DpomFile=pom.xml

## Running

    lein run

