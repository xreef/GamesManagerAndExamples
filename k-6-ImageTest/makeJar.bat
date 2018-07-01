@echo off

echo Making ImagesTests jar...
jar cvmf mainClass.txt ImagesTests.jar *.class Images
echo.

echo Indexing jar...
jar i ImagesTests.jar
echo done
