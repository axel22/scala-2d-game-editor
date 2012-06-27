#!/usr/bin/sh

JAPNG=`dirname $0`/japng.sh
FILE=$1
NUMFRAME=$2

FRAMES= 

for (( c=0; c<$NUMFRAME; c++ ))
do
  FRAMES=$FRAMES$1"000"$c".png, "
done


COMMAND="$JAPNG -out $1.png -frames $FRAMES"


$COMMAND


for (( c=0; c<$NUMFRAME; c++ ))
do
  rm $1"000"$c".png"
done

