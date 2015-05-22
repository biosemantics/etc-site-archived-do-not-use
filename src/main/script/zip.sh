#!/bin/bash
pushd $1; zip -r $3 ./$2; popd
