#!/bin/sh
if [ -z $KNIMEDIR ]; then
  echo "'KNIMEDIR' is not defined! Error.";
  exit;
else

$KNIMEDIR/knime -application org.knime.testing.KNIME_TESTING_APPLICATION -nosplash -root workspace -pattern ".*" 

