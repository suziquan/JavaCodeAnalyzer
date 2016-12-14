rm -frd d:/maven-repo/repository/edu/nju/JavaCodeAnalyzer
git add -A
git commit -m "modified"
git push
mvn clean
mvn deploy -DaltDeploymentRepository=suziquan-mvn-repo::default::file:d:/maven-repo/repository
cd d:/maven-repo
git add -A
git commit -m "deploy"
git push