[目录](README.md)>项目建设

# 项目建设

## 代码提交
本项目目前支持最低support包appcompat-v7版本为22，使用不同分支提供支持。一般为"v22/版本号"，"v23/版本号"等方式命名分支，主分支直接以版本号命名。
本工程提供了一个基本(bash)脚本[sync_branches.sh](../../sync_branches.sh)同步各分支，若需要将代码同步到不同分支，需先commit后执行脚本。
一般情况下可以提交push request至主分支即可；在支持不同版本support包产生分歧的情况下，需要执行脚本同步，并提交push request到不同分支。
旧support包支持可能不会被永久维护。
