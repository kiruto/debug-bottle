#!/bin/bash
git push origin 1.1.x

git checkout -f v22/1.1.x
git pull origin v22/1.1.x
git merge 1.1.x
git push origin v22/1.1.x

git checkout -f v23/1.1.x
git pull origin v23/1.1.x
git merge 1.1.x
git push origin v23/1.1.x

git checkout 1.1.x