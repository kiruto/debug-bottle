#!/bin/bash
git push origin 1.0.0EAP

git checkout -f v22/1.0.0EAP
git checkout --force 1.0.0EAP components/src/main/kotlin/com/exyui/android/debugbottle/components/VersionCtrl.kt
git merge 1.0.0EAP
git push origin v22/1.0.0EAP

git checkout -f v23/1.0.0EAP
git checkout --force 1.0.0EAP components/src/main/kotlin/com/exyui/android/debugbottle/components/VersionCtrl.kt
git merge 1.0.0EAP
git push origin v23/1.0.0EAP

git checkout 1.0.0EAP