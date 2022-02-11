#!/bin/bash

# abort on error
set -e
# print commands
set -x


# import xray
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=xray -Dxray.reportFile=specific_reports/xray.json

# import xray, multipart
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=xray -Dxray.reportFile=specific_reports/xray.json -Dxray.testExecInfoJson=specific_reports/testExecInfo.json

# import junit
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=junit -Dxray.reportFile=../reports/junit.xml

# import junit, multipart
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=junit -Dxray.reportFile=../reports/junit.xml -Dxray.testExecInfoJson=specific_reports/testExecInfo.json

# import xunit
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=xunit -Dxray.reportFile=../reports/xunit.xml

# import xunit, multipart
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=xunit -Dxray.reportFile=../reports/xunit.xml -Dxray.testExecInfoJson=specific_reports/testExecInfo.json

# import nunit
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=nunit -Dxray.reportFile=../reports/nunit.xml

# import nunit, multipart
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=nunit -Dxray.reportFile=../reports/nunit.xml -Dxray.testExecInfoJson=specific_reports/testExecInfo.json

# import testng
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=testng -Dxray.reportFile=../reports/testng.xml

# import testng, multipart
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=testng -Dxray.reportFile=../reports/testng.xml -Dxray.specific_reports/testExecInfo.json

# import robot
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=robot -Dxray.reportFile=../reports/robot.xml

# import robot, multipart
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=robot -Dxray.reportFile=../reports/robot.xml -Dxray.testExecInfoJson=specific_reports/testExecInfo.json

# import cucumber features
mvn xray:import-features -Dxray.cloud=true -Dxray.inputFeatures=features/ 

# export cucumber features
mvn xray:export-features -Dxray.cloud=true -Dxray.issueKeys=CALC-1 -Dxray.outputDir=features/

# import cucumber results
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=cucumber -Dxray.reportFile=specific_reports/cucumber.json

# import cucumber results, multipart
mvn xray:import-results -Dxray.cloud=true -Dxray.reportFormat=cucumber -Dxray.reportFile=specific_reports/cucumber.json -Dxray.testExecInfoJson=specific_reports/testExecInfo.json