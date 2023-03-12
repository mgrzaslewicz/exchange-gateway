#!/usr/bin/env bash

if [[ -z ${VERSION} ]]; then
    echo "VERSION not set. Please run the script with VERSION variable set: VERSION=x.x.x sudo -E ./install.sh";
    exit 101;
else
    VERSION="exchange-gateway-$VERSION"
fi

cd ..
git checkout tags/${VERSION}
mvn clean install
git checkout master
