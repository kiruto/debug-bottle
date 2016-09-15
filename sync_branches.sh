#!/bin/bash
git push origin 1.0.0

git checkout -f v22/1.0.0
git pull origin v22/1.0.0
git merge 1.0.0
git push origin v22/1.0.0

git checkout -f v23/1.0.0
git pull origin v23/1.0.0
git merge 1.0.0
git push origin v23/1.0.0

git checkout 1.0.0