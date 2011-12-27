#!/bin/sh
if [ -z $KNIMEDIR ]; then
  echo "'KNIMEDIR' is not defined! Error.";
  exit;
else

#rm -f $KNIMEDIR/plugins/com.ggasoftware*.jar
#cp ../plugins/com.ggasoftware*.jar $KNIMEDIR/plugins/

$KNIMEDIR/knime -application org.knime.testing.KNIME_TESTING_APPLICATION -nosplash -root workspace -pattern ".*" 

