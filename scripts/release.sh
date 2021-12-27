#!/usr/bin/env bash

(cd ../ && mvn release:prepare release:perform -P local-release --batch-mode -DignoreSnapshots=true)
