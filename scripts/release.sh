#!/usr/bin/env bash
DO_NOT_PROMPT_FOR_ANY_VALUE=--batch-mode
(cd ../ && mvn release:prepare release:perform $DO_NOT_PROMPT_FOR_ANY_VALUE)
