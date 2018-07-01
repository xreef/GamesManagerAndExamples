@echo off

echo Making LoadersTests jar...
jar cvmf mainClass.txt LoadersTests.jar *.class Sounds Images
echo.

echo Indexing jar...
jar i LoadersTests.jar
echo done
