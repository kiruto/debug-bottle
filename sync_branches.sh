#!/bin/bash
git push origin 1.0.1

git checkout -f v22/1.0.1
git pull origin v22/1.0.1
git merge 1.0.1
git push origin v22/1.0.1

git checkout -f v23/1.0.1
git pull origin v23/1.0.1
git merge 1.0.1
git push origin v23/1.0.1

git checkout 1.0.1